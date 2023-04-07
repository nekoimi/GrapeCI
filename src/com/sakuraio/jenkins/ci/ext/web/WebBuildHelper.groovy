package com.sakuraio.jenkins.ci.ext.web

import com.sakuraio.jenkins.ci.core.Context
import com.sakuraio.jenkins.ci.utils.FileUtils
import com.sakuraio.jenkins.ci.utils.SortUtils
/**
 * <p>WebBuildHelper</p>
 *
 * @author nekoimi 2023/03/04
 */
class WebBuildHelper {

    private WebBuildHelper() {
    }

    /**
     * web构建参数环境变量替换
     * @param ctx
     * @param outputPath
     */
    static void replaceWebOutput(Context ctx, String buildOutputPath) {
        def k8sValuesYamlFile = FileUtils.moduleAbsPath(ctx, "k8s/${ctx.buildOptions.buildEnv}-values.yaml")
        if (!FileUtils.exists(k8sValuesYamlFile)) {
            ctx.log.info("k8s values 配置 [${k8sValuesYamlFile}] 不存在，跳过环境变量替换")
            return
        }
        def values = ctx.jenkins.readYaml(file: k8sValuesYamlFile)
        def envList = values?.configMap?.env
        if (!envList) {
            ctx.log.info("k8s values 配置 [${k8sValuesYamlFile}] 环境变量列表为空，跳过环境变量替换")
            return
        }
        def replaceEnvMap = new HashMap()
        for (e in envList) {
            def eKey = e.key as String
            if ("ENV_DEF" == eKey) {
                // 兼容旧版写法，跳过环境变量定义字段
                continue
            }
            def eValue = e.value as String
            // fixme 添加对新版 webapp 的支持：https://github.com/nekoimi/docker-webapp-go
            if (eKey.startsWith("WEBAPP_ENV.")) {
                eKey = eKey.replaceFirst("WEBAPP_ENV.", "")
            }
            replaceEnvMap.put(eKey, eValue)
        }
        def keySortList = replaceEnvMap.keySet().toArray()
        SortUtils.sort(keySortList, {String a, String b ->
            return a.length() > b.length()
        })
        ctx.log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>> EnvReplace Start <<<<<<<<<<<<<<<<<<<<<<<<<<<<")
        for (key in keySortList) {
            ctx.log.info("SortEnv: " + key)
        }
        FileUtils.recursiveTraversalFolder(new File(buildOutputPath), { File f ->
            def fn = f.name
            if (fn.endsWith(".js") ||
                    fn.endsWith(".html") ||
                    fn.endsWith(".css") ||
                    fn.endsWith(".json")) {
                // Replace
                def text = f.text
                for (k in keySortList) {
                    def v = replaceEnvMap.get(k) as String
                    if (v == "/") {
                        text = text.replaceAll("/" + k + "/", k)
                        text = text.replaceAll("/" + k, k)
                        text = text.replaceAll(k + "/", k)
                    } else {
                        if (v.startsWith("/")) {
                            text = text.replaceAll("/" + k, k)
                        }
                        if (v.endsWith("/")) {
                            text = text.replaceAll(k + "/", k)
                        }
                    }
                    text = text.replaceAll(k, v)
                }
                f.write(text, "utf-8")
            }
        })
        ctx.log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>> EnvReplace End <<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    }
}
