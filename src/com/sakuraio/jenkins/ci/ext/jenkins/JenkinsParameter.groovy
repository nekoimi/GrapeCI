package com.sakuraio.jenkins.ci.ext.jenkins

import com.sakuraio.jenkins.ci.Const

/**
 * <p>JenkinsParameter</p>
 *
 * @author nekoimi 2023/02/28
 */
class JenkinsParameter extends JenkinsPropertiesGetter {


    /**
     * 获取构建环境
     * 需要配置参数化构建：{@link Const#JENKINS_PARAMETER_BUILD_ENV}
     * @return
     */
    def getBuildEnv() {
        return getBuildParameter(Const.JENKINS_PARAMETER_BUILD_ENV)
    }

    /**
     * 获取构建分支
     * 需要配置参数化构建：{@link Const#JENKINS_PARAMETER_BUILD_BRANCH}
     * @return
     */
    def getBuildBranch() {
        def branch = getBuildParameter(Const.JENKINS_PARAMETER_BUILD_BRANCH)
        if (!branch) {
            // 兼容早期版本配置
            branch = getBuildParameter("BRANCH_TAG")
        }
        return branch
    }

    /**
     * 获取当前Job名称
     * @return
     */
    def getJobName() {
        return getEnvParameter("JOB_NAME")
    }
}
