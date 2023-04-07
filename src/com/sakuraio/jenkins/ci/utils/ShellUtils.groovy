package com.sakuraio.jenkins.ci.utils

import com.sakuraio.jenkins.ci.core.Context

/**
 * <p>ShellUtils</p>
 *
 * @author nekoimi 2023/02/23
 */
class ShellUtils {

    private ShellUtils() {
    }

    /**
     * 执行shell脚本
     * @param jenkins
     * @param script
     * @return
     */
    static void exec(Context ctx, String script) {
        def out = ctx.jenkins.sh(returnStdout: true, script: "${script}").trim() as String
        ctx.log.debug("Shell执行结果：${out}")
        def outUpper = out.toUpperCase()
        if (outUpper.indexOf("ERROR") > -1) {
            ctx.log.fail(out)
        }
    }
}
