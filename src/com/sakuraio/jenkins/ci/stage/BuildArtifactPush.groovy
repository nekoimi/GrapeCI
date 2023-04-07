package com.sakuraio.jenkins.ci.stage

import com.sakuraio.jenkins.ci.core.Context
import com.sakuraio.jenkins.ci.core.IStage
import com.sakuraio.jenkins.ci.utils.S3Utils

/**
 * <p>BuildArtifactPush</p>
 *
 * @author nekoimi 2023/02/10
 */
class BuildArtifactPush implements IStage {

    @Override
    String title() {
        return "Build Artifact Push"
    }

    @Override
    void execute(Context ctx) {
        if (ctx.state.artifactBuildPackage) {
            def downloadUrl = S3Utils.uploadToS3Bucket(ctx, ctx.state.artifactBuildPackage)

            // update state
            ctx.state.artifactBuildPackageDownloadUrl = downloadUrl
        }
    }
}
