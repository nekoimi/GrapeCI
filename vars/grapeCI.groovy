#!/usr/bin/groovy

import com.sakuraio.jenkins.ci.GrapeCI
import com.sakuraio.jenkins.ci.factory.SingletonFactory

// =========================================================================
// GrapeCI 执行入口
// =========================================================================
def call() {
    SingletonFactory.singletonInstance(this, GrapeCI.class).run()
}

return this