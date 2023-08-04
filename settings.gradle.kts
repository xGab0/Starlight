pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()

        maven {
            name = "Fabric"
            url  = uri("https://maven.fabricmc.net/")
        }
        maven {
            name = "Quilt"
            url  = uri("https://maven.quiltmc.org/repository/release")
        }
        maven {
            name = "Quilt Snapshots"
            url  = uri("https://maven.quiltmc.org/repository/snapshot")
        }
    }
}