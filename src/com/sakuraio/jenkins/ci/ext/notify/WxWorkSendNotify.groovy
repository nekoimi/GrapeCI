package com.sakuraio.jenkins.ci.ext.notify


import com.sakuraio.jenkins.ci.core.Context
import com.sakuraio.jenkins.ci.utils.FileUtils

/**
 * <p>企业微信机器人通知</p>
 *
 * 依赖企业微信通知插件：{@see https://github.com/jenkinsci/wxwork-notification-plugin}
 * @author nekoimi 2023/02/28
 */
class WxWorkSendNotify extends AbsSendNotify {

    @Override
    def sendSuccess(Context ctx) {
        def message = jobInfoMessage(ctx, true)
        if (ctx.state.sonarCheck) {
            message.add("代码质量：${ctx.state.sonarCheckIssuesUrl}")
        }
        if (ctx.state.image) {
            message.add("Docker Image：${ctx.state.registryImage}")
        }
        if (ctx.state.artifactBuildPackage) {
            message.add("构建产物：${ctx.state.artifactBuildPackageDownloadUrl}")
        }
        if (ctx.state.artifactComposeDeployPackage) {
            message.add("Compose部署产物：${ctx.state.artifactComposeDeployPackageDownloadUrl}")
        }
        if (ctx.state.artifactHelmDeployPackage) {
            message.add("Helm部署产物：${ctx.state.artifactHelmDeployPackageDownloadUrl}")
        }
        def accessUrls = readAccessUrlFromValuesYaml(ctx)
        for (accessUrl in accessUrls) {
            message.add("访问地址：${accessUrl}")
        }

        // 调用jenkins微信机器人插件API发送企业微信消息
        ctx.jenkins.wxwork(
                robot: "${ctx.globalOptions.wxWorkRobotId}",
                type: 'text',
                atMe: true,
                text: message
        )
    }

    @Override
    def sendFail(Context ctx, Object errorMsg) {
        def message = jobInfoMessage(ctx, false)
        message.add("错误信息：${errorMsg}")
        // 调用jenkins微信机器人插件API发送企业微信消息
        this.jenkins.wxwork(
                robot: ctx.globalOptions.wxWorkRobotId,
                type: 'text',
                atMe: true,
                text: message
        )
    }

    /**
     * 构建任务详细信息
     * @param ctx
     * @param status
     * @return
     */
    private List<String> jobInfoMessage(Context ctx, boolean status) {
        return [
                "构建状态：${status ? '成功' : '失败'}",
                "项目名称：${ctx.buildOptions.name}",
                "项目版本：${ctx.buildOptions.version}",
                "项目简介：${ctx.buildOptions.description}",
                "构建任务：${this.jenkinsParameter.getJobName()}",
                "构建环境：${this.jenkinsParameter.getBuildEnv()}",
                "构建分支：${this.jenkinsParameter.getBuildBranch()}",
        ];
    }

    /**
     * 获取values.yaml配置的访问地址
     * @param ctx
     * @return
     */
    private static List<String> readAccessUrlFromValuesYaml(Context ctx) {
        def valuesYamlPath = FileUtils.moduleAbsPath(ctx, "k8s/${ctx.buildOptions.buildEnv}-values.yaml")
        def accessUrls = []
        if (!FileUtils.exists(valuesYamlPath)) {
            return accessUrls
        }
        def valuesYaml = ctx.jenkins.readYaml(file: valuesYamlPath)
        def hostMapList = valuesYaml?.ingress?.hosts
        if (!hostMapList) {
            return accessUrls
        }

        for (hostMap in hostMapList) {
            def host = hostMap?.host
            if (!host) {
                continue
            }

            def pathMapList = hostMap.paths
            if (!pathMapList) {
                accessUrls.add(host)
            } else {
                for (pathMap in pathMapList) {
                    def path = pathMap?.path
                    if (path == "/") {
                        accessUrls.add(host)
                    } else {
                        accessUrls.add("${host}${path}")
                    }
                }
            }
        }

        return accessUrls
    }
}
