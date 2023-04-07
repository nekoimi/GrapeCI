package com.sakuraio.jenkins.ci.config.properties

/**
 * <p>DockerProperties</p>
 *
 * @author nekoimi 2023/02/10
 */
class DockerProperties {
    /**
     * docker镜像服务地址
     */
    def registry

    /**
     * docker凭据ID，在"jenkins凭据管理"设置
     */
    def credentials

    /**
     * docker推送仓库地址
     */
    def repository

    /**
     * docker-compose部署配置存储git仓库地址
     */
    def dockerComposeGit
}
