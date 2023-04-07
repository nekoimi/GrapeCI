package com.sakuraio.jenkins.ci.config

import com.sakuraio.jenkins.ci.core.AbsOptionsLoader

/**
 * <p>全局配置</p>
 *
 * @author nekoimi 2023/02/28
 */
class GlobalOptions extends AbsOptionsLoader {

    /**
     * jenkins home 实际路径 (使用docker运行jenkins为jenkins home挂在宿主机的绝对路径)
     */
    def jenkinsHomeVolume

    /**
     * git凭据ID
     */
    def gitCredentials

    /**
     * 构建产物上传保存服务
     * artifact s3 storage host
     * 目前使用兼容s3的存储 -> Minio
     */
    def artifactS3Host

    /**
     * artifact s3 storage bucket
     */
    def artifactS3Bucket

    /**
     * Sonar Check 配置
     */
    def sonarHost

    /**
     * Sonar Token
     */
    def sonarToken

    /**
     * 微信机器人通知配置ID
     */
    def wxWorkRobotId

    /**
     * 构建产物搜索列表
     */
    List<String> buildOutput = new ArrayList<>()

    @Override
    protected String resourcePath() {
        return "global.yaml"
    }

    @Override
    protected void postPropertiesSet() {
        this.jenkinsHomeVolume = this.options?.jenkinsHomeVolume
        this.gitCredentials = this.options?.gitCredentials
        this.artifactS3Host = this.options?.artifactS3Host
        this.artifactS3Bucket = this.options?.artifactS3Bucket
        this.sonarHost = this.options?.sonarHost
        this.sonarToken = this.options?.sonarToken
        this.wxWorkRobotId = this.options?.wxWorkRobotId
        def bos = this.options?.buildOutput
        if (bos) {
            this.buildOutput = bos as List
        }
    }
}
