package com.sakuraio.jenkins.ci.ext.jenkins


import com.sakuraio.jenkins.ci.core.Initialization
/**
 * <p>JenkinsPropertiesGetter</p>
 *
 * @author nekoimi 2023/02/28
 */
abstract class JenkinsPropertiesGetter implements Initialization, Serializable {
    /**
     * Jenkins对象
     */
    def jenkins

    @Override
    void initialization(jenkins) {
        this.jenkins = jenkins
    }

    /**
     * 获取jenkins环境变量参数
     * @param name
     * @return
     */
    protected def getEnvParameter(name) {
        return this.jenkins.env["${name}"]
    }

    /**
     * 获取jenkins构建参数(需要配置参数化构建)
     * @param name
     * @return
     */
    protected def getBuildParameter(name) {
        return this.jenkins.params["${name}"]
    }
}
