pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
rootProject.name = "ActivityTrackerAI"
include(":app") // ή :androidApp αν έτσι λέγεται το module σου
include(":shared") // αν έχεις KMP, αλλιώς αφαίρεσέ το