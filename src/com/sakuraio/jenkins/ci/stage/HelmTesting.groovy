package com.sakuraio.jenkins.ci.stage

import com.sakuraio.jenkins.ci.core.Context
import com.sakuraio.jenkins.ci.core.IStage
import com.sakuraio.jenkins.ci.utils.LockUtils
import com.sakuraio.jenkins.ci.utils.SshUtils
/**
 * <p>HelmTesting</p>
 *
 * @author nekoimi 2023/02/10
 */
class HelmTesting implements IStage {

    @Override
    String title() {
        return "Helm Testing"
    }

    @Override
    void execute(Context ctx) {
        def k8sOptions = ctx.buildOptions?.kubernetes
        if (!k8sOptions) {
            ctx.log.warning("当前构建环境[${ctx.buildOptions.buildEnv}]缺少k8s部署配置，忽略helm测试")
            return
        }

        LockUtils.run(ctx, k8sOptions.deployHelmGit, {
            SshUtils.remoteK8sExecShell(ctx.jenkins, k8sOptions, "deploy/bin/helm-testing.sh", [
                    REPLACE_CHART_NAME      : "${ctx.buildOptions.name}",
                    REPLACE_DEPLOY_NAMESPACE: "${k8sOptions.deployNamespace}"
            ])
        })
    }
}
