package com.sakuraio.jenkins.ci.ext.notify

import com.sakuraio.jenkins.ci.core.Context
import com.sakuraio.jenkins.ci.core.Initialization

/**
 * <p>SendNotify</p>
 *
 * @author nekoimi 2023/02/28
 */
interface ISendNotify extends Initialization, Serializable {

    /**
     * 发送成功消息
     * @param ctx
     * @return
     */
    def sendSuccess(Context ctx)

    /**
     * 发送构建失败通知
     * @return
     */
    def sendFail(Context ctx, errorMsg)
}