package com.sakuraio.jenkins.ci.stage

import com.sakuraio.jenkins.ci.core.Context
import com.sakuraio.jenkins.ci.core.IStage
import com.sakuraio.jenkins.ci.utils.DockerUtils

/**
 * <p>PushImage</p>
 *
 * @author nekoimi 2023/02/10
 */
class PushImage implements IStage {

    @Override
    String title() {
        return "Push Image"
    }

    @Override
    void execute(Context ctx) {
        if (!ctx.state.image) {
            ctx.log.info("image信息不存在，忽略image推送")
            return
        }

        def dockerRegistryHost = DockerUtils.parseRegistryHost(ctx.buildOptions.docker.registry)
        ctx.jenkins.docker.withRegistry("${dockerRegistryHost}", "${ctx.buildOptions.docker.credentials}") {
            ctx.log.info("Push Image: ${ctx.state.registryImage}")
            ctx.jenkins.docker.image("${ctx.state.registryImage}").push()
        }
    }
}
