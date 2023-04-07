package com.sakuraio.jenkins.ci.utils

import com.sakuraio.jenkins.ci.config.properties.KubernetesProperties
/**
 * <p>SshUtils</p>
 *
 * @author nekoimi 2023/02/13
 */
class SshUtils {

    private SshUtils() {
    }

    /**
     * k8s远程执行
     * @param jenkins
     * @param callback
     */
    static void remoteK8sExec(jenkins, KubernetesProperties k8sOptions, Closure callback) {
        def remote = [:]
        remote.name = "${k8sOptions.host}"
        remote.host = "${k8sOptions.host}"
        remote.port = 22
        remote.allowAnyHosts = true
        jenkins.withCredentials([jenkins.sshUserPrivateKey(
                credentialsId: "${k8sOptions.credentials}",
                usernameVariable: "CICD_REMOTE_EXEC_USERNAME",
                keyFileVariable: "CICD_REMOTE_EXEC_IDENTITY_KEY_FILE")]) {
            remote.user = jenkins.CICD_REMOTE_EXEC_USERNAME
            remote.identityFile = jenkins.CICD_REMOTE_EXEC_IDENTITY_KEY_FILE
            jenkins.sshCommand remote: remote, command: callback.call(k8sOptions)
        }
    }

    /**
     * 远程执行shell
     * @param jenkins
     * @param k8sOptions
     * @param shellResource
     * @param injectArgs
     */
    static void remoteK8sExecShell(jenkins, KubernetesProperties k8sOptions, String shellResource, LinkedHashMap<String, String> injectArgs = [:]) {
        remoteK8sExec(jenkins, k8sOptions, { k8s ->
            return FileUtils.readResourceContentInjectArgs(jenkins, shellResource, injectArgs)
        })
    }
}
