package com.sakuraio.jenkins.ci.stage

import com.sakuraio.jenkins.ci.core.Context
import com.sakuraio.jenkins.ci.core.IStage
import com.sakuraio.jenkins.ci.core.Initialization
import com.sakuraio.jenkins.ci.ext.notify.ISendNotify
import com.sakuraio.jenkins.ci.ext.notify.WxWorkSendNotify
import com.sakuraio.jenkins.ci.factory.SingletonFactory
/**
 * <p>Notify</p>
 *
 * @author nekoimi 2023/02/10
 */
class Notify implements IStage, Initialization {

    /**
     * 消息通知
     */
    ISendNotify notify

    @Override
    void initialization(Object jenkins) {
        this.notify = SingletonFactory.singletonInstance(jenkins, WxWorkSendNotify.class)
    }

    @Override
    String title() {
        return "Notify"
    }

    @Override
    void execute(Context ctx) {
        this.notify.sendSuccess(ctx)
    }
}
