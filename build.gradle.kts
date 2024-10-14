// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
}
allprojects{
    gradle.projectsEvaluated {
        tasks.withType<JavaCompile>().configureEach {
            // Xbootclasspath/p: 是Java编译的寻址优先设置，插入内容到-bootclasspath的内容之前
            // Xbootclasspath/p: 之后的路径请使用「绝对路径」!!!
            options.compilerArgs.add("-Xbootclasspath/p:${rootDir.path}/app/libs/framework.jar")
            println("-Xbootclasspath/p:${rootDir.path}/app/libs/framework.jar")
        }
    }
}