package com.sakuraio.jenkins.ci.stage

import com.sakuraio.jenkins.ci.core.Context
import com.sakuraio.jenkins.ci.core.IStage

/**
 * <p>DockerDeploy</p>
 *
 * @author nekoimi 2023/02/10
 */
class DeployToCompose implements IStage {

    @Override
    String title() {
        return "Deploy To Compose"
    }

    @Override
    void execute(Context ctx) {

    }
}
