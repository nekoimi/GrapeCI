package com.sakuraio.jenkins.ci.utils

import com.sakuraio.jenkins.ci.core.Context

/**
 * <p>AssertUtils</p>
 *
 * @author nekoimi 2023/02/10
 */
class AssertUtils {

    private AssertUtils() {
    }

    static def checkPipeline(Context ctx) {
        def pipeline = ctx.buildOptions.pipeline
        if (!pipeline) {
            ctx.log.fail("缺少pipeline配置!")
        }
        return pipeline
    }

    static def checkPipeline(Context ctx, target) {
        def pipeline = checkPipeline(ctx)
        if (pipeline != target) {
            ctx.log.fail("pipeline配置错误!")
        }
        return pipeline
    }
}
