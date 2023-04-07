package com.sakuraio.jenkins.ci

import com.sakuraio.jenkins.ci.config.BuildOptions
import com.sakuraio.jenkins.ci.config.ModuleOptions
import com.sakuraio.jenkins.ci.core.Context
import com.sakuraio.jenkins.ci.core.IStage
import com.sakuraio.jenkins.ci.core.Initialization
import com.sakuraio.jenkins.ci.ext.jenkins.JenkinsParameter
import com.sakuraio.jenkins.ci.ext.notify.WxWorkSendNotify
import com.sakuraio.jenkins.ci.factory.SingletonFactory
import com.sakuraio.jenkins.ci.logger.Log

/**
 * <p>GrapeCI</p>
 *
 * @author nekoimi 2022/11/18
 */
class GrapeCI implements Initialization, Serializable {
    private Object jenkins
    private Log log

    @Override
    void initialization(Object jenkins) {
        this.jenkins = jenkins;
        this.log = SingletonFactory.singletonInstance(this.jenkins, Log.class)
    }

    /**
     * 初始化工作环境
     */
    private void initWorkspace() {
        // 构建环境信息
        def jenkinsParameter = SingletonFactory.singletonInstance(this.jenkins, JenkinsParameter.class)
        // banner
        this.log.banner(jenkinsParameter)

        this.jenkins.stage("Clean workspace") {
            this.jenkins.cleanWs()
        }

        this.jenkins.stage("Checkout") {
            this.jenkins.checkout([
                    $class           : 'GitSCM',
                    branches         : [[name: jenkinsParameter.getBuildBranch()]],
                    userRemoteConfigs: this.jenkins.scm.userRemoteConfigs
            ])
        }
    }

    /**
     * 执行
     */
    void run() {
        // 初始化工作目录
        this.initWorkspace()
        // 初始化CI上下文
        def context = SingletonFactory.singletonInstance(this.jenkins, Context.class)
        def buildOptions = SingletonFactory.singletonInstance(this.jenkins, BuildOptions.class)
        def moduleOptions = SingletonFactory.singletonInstance(this.jenkins, ModuleOptions.class)
        def notify = SingletonFactory.singletonInstance(this.jenkins, WxWorkSendNotify.class)
        // ---------------------------------
        // 执行每个模块的构建流程
        for (def mod in moduleOptions.modules) {
            context = context.withModule(mod)
            def runnable = false
            def jenkinsBooleanParameter = mod?.booleanParam
            if (jenkinsBooleanParameter) {
                runnable = this.jenkins.params["${jenkinsBooleanParameter}"] as Boolean || jenkinsBooleanParameter == Const.ALWAYS_RUNNABLE
            }
            for (stage in buildOptions.stages) {
                this.log.info("Stage: ${stage}")
                try {
                    def stageClazz = this.class.classLoader.loadClass("com.sakuraio.jenkins.ci.stage.${stage}")
                    if (!stageClazz) {
                        this.log.fail("The stage ${stage} is not supported. Cannot load the stage class!")
                        break
                    }
                    def stageInstance = SingletonFactory.singletonInstance(this.jenkins, stageClazz)
                    if (!stageInstance) {
                        this.log.fail("The stage ${stage} is not supported. Cannot get the stage instance!")
                        break
                    }
                    def stageInst = stageInstance as IStage
                    this.jenkins.stage("${stageInst.title()}: ${mod?.name}") {
                        if (runnable) {
                            stageInst.execute(context)
                        } else {
                            this.log.info("${stageInst.title()}: ${mod?.name} not enabled!")
                        }
                    }
                } catch (Exception e) {
                    notify.sendFail(context, e.message)
                    this.log.fail(e.message)
                }
            }
        }
    }
}
