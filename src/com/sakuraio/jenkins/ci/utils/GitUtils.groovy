package com.sakuraio.jenkins.ci.utils

import com.sakuraio.jenkins.ci.core.Context

/**
 * <p>GitUtils</p>
 *
 * @author nekoimi 2023/02/13
 */
class GitUtils {

    private GitUtils() {
    }

    /**
     * 下载代码到指定路径
     * @param ctx
     * @param git
     * @param folderPath
     */
    static void pull(Context ctx, git, folderPath) {
        def workspaceFolder = FileUtils.workspaceAbsPath(ctx)
        def folder = new File(folderPath as String)
        ctx.jenkins.withCredentials([ctx.jenkins.gitUsernamePassword(
                credentialsId: "${ctx.globalOptions.gitCredentials}")]) {
            def isClone = false
            if (!folder.exists()) {
                // create folder
                folder.mkdirs()

                isClone = true
            }

            try {
                if (isClone) {
                    ctx.jenkins.sh "git clone --branch master --single-branch ${git} ${folderPath}"
                } else {
                    ctx.jenkins.sh "cd ${folderPath} && git pull origin master"
                }
            } finally {
                ctx.jenkins.sh "cd ${workspaceFolder}"
            }
        }
    }

    static void push(Context ctx, folderPath) {
        def folder = new File(folderPath as String)
        if (!folder.exists()) {
            ctx.log.fail("文件夹[${folderPath}]不存在，忽略git push推送")
            return
        }

        def workspaceFolder = FileUtils.workspaceAbsPath(ctx)
        ctx.jenkins.withCredentials([ctx.jenkins.gitUsernamePassword(credentialsId: "${ctx.globalOptions.gitCredentials}")]) {
            try {
                def gitStatus = ctx.jenkins.sh(returnStdout: true, script: "cd ${folderPath} && git status -s") as String
                if (gitStatus.trim().length() <= 0) {
                    ctx.log.info("文件夹[${folderPath}]未更改，忽略git push推送")
                } else {
                    ctx.jenkins.sh pushCommands(folderPath, "feat: Updated by Jenkins ${ctx.jenkins.env.JOB_NAME} -> ${ctx.buildOptions.name}-${ctx.buildOptions.buildEnv}-${ctx.jenkins.env.BUILD_ID}")
                }
            } finally {
                ctx.jenkins.sh "cd ${workspaceFolder}"
            }
        }
    }

    private static String pushCommands(folderPath, message) {
        return """
cd ${folderPath}
git add .
git commit -m "${message}" 
git push origin master
"""
    }
}
