package com.sakuraio.jenkins.ci.stage

import com.sakuraio.jenkins.ci.core.Context
import com.sakuraio.jenkins.ci.core.IStage
import com.sakuraio.jenkins.ci.utils.FileUtils

/**
 * <p>DockerCompose</p>
 *
 * @author nekoimi 2023/02/10
 */
class DockerCompose implements IStage {

    /**
     * docker-compose配置模板
     */
    private static final def DOCKER_COMPOSE_TEMPLATE = "docker-compose.template.yaml"

    @Override
    String title() {
        return "Docker Compose"
    }

    @Override
    void execute(Context ctx) {
        def registryImage = ctx.state.registryImage as String
        if (!registryImage) {
            ctx.log.warning("缺少image信息 -> ，忽略Compose配置更新")
            return
        }

        def composeCacheFolder = FileUtils.moduleAbsPath(ctx, ".compose-cache")
        def cf = new File(composeCacheFolder)
        cf.exists() || cf.mkdirs()
        // 生成docker-compose.yaml
        FileUtils.rewriteWithResourceInjectArgs(ctx.jenkins, "template/compose/docker-compose.yaml", "${composeCacheFolder}/docker-compose.yaml", [
                REPLACE_NAME: "${ctx.buildOptions.name}",
                REPLACE_IMAGE: registryImage
        ])
        ctx.log.info("Compose配置: ${composeCacheFolder}/docker-compose.yaml 创建完成")
    }
}
