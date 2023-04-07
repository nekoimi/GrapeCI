package com.sakuraio.jenkins.ci.stage

import com.sakuraio.jenkins.ci.core.IStage
import com.sakuraio.jenkins.ci.core.Context
import com.sakuraio.jenkins.ci.utils.FileUtils
import com.sakuraio.jenkins.ci.utils.GitUtils
import com.sakuraio.jenkins.ci.utils.LockUtils

/**
 * <p>HelmChart</p>
 *
 * @author nekoimi 2023/02/10
 */
class HelmChart implements IStage {

    /**
     * helm配置模板
     */
    private static final def HELM_TEMPLATE = "helm-deploy-template"

    @Override
    String title() {
        return "Helm Chart"
    }

    @Override
    void execute(Context ctx) {
        def image = ctx.state.image as String
        if (!image) {
            ctx.log.warning("缺少image信息 -> ，忽略helm配置更新")
            return
        }

        def k8sValueYamlFile = FileUtils.moduleAbsPath(ctx, "k8s/${ctx.buildOptions.buildEnv}-values.yaml")
        if (!FileUtils.exists(k8sValueYamlFile)) {
            ctx.log.warning("缺少k8s部署配置文件 -> ${k8sValueYamlFile}，忽略helm配置更新")
            return
        }

        def deployHelmGit = ctx.buildOptions?.kubernetes?.deployHelmGit
        if (!deployHelmGit) {
            ctx.log.warning("缺少helm仓库配置，忽略helm配置更新")
            return
        }

        LockUtils.run(ctx, deployHelmGit, {
            def helmCacheFolder = FileUtils.moduleAbsPath(ctx, ".helm-cache")
            // 下载helm仓库配置
            GitUtils.pull(ctx, deployHelmGit, helmCacheFolder)

            // 项目的helm配置路径
            def moduleHelmPath = "${helmCacheFolder}/${ctx.buildOptions.name}"
            if (!FileUtils.exists(moduleHelmPath)) {
                // helm模板路径
                def templateHelmPath = "${helmCacheFolder}/${HELM_TEMPLATE}"
                if (FileUtils.exists(templateHelmPath)) {
                    FileUtils.mkdirs(moduleHelmPath)
                    // 创建helm部署配置
                    createModuleHelmChart(ctx, templateHelmPath, moduleHelmPath)
                } else {
                    ctx.log.warning("缺少helm部署模板 -> ${templateHelmPath}，忽略helm配置生成")
                }
            }

            if (FileUtils.exists(moduleHelmPath)) {
                // 更新部署image信息
                updateModuleHelmChart(ctx, moduleHelmPath)
            } else {
                ctx.log.warning("缺少helm部署模板 -> ${moduleHelmPath}，忽略helm配置更新")
            }

            // 推送helm配置
            GitUtils.push(ctx, helmCacheFolder)
        })
    }

    /**
     * 根据模板创建helm配置
     * @param ctx
     * @param templateHelmPath
     * @param moduleHelmPath
     */
    private static void createModuleHelmChart(Context ctx, String templateHelmPath, String moduleHelmPath) {
        ctx.log.info("创建部署helm配置: ${moduleHelmPath}")
        // 复制模板
        FileUtils.copy(ctx.log, templateHelmPath, moduleHelmPath, { String from, String to ->
            FileUtils.rewriteWithFileInjectArgs(from, to, [
                    "${HELM_TEMPLATE}": "${ctx.buildOptions.name}"
            ])
        })

        // 更新chart信息
        FileUtils.rewriteWithResourceInjectArgs(ctx.jenkins, "template/helm/Chart.yaml", "${moduleHelmPath}/Chart.yaml", [
                REPLACE_NAME       : "${ctx.buildOptions.name}",
                REPLACE_VERSION    : "${ctx.buildOptions.version}",
                REPLACE_DESCRIPTION: "${ctx.buildOptions.description}"
        ])
        ctx.log.info("更新chart信息: ${moduleHelmPath}/Chart.yaml")
    }

    /**
     * 更新部署image配置
     * @param ctx
     * @param helmChartPath
     */
    private static void updateModuleHelmChart(Context ctx, helmChartPath) {
        def sourceValuesFilePath = FileUtils.moduleAbsPath(ctx, "k8s/${ctx.buildOptions.buildEnv}-values.yaml")
        FileUtils.rewriteWithFileInjectArgs(sourceValuesFilePath, "${helmChartPath}/values.yaml")
        ctx.log.info("使用values配置: Copy ${sourceValuesFilePath} to ${helmChartPath}/values.yaml")

        FileUtils.writeWithResourceInjectArgs(ctx.jenkins, "template/helm/image.values.yaml", "${helmChartPath}/values.yaml", [
                REPLACE_REPOSITORY: ctx.state.image as String,
                REPLACE_TAG       : ctx.state.imageTag as String
        ])
        ctx.log.info("更新部署image信息: ${ctx.state.registryImage} -> ${helmChartPath}/values.yaml")
    }
}
