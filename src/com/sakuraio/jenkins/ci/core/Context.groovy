package com.sakuraio.jenkins.ci.core

import com.sakuraio.jenkins.ci.config.BuildOptions
import com.sakuraio.jenkins.ci.config.GlobalOptions
import com.sakuraio.jenkins.ci.factory.SingletonFactory
import com.sakuraio.jenkins.ci.logger.Log
/**
 * <p>Context</p>
 *
 * @author nekoimi 2023/02/10
 */
class Context implements Initialization, Serializable {
    /**
     * jenkins上下文
     */
    def jenkins

    /**
     * 日志对象
     */
    Log log

    /**
     * 配置
     */
    GlobalOptions globalOptions

    /**
     * 构建环境参配置
     */
    BuildOptions buildOptions

    /**
     * 全局构建状态
     */
    State state

    @Override
    void initialization(Object jenkins) {
        this.jenkins = jenkins
        this.log = SingletonFactory.singletonInstance(this.jenkins, Log.class)
        this.globalOptions = SingletonFactory.singletonInstance(this.jenkins, GlobalOptions.class)
        this.buildOptions = SingletonFactory.singletonInstance(this.jenkins, BuildOptions.class)
        this.state = SingletonFactory.singletonInstance(this.jenkins, State.class)
    }

    /**
     * @param jenkins
     * @param log
     * @param globalOptions
     * @param buildOptions
     * @return
     */
    Context withModule(Map map) {
        if (map?.name) {
            this.buildOptions.name = map.name
        }
        if (map?.path) {
            def modulePath = map.path as String
            if (modulePath.length() > 0) {
                this.buildOptions.modulePath = modulePath
            }
        }
        if (map?.description) {
            this.buildOptions.description = map.description
        }
        return this
    }
}
