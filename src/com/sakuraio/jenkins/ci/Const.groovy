package com.sakuraio.jenkins.ci

/**
 * <p>Const</p>
 *
 * @author nekoimi 2023/02/10
 */
final class Const {

    private Const() {
    }

    static final def ALWAYS_RUNNABLE = "ALWAYS_RUNNABLE"
    static final def PROJECT_YAML = "project.yaml"

    /**
     * ======================================================================
     * Jenkins参数化构建，添加参数配置
     * ======================================================================
     *
     * 构建环境参数
     */
    static final def JENKINS_PARAMETER_BUILD_ENV = "BUILD_ENV"

    /**
     * 构建分支参数，使用参数化构建->git参数，选择分支或者Tag
     */
    static final def JENKINS_PARAMETER_BUILD_BRANCH = "BUILD_BRANCH"

    /**
     * ======================================================================
     * pipeline分类
     * ======================================================================
     *
     * 构建环境参数
     */
    static final def WEB_SPEC = "web-spec"
    static final def JAVA_SPEC = "java-spec"


    /**
     TEST("test"),
     RELEASE("release")

     BCS("bcs-spec"),
     DOCKER_IMAGE("build-image"),
     SHELL("shell-spec"),
     SVC("svc-spec"),
     WEB("web-spec"),
     PHP("php-spec"),
     JAVA("java-spec"),
     GOLANG("go-spec")
     */
}
