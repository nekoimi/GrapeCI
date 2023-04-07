package com.sakuraio.jenkins.ci.stage

import com.sakuraio.jenkins.ci.core.IStage
import com.sakuraio.jenkins.ci.core.Context
import com.sakuraio.jenkins.ci.utils.S3Utils

/**
 * <p>DeployArtifactPush</p>
 *
 * @author nekoimi 2023/02/14
 */
class DeployArtifactPush implements IStage {

    @Override
    String title() {
        return "Deploy Artifact Push"
    }

    @Override
    void execute(Context ctx) {
        if (ctx.state.artifactComposeDeployPackage) {
            def downloadUrl = S3Utils.uploadToS3Bucket(ctx, ctx.state.artifactComposeDeployPackage)

            // update state
            ctx.state.artifactComposeDeployPackageDownloadUrl = downloadUrl
        }

        if (ctx.state.artifactHelmDeployPackage) {
            def downloadUrl = S3Utils.uploadToS3Bucket(ctx, ctx.state.artifactHelmDeployPackage)

            // update state
            ctx.state.artifactHelmDeployPackageDownloadUrl = downloadUrl
        }
    }
}
