package com.sakuraio.jenkins.ci.utils

import com.sakuraio.jenkins.ci.core.Context

/**
 * <p>S3Utils</p>
 *
 * @author nekoimi 2023/02/16
 */
class S3Utils {

    private S3Utils() {
    }

    /**
     * 上传到S3存储
     * @param ctx
     * @param artifactPackage
     * @return
     */
    static String uploadToS3Bucket(Context ctx, artifactPackage) {
        def includePattern = "*${artifactPackage}"
        if (ctx.buildOptions.modulePath) {
            includePattern = "${ctx.buildOptions.modulePath}/*${artifactPackage}"
        }

        // 上传压缩文件到oss
        ctx.jenkins.minio(
                bucket: ctx.globalOptions.artifactS3Bucket,
                includes: includePattern,
                excludes: "",
                targetFolder: ""
        )

        return "${ctx.globalOptions.artifactS3Host}/${ctx.globalOptions.artifactS3Bucket}/${artifactPackage}"
    }
}
