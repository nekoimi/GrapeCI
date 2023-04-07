package com.sakuraio.jenkins.ci.utils

import com.sakuraio.jenkins.ci.core.Context

/**
 * <p>DockerUtils</p>
 *
 * @author nekoimi 2023/02/10
 */
class DockerUtils {

    private DockerUtils() {
    }

    /**
     * 获取一个docker image名称
     * @param ctx
     * @return
     */
    static def getImage(Context ctx) {
        def imageName = "${ctx.buildOptions.name}"
        def imageTag = "${ctx.buildOptions.version}.${ctx.jenkins.env.BUILD_ID}"
        def repository = StrUtils.cleanStartEndWiths(ctx.buildOptions?.docker?.repository as String, "/")
        if (!repository) {
            return [
                    "${ctx.buildOptions.group}/${imageName}",
                    imageTag,
                    "${ctx.buildOptions.group}/${imageName}:${imageTag}"
            ]
        }

        repository = repository.replaceFirst("(http://)|(https://)", "")

        if (checkRepositoryIsHost(repository)) {
            imageName = "${repository}/${ctx.buildOptions.group}/${imageName}"
        } else {
            imageName = repository
            imageTag = "${ctx.buildOptions.name}-${ctx.buildOptions.version}.${ctx.jenkins.env.BUILD_ID}"
        }

        return [
                imageName, imageTag, "${imageName}:${imageTag}"
        ]
    }

    /**
     * 检查docker repository是否是一个host路径
     * @param repository
     * @return
     */
    static Boolean checkRepositoryIsHost(repository) {
        def original = repository as String
        // 去掉开头的 http/https
        original = original.replaceFirst("(http://)|(https://)", "")
        // 去掉末尾的 /
        if (original.endsWith("/")) {
            original = original.substring(0, original.length() - 1)
        }
        // 判断路径中是否存在 /
        return !original.contains("/")
    }

    /**
     * 从一个路径中解析registry的host
     * registry.sakuraio.com/java-repos/test:1.0.0-test             => registry.sakuraio.com
     * registry.cn-shenzhen.aliyuncs.com/nekoimi/test:1.0.0-test    => registry.cn-shenzhen.aliyuncs.com
     * @param str
     * @return
     */
    static String parseRegistryHost(path) {
        if (!path) {
            return ""
        }
        def original = path as String
        def isHttps = false
        if (original.startsWith("https")) {
            isHttps = true
        }
        // 去掉开头的 http/https
        original = original.replaceFirst("(http://)|(https://)", "")
        // 去掉末尾的 /
        if (original.endsWith("/")) {
            original = original.substring(0, original.length() - 1)
        }
        // 判断路径中是否存在 /
        if (original.contains("/")) {
            original = original.split("/").first()
        }
        // 拼接协议
        return isHttps ? "https://${original}" : "http://${original}"
    }
}
