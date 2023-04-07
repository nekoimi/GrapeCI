package com.sakuraio.jenkins.ci.stage

import com.sakuraio.jenkins.ci.core.IStage
import com.sakuraio.jenkins.ci.core.Context
import com.sakuraio.jenkins.ci.utils.LockUtils
import com.sakuraio.jenkins.ci.utils.SshUtils
/**
 * <p>DeployToKubernetes</p>
 *
 * @author nekoimi 2023/02/10
 */
class DeployToKubernetes implements IStage {

    @Override
    String title() {
        return "Deploy To Kubernetes"
    }

    @Override
    void execute(Context ctx) {
        def k8sOptions = ctx.buildOptions?.kubernetes
        if (!k8sOptions) {
            ctx.log.warning("当前构建环境[${ctx.buildOptions.buildEnv}]缺少k8s部署配置，忽略部署")
            return
        }

        LockUtils.run(ctx, k8sOptions.deployHelmGit, {
            SshUtils.remoteK8sExecShell(ctx.jenkins, k8sOptions, "deploy/bin/helm-deploy.sh", [
                    REPLACE_WORKSPACE       : "${k8sOptions.workspace}",
                    REPLACE_CHART_NAME      : "${ctx.buildOptions.name}",
                    REPLACE_DEPLOY_NAMESPACE: "${k8sOptions.deployNamespace}"
            ])
        })
    }
}
