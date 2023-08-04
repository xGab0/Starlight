import java.io.ByteArrayOutputStream

plugins {
    id("fabric-loom") version "1.3-SNAPSHOT"
    //id("io.github.juuxel.loom-vineflower") version '1.11.0'
    id("maven-publish")
}

fun getGitCommit(): String {
    val stdout = ByteArrayOutputStream()
    task<Exec>("stdout") {
        commandLine("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}

//archivesBaseName = project.archives_base_name
version = project.property("mod_version")!! as String + "+fabric." + getGitCommit()
group = project.property("maven_group")!! as String

dependencies {
    val minecraft_version = project.property("minecraft_version")!! as String
    //val yarn_mappings     = project.property("yarn_mappings")!! as String
    val loader_version    = project.property("loader_version")!! as String
    //val fabric_version    = project.property("fabric_version")!! as String

    //to change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${minecraft_version}")
    //mappings "net.fabricmc:yarn:${yarn_mappings}:v2"
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${loader_version}")

    // Fabric API. This is technically optional, but you probably want it anyway.
    //modImplementation "net.fabricmc.fabric-api:fabric-api:${fabric_version}"

    // PSA: Some older mods, compiled on Loom 0.2.1, might have outdated Maven POMs.
    // You may need to force-disable transitiveness on them.
}

loom {
    accessWidenerPath = file("src/main/resources/starlight.accesswidener")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<Jar> {
        from("LICENSE")
    }

    // make build reproducible
    tasks.withType<AbstractArchiveTask> {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    tasks.withType<ProcessResources> {
        inputs.property("version", project.version)
        filteringCharset = "UTF-8"

        filesMatching("fabric.mod.json") {
            expand(mapOf("version" to project.version));
        }
    }

    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}

// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.remapJar)
            artifact(tasks.remapSourcesJar)
        }
    }

    // select the repositories you want to publish to
    repositories {
        // uncomment to publish to the local maven
        // mavenLocal()
    }
}