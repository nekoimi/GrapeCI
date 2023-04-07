package com.sakuraio.jenkins.ci.stage

import com.sakuraio.jenkins.ci.Const
import com.sakuraio.jenkins.ci.core.Context
import com.sakuraio.jenkins.ci.core.IStage
import com.sakuraio.jenkins.ci.ext.java.JavaBuildHelper
import com.sakuraio.jenkins.ci.utils.AssertUtils
import com.sakuraio.jenkins.ci.utils.DockerUtils
import com.sakuraio.jenkins.ci.utils.FileUtils
import com.sakuraio.jenkins.ci.utils.ShellUtils
/**
 * <p>BuildImage</p>
 *
 * @author nekoimi 2023/02/10
 */
class BuildImage implements IStage {

    @Override
    String title() {
        return "Build Image"
    }

    @Override
    void execute(Context ctx) {
        def pipeline = AssertUtils.checkPipeline(ctx)
        def moduleAbsPath = FileUtils.moduleAbsPath(ctx)
        def dockerfile = FileUtils.moduleAbsPath(ctx, "Dockerfile")
        if (!FileUtils.exists(dockerfile)) {
            ctx.log.info("Dockerfile 文件 -> ${dockerfile} 不存在！忽略image构建")
            return
        }

        if (pipeline == Const.JAVA_SPEC) {
            JavaBuildHelper.renameJavaSpecTargetOutput(
                    FileUtils.moduleAbsPath(ctx, "target")
            )
        }

        def (imageName, imageTag, registryImage) = DockerUtils.getImage(ctx)
        ctx.log.info("Image: ${registryImage}")

        // docker build --build-arg JAR_NAME=target/${jarName} -t ${dockerImage} -f ${module.context}/Dockerfile ${module.context}
        ShellUtils.exec(ctx, "docker build -t ${registryImage} -f ${dockerfile} ${moduleAbsPath}")

        // update state
        ctx.state.image = imageName
        ctx.state.imageTag = imageTag
        ctx.state.registryImage = registryImage
    }
}
