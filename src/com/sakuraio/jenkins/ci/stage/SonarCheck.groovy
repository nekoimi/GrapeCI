package com.sakuraio.jenkins.ci.stage

import com.sakuraio.jenkins.ci.core.Context
import com.sakuraio.jenkins.ci.core.IStage

/**
 * <p>SonarCheck</p>
 *
 * @author nekoimi 2023/02/10
 */
class SonarCheck implements IStage {

    /**
     * sonar-scanner image
     */
    private static final def SONAR_SCANNER_IMAGE = "sonarsource/sonar-scanner-cli:4.8"

    @Override
    String title() {
        return "Sonar Check"
    }

    @Override
    void execute(Context ctx) {
        def buildId = ctx.jenkins.env.BUILD_ID as Integer
        if (buildId > 0 && buildId % 10 == 0) {
            dockerExecute(ctx)
        }
    }

    static def dockerExecute(Context ctx) {
        def options = [
                "--rm",
                "-w /usr/src",
                "-e SONAR_HOST_URL='${ctx.globalOptions.sonarHost}'",
                "-e SONAR_LOGIN='${ctx.globalOptions.sonarToken}'",
                "-e SONAR_SCANNER_OPTS='-Dsonar.projectKey=${ctx.buildOptions.name} -Dsonar.java.binaries=/usr/src/target/classes'",
                "-v ${ctx.globalOptions.jenkinsHomeVolume}/workspace/${ctx.jenkins.env.JOB_NAME}/${ctx.buildOptions.modulePath}:/usr/src"
        ]

        ctx.jenkins.sh("docker run ${options.join(" ")} ${SONAR_SCANNER_IMAGE}")

        // update state
        ctx.state.sonarCheck = true
        ctx.state.sonarCheckIssuesUrl = "${ctx.globalOptions.sonarHost}/project/issues?id=${ctx.buildOptions.name}&resolved=false"
    }
}
