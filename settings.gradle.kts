pluginManagement {
    repositories {
        google()
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
}
rootProject.name = "HabitFlow"
include(":app")

// Redirect build output outside OneDrive to avoid file-lock issues during sync
val buildBase = File("C:/Users/Juliana/AppData/Local/Temp/HabitFlowBuild")
gradle.allprojects {
    layout.buildDirectory.set(buildBase.resolve(project.name))
}
