package com.sakuraio.jenkins.ci.core
/**
 * <p>所有的stage都需要实现该接口</p>
 *
 * @author nekoimi 2022/11/18
 */
interface IStage extends Serializable {

    /**
     * 标题
     * @return
     */
    String title()

    /**
     * 执行
     * @param context
     */
    void execute(Context ctx)
}