package com.sakuraio.jenkins.ci.core

import com.sakuraio.jenkins.ci.ext.jenkins.JenkinsParameter
import com.sakuraio.jenkins.ci.factory.SingletonFactory
import com.sakuraio.jenkins.ci.logger.Log

/**
 * <p>AbsOptionsLoader</p>
 *
 * @author nekoimi 2023/02/28
 */
abstract class AbsOptionsLoader implements Initialization, Serializable {
    /**
     * 加载到的配置
     */
    def options

    /**
     * jenkins对象
     */
    def jenkins

    /**
     * 日志对象
     */
    Log log

    /**
     * jenkins参数对象
     */
    JenkinsParameter jenkinsParameter

    /**
     * <p>配置文件资源路径</p>
     *
     * 基于 "resources" 文件夹的相对路径
     * @return
     */
    protected abstract String resourcePath()

    /**
     * <p>初始化配置</p>
     */
    protected abstract void postPropertiesSet();

    @Override
    void initialization(jenkins) {
        this.jenkins = jenkins
        this.log = SingletonFactory.singletonInstance(this.jenkins, Log.class)
        this.jenkinsParameter = SingletonFactory.singletonInstance(this.jenkins, JenkinsParameter.class)
        def resourcePath = resourcePath()
        if (resourcePath) {
            this.log.info("加载配置：${resourcePath}")
            def resourceText = this.jenkins.libraryResource(resourcePath)
            this.options = this.jenkins.readYaml(text: resourceText)
        }
        this.postPropertiesSet()
    }
}
