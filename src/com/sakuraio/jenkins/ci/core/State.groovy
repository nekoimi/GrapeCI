package com.sakuraio.jenkins.ci.core

/**
 * <p>State</p>
 *
 * @author nekoimi 2023/02/28
 */
class State implements Serializable {
    // 是否是多模块项目
    def isMultiModule = false
    // 是否构建过
    def isBuild = false

    // docker image 名称
    def image
    // docker image Tag
    def imageTag
    // 带registry信息的docker image名称
    def registryImage

    // sonar 代码质量检查
    def sonarCheck
    // sonar 代码质量检查问题url
    def sonarCheckIssuesUrl

    // 构建产物名称
    def artifactBuildPackage
    // 构建产物下载链接
    def artifactBuildPackageDownloadUrl

    // docker compose 部署产物名称
    def artifactComposeDeployPackage
    // docker compose 部署产物下载地址
    def artifactComposeDeployPackageDownloadUrl

    // helm 部署产物名称
    def artifactHelmDeployPackage
    // helm部署产物下载地址
    def artifactHelmDeployPackageDownloadUrl
}
