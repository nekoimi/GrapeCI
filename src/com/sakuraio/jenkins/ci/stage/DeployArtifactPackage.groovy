package com.sakuraio.jenkins.ci.stage

import com.sakuraio.jenkins.ci.core.Context
import com.sakuraio.jenkins.ci.core.IStage
import com.sakuraio.jenkins.ci.utils.FileUtils
import com.sakuraio.jenkins.ci.utils.ShellUtils
/**
 * <p>DeployArtifactPackage</p>
 *
 * @author nekoimi 2023/02/14
 */
class DeployArtifactPackage implements IStage {

    @Override
    String title() {
        return "Deploy Artifact Package"
    }

    @Override
    void execute(Context ctx) {
        if (!ctx.state.image) {
            ctx.log.warning("缺少image信息 -> ，忽略部署包生成")
            return
        }

        // 打包docker-compose的部署产物
        packageDockerComposeDeployArtifact(ctx)

        // 打包Helm的部署产物
        packageHelmDeployArtifact(ctx)
    }

    /**
     * 打包docker-compose的部署产物
     *
     * docker-compose.yaml + image
     * - :
     * - - image
     * - docker-compose.yaml
     * - deploy.sh
     * @param ctx
     */
    private static void packageDockerComposeDeployArtifact(Context ctx) {
        def moduleComposeFolder = FileUtils.moduleAbsPath(ctx, ".compose-cache")
        if (!FileUtils.exists(moduleComposeFolder)) {
            ctx.log.info("未找到Compose部署配置，跳过打包部署包")
            return
        }

        def deployFolder = FileUtils.moduleAbsPath(ctx, ".compose-deploy")
        def deployTarPackage = "compose-deploy-${ctx.buildOptions.name}-${ctx.jenkins.env.BUILD_ID}.tar.gz"
        def deployFolderTarGz = FileUtils.moduleAbsPath(ctx, deployTarPackage)

        // 导出docker-image
        saveImageToModuleVolume(ctx, "${deployFolder}/image", "docker-image.tar")

        // 复制docker-compose.yaml
        FileUtils.copy(ctx.log, "${moduleComposeFolder}/docker-compose.yaml", "${deployFolder}/docker-compose.yaml")

        // 生成部署脚本
        FileUtils.rewriteWithResourceInjectArgs(ctx.jenkins, "deploy/artifact/docker-compose-deploy.sh", "${deployFolder}/deploy.sh", [
                REPLACE_CHART_NAME      : "${ctx.buildOptions.name}",
                REPLACE_DEPLOY_NAMESPACE: "${ctx.buildOptions?.kubernetes?.deployNamespace}"
        ])

        // 打包
        ShellUtils.exec(ctx, "tar -zcvf ${deployFolderTarGz} -C ${deployFolder} .")

        ctx.log.info("Compose部署产物：${deployFolderTarGz} 打包成功!")
        // 更新环境变量
        ctx.state.artifactComposeDeployPackage = deployTarPackage
    }

    /**
     * 打包Helm的部署产物
     *
     * helm chart + image
     * - :
     * - - chart
     * - - image
     * - deploy.sh
     * @param ctx
     */
    private static void packageHelmDeployArtifact(Context ctx) {
        def moduleHelmFolder = FileUtils.moduleAbsPath(ctx, ".helm-cache/${ctx.buildOptions.name}")
        if (!FileUtils.exists(moduleHelmFolder)) {
            ctx.log.info("未找到helm部署配置，跳过打包部署包")
            return
        }

        def deployFolder = FileUtils.moduleAbsPath(ctx, ".helm-deploy")
        def deployTarPackage = "helm-deploy-${ctx.buildOptions.name}-${ctx.jenkins.env.BUILD_ID}.tar.gz"
        def deployFolderTarGz = FileUtils.moduleAbsPath(ctx, deployTarPackage)

        // 复制chart配置
        FileUtils.copy(ctx.log, moduleHelmFolder, "${deployFolder}/chart")
        // 复制values.yaml
        FileUtils.rewriteWithFileInjectArgs("${moduleHelmFolder}/values.yaml", "${deployFolder}/chart/values.yaml")
        // 导出docker-image
        saveImageToModuleVolume(ctx, "${deployFolder}/image", "docker-image.tar")
        // 生成README.md提示文件
        FileUtils.rewriteWithResourceInjectArgs(ctx.jenkins, "deploy/artifact/helm-deploy-readme.md", "${deployFolder}/README.md")
        // 生成部署脚本
        FileUtils.rewriteWithResourceInjectArgs(ctx.jenkins, "deploy/artifact/helm-deploy.sh", "${deployFolder}/deploy.sh", [
                REPLACE_CHART_NAME      : "${ctx.buildOptions.name}",
                REPLACE_DEPLOY_NAMESPACE: "${ctx.buildOptions?.kubernetes?.deployNamespace}"
        ])

        // 打包
        ctx.log.debug("打包Helm包: ${deployFolder} -> ${deployFolderTarGz}")
        ShellUtils.exec(ctx, "tar -zcvf ${deployFolderTarGz} -C ${deployFolder} .")

        ctx.log.info("Helm部署产物：${deployFolderTarGz} 打包成功!")
        // 更新环境变量
        ctx.state.artifactHelmDeployPackage = deployTarPackage
    }


    /**
     * 保存image
     * @param ctx
     * @param saveModulePath
     * @param name
     */
    private static void saveImageToModuleVolume(Context ctx, String savePath, name) {
        def registryImage = ctx.state.registryImage as String
        if (registryImage) {
            def mf = new File(savePath)
            if (!mf.exists()) {
                mf.mkdirs()
            }

            ShellUtils.exec(ctx, "docker save -o ${savePath}/${name} ${registryImage}")

            ctx.log.info("Save Image: ${registryImage} -> ${savePath}/${name} SUCCESS!")
        }
    }

}
