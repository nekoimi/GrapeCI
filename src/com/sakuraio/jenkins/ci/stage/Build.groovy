package com.sakuraio.jenkins.ci.stage


import com.sakuraio.jenkins.ci.core.Context
import com.sakuraio.jenkins.ci.core.IStage
/**
 * <p>使用容器image来构建当前项目</p>
 *
 * @author nekoimi 2023/02/10
 */
class Build implements IStage {

    @Override
    String title() {
        return "Build"
    }

    @Override
    void execute(Context ctx) {
        if (ctx.state.isMultiModule && ctx.state.isBuild) {
            ctx.log.info("多模块项目已经构建过，跳过本次构建")
            return
        }
        def image = ctx.buildOptions?.image
        if (image) {
            def commands = ctx.buildOptions?.commands
            dockerBuildExecute(ctx, image, commands)
            // 构建状态
            ctx.state.isBuild = true
        }
    }

    /**
     * 使用docker执行构建命令
     * @param ctx
     * @param image
     * @param commands
     */
    static def dockerBuildExecute(Context ctx, image, List<String> commands = []) {
        def options = [
                "--rm",
                "-w /workspace",
                "-e TZ=Asia/Shanghai",
                "-v ${ctx.globalOptions.jenkinsHomeVolume}/workspace/${ctx.jenkins.env.JOB_NAME}:/workspace",
                "-v /root/.m2:/root/.m2",
                "-v /root/.gradle:/root/.gradle",
                "-v /root/.go:/root/.go",
                "-v /root/.composer:/root/.composer",
                "-v /root/.npm:/root/.npm",
                "-v /root/.node-gyp:/root/.node-gyp"
        ]
        for (command in commands) {
            ctx.jenkins.sh("docker run ${options.join(" ")} ${image} ${command}")
        }
    }
}
