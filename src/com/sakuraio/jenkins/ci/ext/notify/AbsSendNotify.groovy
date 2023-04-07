package com.sakuraio.jenkins.ci.ext.notify

import com.sakuraio.jenkins.ci.ext.jenkins.JenkinsParameter
import com.sakuraio.jenkins.ci.factory.SingletonFactory

/**
 * <p>AbsSendNotify</p>
 *
 * @author nekoimi 2023/02/28
 */
abstract class AbsSendNotify implements ISendNotify {
    /**
     * Jenkins对象
     */
    def jenkins

    /**
     * Jenkins参数对象
     */
    JenkinsParameter jenkinsParameter

    @Override
    void initialization(jenkins) {
        this.jenkins = jenkins
        this.jenkinsParameter = SingletonFactory.singletonInstance(this.jenkins, JenkinsParameter.class)
    }
}
