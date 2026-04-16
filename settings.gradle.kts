pluginManagement {
    repositories {
        google()        // ✅ REQUIRED
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()        // ✅ REQUIRED
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "VisitralTMSO"
include(":app")