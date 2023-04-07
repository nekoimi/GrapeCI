package com.sakuraio.jenkins.ci.config

import com.sakuraio.jenkins.ci.Const
import com.sakuraio.jenkins.ci.config.properties.DockerProperties
import com.sakuraio.jenkins.ci.config.properties.KubernetesProperties
import com.sakuraio.jenkins.ci.core.AbsOptionsLoader

/**
 * <p>构建配置</p>
 *
 * @author nekoimi 2023/02/28
 */
class BuildOptions extends AbsOptionsLoader {

    /**
     * 项目名称
     * 唯一，简洁太长，由英文、数字、-、_组成，不能有其他字符
     * - docker image
     * - helm chart
     * - k8s部署的service名称
     */
    def name

    /**
     * 项目版本号，必填，遵循 Semver 规范 (https://semver.org) -> 主版本号.次版本号.修订号
     * 作为image的tag来区分不同的docker image
     */
    def version

    /**
     * 项目简介
     * 一句话说明，后续通知相关消息会用到
     */
    def description

    /**
     * 项目分组，选填
     * 用于特定条件下分组分类
     * 例如：
     * harbor里面的项目名称，给docker image归类，相同分类的项目docker image会放在一起
     */
    def group

    /**
     * 构建流程
     *
     * 例如：
     * # svc-spec: 通用后端项目构建，dockerfile -> image -> push harbor -> helm deploy to k8s
     * # web-spec: 标准前端项目构建，nodejs -> dist + dockerfile -> image -> push harbor -> helm deploy to k8s
     * # 后端扩展：
     * # php-spec: 标准php构建，composer + dockerfile -> image -> push harbor -> helm deploy to k8s
     * # java-spec: 标准java构建，maven -> jar + dockerfile -> image -> push harbor -> helm deploy to k8s
     * # go-spec: 标准go构建，go -> binary + dockerfile -> image -> push harbor -> helm deploy to k8s
     */
    def pipeline

    /**
     * 模块相对当前job的路径
     */
    def modulePath

    /**
     * 构建环境
     */
    def buildEnv

    /**
     * 用于build项目的docker镜像
     */
    def image

    /**
     * 命令列表，使用 {buildImage} 来执行
     */
    List<String> commands = new ArrayList<>()

    /**
     * 构建步骤
     */
    List<String> stages = new ArrayList<>()

    /**
     * Docker配置
     */
    DockerProperties docker

    /**
     * Kubernetes配置
     */
    KubernetesProperties kubernetes

    @Override
    protected String resourcePath() {
        return "build.${this.jenkinsParameter.getBuildEnv()}.yaml"
    }

    @Override
    protected void postPropertiesSet() {
        this.buildEnv = jenkinsParameter.getBuildEnv()
        this.log.info("加载项目配置：${Const.PROJECT_YAML}")
        def pos = this.jenkins.readYaml(file: Const.PROJECT_YAML)
        this.name = pos?.name
        this.version = pos?.version
        this.description = pos?.description
        this.group = pos?.group
        this.pipeline = pos?.pipeline
        this.modulePath = pos?.build?.context

        // 读取构建所使用的image配置需要兼容早期版本写法
        def v1Image = pos?.buildImage
        if (v1Image) {
            this.image = v1Image
        } else {
            this.image = pos?.build?.image
        }

        // 读取构建命令需要兼容早期版本的配置写法
        if (pos?.installCommand) {
            this.commands.add(pos.installCommand as String)
        }
        if (pos?.buildCommand) {
            this.commands.add(pos.buildCommand as String)
        }
        def cmdList = pos?.build?.commands as List
        if (cmdList) {
            this.commands.addAll(cmdList)
        }

        // 读取构建环境配置
        if (this.options?.stages) {
            this.stages.addAll(this.options.stages as List)
        }
        if (this.options?.docker) {
            this.docker = new DockerProperties(this.options.docker as Map)
        }
        // 读取k8s的配置需要兼容早期版本
        def k8s = this.options?.k8s
        if (!k8s) {
            k8s = this.options?.kubernetes
        }
        if (k8s) {
            this.kubernetes = new KubernetesProperties(k8s as Map)
        }
    }
}
