package com.sakuraio.jenkins.ci.config

import com.sakuraio.jenkins.ci.Const
import com.sakuraio.jenkins.ci.core.AbsOptionsLoader
import com.sakuraio.jenkins.ci.core.State
import com.sakuraio.jenkins.ci.factory.SingletonFactory

/**
 * <p>ModuleProperties</p>
 *
 * @author nekoimi 2023/02/28
 */
class ModuleOptions extends AbsOptionsLoader {

    /**
     * 模块配置列表
     */
    List<Map> modules = new ArrayList<>();

    @Override
    protected String resourcePath() {
        return null
    }

    @Override
    protected void postPropertiesSet() {
        def state = SingletonFactory.singletonInstance(this.jenkins, State.class)
        def pos = this.jenkins.readYaml(file: Const.PROJECT_YAML)
        def mds = pos?.modules
        if (mds) {
            this.modules.addAll(mds as List)
            // 多模块项目
            state.isMultiModule = true
        } else {
            // 如果不存在多模块配置
            // 需要默认设置当前项目为默认模块
            this.modules.add([
                    booleanParam: Const.ALWAYS_RUNNABLE,
                    name        : pos?.name,
                    path        : pos?.build?.context,
                    description : pos?.description
            ])
        }
    }
}
