package com.sakuraio.jenkins.ci.utils

import com.sakuraio.jenkins.ci.core.Context
import com.sakuraio.jenkins.ci.logger.Log

/**
 * <p>FileUtils</p>
 *
 * @author nekoimi 2022/11/17
 */
class FileUtils {

    private FileUtils() {}

    /**
     * 预处理文件、路径
     * @param filepath
     */
    private static File preWriteFile(String filepath) {
        def file = new File(filepath)
        if (!file.exists()) {
            if (file.isDirectory()) {
                file.mkdirs()
            } else {
                if (!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                }
                file.createNewFile()
            }
        }
        return file
    }

    /**
     * 递归遍历文件夹处理文件
     * @param file
     * @param callback
     */
    static void recursiveTraversalFolder(File file, Closure callback) {
        if (file.isDirectory()) {
            file.listFiles().each { f ->
                f.isDirectory() ? recursiveTraversalFolder(f, callback) : callback.call(f)
            }
        } else {
            callback.call(file)
        }
    }

    /**
     * 复制文件/文件夹
     * @param src
     * @param target
     */
    static void copy(Log log, String src, String target) {
        copy(log, src, target, { String from, String to ->
            def f = new File(from)
            def t = new File(to)
            t.write(f.text, "utf-8")
        })
    }

    /**
     * 复制文件/文件夹
     * @param src
     * @param target
     */
    static void copy(Log log, String src, String target, Closure callback) {
        recursiveTraversalFolder(new File(src), { File from ->
            def toPath = from.path.replaceAll("\\\\", "/").replaceFirst(src, target)
            def to = preWriteFile(toPath)
            log.info("Copy ${from.path} to ${to.path}")
            callback.call(from.path, to.path)
        })
    }

    /**
     * 判断文件是否存在
     * @param filepath
     * @return
     */
    static boolean exists(String filepath) {
        return new File(filepath).exists()
    }

    /**
     * 创建文件夹
     * @param filepath
     * @return
     */
    static boolean mkdirs(String filepath) {
        new File(filepath).mkdirs()
    }

    /**
     * 获取当前jenkins构建job路径下的绝对路径
     * @param ctx
     * @param targetPath
     * @return
     */
    static String workspaceAbsPath(Context ctx, targetPath = "") {
        def workspacePath = ctx.jenkins.env.WORKSPACE as String
        if (!targetPath) {
            return "${workspacePath}"
        } else {
            return "${workspacePath}/${targetPath}"
        }
    }

    /**
     * 获取当前jenkins构建job路径父级路径下的绝对路径
     * @param ctx
     * @param targetPath
     * @return
     */
    static String workspaceParentAbsPath(Context ctx, targetPath = "") {
        def workspacePath = ctx.jenkins.env.WORKSPACE as String
        def workspaceParentPath = new File(workspacePath).parent
        if (!targetPath) {
            return "${workspaceParentPath}"
        } else {
            return "${workspaceParentPath}/${targetPath}"
        }
    }

    /**
     * 获取当前jenkins构建job路径父级路径下的绝对路径的文件对象
     * @param ctx
     * @param targetPath
     * @return
     */
    static File workspaceParentFile(Context ctx, targetPath = "") {
        def filepath = workspaceParentAbsPath(ctx, targetPath)
        return new File(filepath)
    }

    /**
     * 获取当前构建环境下的当前绝对路径
     * @param ctx
     * @param targetPath
     * @return
     */
    static String moduleAbsPath(Context ctx, targetPath = "") {
        def workspacePath = ctx.jenkins.env.WORKSPACE as String
        def modulePath = ctx.buildOptions.modulePath
        if (modulePath) {
            if (!targetPath) {
                return "${workspacePath}/${modulePath}"
            } else {
                return "${workspacePath}/${modulePath}/${targetPath}"
            }
        } else {
            if (!targetPath) {
                return "${workspacePath}"
            } else {
                return "${workspacePath}/${targetPath}"
            }
        }
    }

    /**
     * 获取当前构建环境下的当前绝对路径的文件对象
     * @param ctx
     * @param targetPath
     * @return
     */
    static File moduleFile(Context ctx, targetPath = "") {
        def filepath = moduleAbsPath(ctx, targetPath)
        return new File(filepath)
    }

    /**
     * 写入文件A到文件B
     * @param from
     * @param to
     * @param injectArgs
     */
    static void writeWithFileInjectArgs(String from, String to, LinkedHashMap<String, String> injectArgs = [:]) {
        def toF = preWriteFile(to)
        def text = new File(from).text
        for (e in injectArgs.entrySet()) {
            text = text.replaceAll(e.getKey(), e.getValue())
        }
        toF.append(text, "utf-8")
    }

    /**
     * 覆盖写入文件A到文件B
     * @param from
     * @param to
     * @param injectArgs
     */
    static void rewriteWithFileInjectArgs(String from, String to, LinkedHashMap<String, String> injectArgs = [:]) {
        def toF = preWriteFile(to)
        def text = new File(from).text
        for (e in injectArgs.entrySet()) {
            text = text.replaceAll(e.getKey(), e.getValue())
        }
        toF.write(text, "utf-8")
    }

    /**
     * 加载资源内容，并注入参数
     * @param jenkins
     * @param resource
     * @param injectArgs
     * @return
     */
    static String readResourceContentInjectArgs(jenkins, String resource, LinkedHashMap<String, String> injectArgs = [:]) {
        def text = jenkins.libraryResource(resource) as String
        for (e in injectArgs.entrySet()) {
            text = text.replaceAll(e.getKey(), e.getValue())
        }
        return text
    }

    /**
     * 追加写入资源信息到文件
     * @param jenkins
     * @param fromResource
     * @param to
     * @param injectArgs
     */
    static void writeWithResourceInjectArgs(jenkins, String fromResource, String to, LinkedHashMap<String, String> injectArgs = [:]) {
        def toF = preWriteFile(to)
        def text = readResourceContentInjectArgs(jenkins, fromResource, injectArgs)
        toF.append(text, "utf-8")
    }

    /**
     * 覆盖写入资源信息到文件
     * @param jenkins
     * @param fromResource
     * @param resource
     * @param injectArgs
     */
    static void rewriteWithResourceInjectArgs(jenkins, String fromResource, String to, LinkedHashMap<String, String> injectArgs = [:]) {
        def toF = preWriteFile(to)
        def text = readResourceContentInjectArgs(jenkins, fromResource, injectArgs)
        toF.write(text, "utf-8")
    }
}
