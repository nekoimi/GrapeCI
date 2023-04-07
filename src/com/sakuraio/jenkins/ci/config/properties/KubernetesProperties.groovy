package com.sakuraio.jenkins.ci.config.properties

/**
 * <p>KubernetesProperties</p>
 *
 * @author nekoimi 2023/02/10
 */
class KubernetesProperties {
    /**
     * k8s api server地址
     */
    def host

    /**
     * k8s api server节点访问凭据ID，在"jenkins凭据管理"设置
     * 证书生成：ssh-keygen -t rsa -b 4096
     */
    def credentials

    /**
     * k8s服务器工作目录
     */
    def workspace

    /**
     * helm部署命名空间
     */
    def deployNamespace

    /**
     * helm部署配置存储git仓库地址
     */
    def deployHelmGit
}
