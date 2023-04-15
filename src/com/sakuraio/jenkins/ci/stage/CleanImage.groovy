package com.sakuraio.jenkins.ci.stage

import com.sakuraio.jenkins.ci.core.Context
import com.sakuraio.jenkins.ci.core.IStage
import com.sakuraio.jenkins.ci.utils.ShellUtils

/**
 * <p>清除构建生成的docker image，免得占存储空间</p>
 *
 * @author nekoimi 2023-04-15
 */
class CleanImage implements IStage {

    @Override
    String title() {
        return "Clean Image"
    }

    @Override
    void execute(Context ctx) {
        // 清除构建的image
        def image = ctx.state.registryImage
        if (image) {
            ctx.log.debug("清除构建的image - START")
            ShellUtils.exec(ctx, "docker rmi ${image}")
            ctx.log.debug("清除构建的image - END")
        }
    }
}
