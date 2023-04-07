package com.sakuraio.jenkins.ci.stage

import com.sakuraio.jenkins.ci.Const
import com.sakuraio.jenkins.ci.core.IStage
import com.sakuraio.jenkins.ci.core.Context
import com.sakuraio.jenkins.ci.ext.web.WebBuildHelper
import com.sakuraio.jenkins.ci.utils.AssertUtils
import com.sakuraio.jenkins.ci.utils.FileUtils
import com.sakuraio.jenkins.ci.utils.ShellUtils

/**
 * <p>构建产物打包
 * 比如：
 *    前端项目使用node的image来构建出的dist产物，需要直接拿到这个dist产物的情况，可以使用这个Stage
 *    Java项目使用maven的image来构建出jar包，需要这届获取这个jar包
 * </p>
 *
 * @author nekoimi 2023/02/10
 */
class BuildArtifactPackage implements IStage {

    @Override
    String title() {
        return "Build Artifact Package"
    }

    @Override
    void execute(Context ctx) {
        def pipeline = AssertUtils.checkPipeline(ctx)

        def outputArtifactFilename = "${ctx.buildOptions.name}-${ctx.buildOptions.version}.${ctx.jenkins.env.BUILD_ID}.tar.gz"

        for (buildOutput in ctx.globalOptions.buildOutput) {
            def buildOutputPath = FileUtils.moduleAbsPath(ctx, buildOutput)
            if (!FileUtils.exists(buildOutputPath)) {
                ctx.log.info("构建产物文件[${buildOutputPath}]不存在，跳过")
                continue
            }
            // 前端项目需要处理环境变量参数
            if (pipeline == Const.WEB_SPEC) {
                // 替换环境变量
                WebBuildHelper.replaceWebOutput(ctx, buildOutputPath)
            }
            // 打包输出
            packageBuildOutput(ctx, buildOutputPath, outputArtifactFilename)
        }
    }


    /**
     * 构建产物打包
     * @param ctx
     * @param outputPath
     * @param outputArtifactFilename
     */
    private static void packageBuildOutput(Context ctx, String buildOutputPath, String outputArtifactFilename) {
        def tarFilepath = FileUtils.moduleAbsPath(ctx, outputArtifactFilename)
        def tarFile = new File(tarFilepath)
        if (tarFile.exists()) {
            tarFile.delete()
        }

        def tarPath = buildOutputPath
        def tarCtx = "."
        def outputTestFile = new File(buildOutputPath)
        if (outputTestFile.isFile()) {
            tarPath = outputTestFile.parent
            tarCtx = outputTestFile.name
        }

        ShellUtils.exec(ctx, "tar -zcvf ${tarFilepath} -C ${tarPath} ${tarCtx}")

        ctx.log.info("构建产物：${outputArtifactFilename} 打包成功!")

        // update state
        ctx.state.artifactBuildPackage = outputArtifactFilename
    }
}
