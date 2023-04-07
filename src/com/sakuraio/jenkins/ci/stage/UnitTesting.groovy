package com.sakuraio.jenkins.ci.stage

import com.sakuraio.jenkins.ci.core.Context
import com.sakuraio.jenkins.ci.core.IStage

/**
 * <p>UnitTesting</p>
 *
 * @author nekoimi 2023/02/10
 */
class UnitTesting implements IStage {

    @Override
    String title() {
        return "Unit Testing"
    }

    @Override
    void execute(Context ctx) {
        ctx.log.debug("${title()}")
    }
}
