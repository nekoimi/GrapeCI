package com.sakuraio.jenkins.ci.utils

import com.sakuraio.jenkins.ci.core.Context

/**
 * <p>LockUtils</p>
 *
 * @author nekoimi 2023/02/13
 */
class LockUtils {

    static def run(Context ctx, name, Closure callback) {
        def lockFile = FileUtils.workspaceParentFile(ctx, ".${CodecUtils.sha1(name as String)}.lock")
        ctx.log.info("lock: ${lockFile.path}")
        while (true) {
            if (lockFile.exists()) {
                ctx.log.info("未获取到lock, wait")
                sleep(3000)
            } else {
                if (lockFile.createNewFile()) {
                    try {
                        ctx.log.info("获取到lock, 同步执行 - START")
                        callback.call()
                        ctx.log.info("获取到lock, 同步执行 - STOP")
                    } catch (Exception e) {
                        ctx.log.fail(e.message)
                    } finally {
                        lockFile.delete()
                        ctx.log.info("释放lock")
                        break
                    }
                }
            }
        }
    }
}
