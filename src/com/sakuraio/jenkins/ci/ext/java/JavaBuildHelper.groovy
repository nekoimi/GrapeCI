package com.sakuraio.jenkins.ci.ext.java

import com.sakuraio.jenkins.ci.utils.FileUtils

/**
 * <p>Java构建优化扩展功能</p>
 *
 * @author nekoimi 2023/02/28
 */
class JavaBuildHelper {

    private JavaBuildHelper() {}

    /**
     * 修改java项目构建的输出
     * xxx.jar 命名为 app.jar
     * xxx.war 命名为 app.war
     * @return
     */
    static void renameJavaSpecTargetOutput(outputPath) {
        def targetFile = new File(outputPath as String)
        if (targetFile.exists()) {
            FileUtils.recursiveTraversalFolder(targetFile, { File f ->
                def filename = f.name
                if (filename.endsWith(".jar")) {
                    f.renameTo("${f.parent}/app.jar")
                }
                if (filename.endsWith(".war")) {
                    f.renameTo("${f.parent}/app.war")
                }
            })
        }
    }
}
