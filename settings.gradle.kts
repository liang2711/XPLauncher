import kotlin.concurrent.timerTask

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    var path=rootDir.absolutePath+"\\app\\libs\\mini_framework.jar"
    gradle.projectsEvaluated {

    }
}

rootProject.name = "XPLauncher"
include(":app")
 