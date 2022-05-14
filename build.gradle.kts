import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-library")
    id("maven-publish")
    id("forge")
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.1"
}

// These settings allow you to choose what version of Java you want to be compatible with. Forge 1.7.10 runs on Java 6 to 8.
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

version = project.property("version").toString()
group = project.property("group").toString()

minecraft {
    version = "${project.property("minecraft_version")}-${project.property("forge_version")}"
    runDir = "run"
    mappings = "stable_9"
}

repositories {
    mavenCentral()
}

val shade by configurations.creating
configurations.implementation.get().extendsFrom(shade)

val kaizVersionName = "v1.4.6"
val kaizJarName = "src1.7.10_20200822+KaizPatchX-1.4.6.jar"

//val kaizObfJar = buildDir.resolve("kaiz-patch/KaizPatchX.jar")
val kaizObfJar = buildDir.resolve("kaiz-patch/$kaizJarName")
val kaizDevJar = buildDir.resolve("kaiz-patch/$kaizJarName-debof.jar")//File("$projectDir/libs/KaizPatchX-$kaizVersionName-dev.jar")
val downloadKaizPatchX: Task by tasks.creating {
    val url =
        uri("https://github.com/Kai-Z-JP/KaizPatchX/releases/download/$kaizVersionName/$kaizJarName")
    outputs.file(kaizObfJar)
    inputs.property("url", url)
    doLast {
        url.toURL().openStream().use { i ->
            kaizObfJar.outputStream().use { o ->
                i.copyTo(o)
            }
        }
    }
}

val mcpMappingConfig by configurations.creating

val deobfKaizPatchX by tasks.creating(com.anatawa12.modPatching.source.DeobfuscateSrg::class) {
    mappings.from(mcpMappingConfig)

    sourceJar.set(kaizObfJar)
    destination.set(kaizDevJar)

    dependsOn(downloadKaizPatchX)
}

//tasks.compileKotlin.get().dependsOn(deobfKaizPatchX)
//tasks.sourceMainJava.get().dependsOn(deobfKaizPatchX)

dependencies {
    shade(kotlin("stdlib-jdk8"))
    //mcpMappingConfig("de.oceanlabs.mcp:mcp_snapshot:20140908-1.7.10@zip")
    mcpMappingConfig("de.oceanlabs.mcp:mcp_stable:9-1.7.10@zip")
    implementation(files(kaizDevJar).builtBy(deobfKaizPatchX))

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}

tasks.processResources {
    // This will ensure that this task is redone when the versions change.
    inputs.property("version", project.version)
    inputs.property("mcversion", project.minecraft.version)

    // Replace values in only mcmod.info.
    filesMatching("mcmod.info") {
        // Replace version and mcversion.
        expand(
            mapOf(
                "version" to project.version,
                "mcversion" to project.minecraft.version
            )
        )
    }
}

// Ensures that the encoding of source files is set to UTF-8, see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.jar {
    shade.forEach { dep ->
        from(project.zipTree(dep)) {
            exclude("META-INF", "META-INF/**")
            exclude("COPYING")
            exclude("COPYING.LESSER")
            exclude("NOTICE")
        }
    }
}

val shadowModJar by tasks.creating(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
    dependsOn(tasks.reobf.get())

    val basePkg = "xyz.fmdc.arw.libs"
    relocate("kotlin.", "$basePkg.kotlin.")
    relocate("kotlinx.", "$basePkg.kotlinx.")
    relocate("org.intellij.lang.annotations.", "$basePkg.ij_annotations.")
    relocate("org.jetbrains.annotations.", "$basePkg.jb_annotations.")

    from(provider { zipTree(tasks.jar.get().archiveFile) })
    destinationDirectory.set(buildDir.resolve("shadowing"))
    archiveVersion.set("")
    manifest.from(provider {
        zipTree(tasks.jar.get().archiveFile)
            .matching { include("META-INF/MANIFEST.MF") }
            .files.first()
    })
}

val copyShadowedJar by tasks.creating {
    dependsOn(shadowModJar)
    doLast {
        shadowModJar.archiveFile.get().asFile.inputStream().use { src ->
            tasks.jar.get().archiveFile.get().asFile.apply { parentFile.mkdirs() }
                .outputStream()
                .use { dst -> src.copyTo(dst) }
        }
    }
}

tasks.assemble.get().dependsOn(copyShadowedJar)


// This task creates a .jar file containing the source code of this mod.
val sourcesJar by tasks.creating(Jar::class) {
    dependsOn(tasks.classes.get())
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

// This task creates a .jar file containing a deobfuscated version of this mod, for other developers to use in a development environment.
val devJar by tasks.creating(Jar::class) {
    archiveClassifier.set("dev")
    from(sourceSets.main.get().output)
}

// Creates the listed artifacts on building the mod.
artifacts {
    archives(sourcesJar)
    archives(devJar)
}

// This block configures any maven publications you want to make.
publishing {
    publications {
        @Suppress("UNUSED_VARIABLE")
        val mavenJava by creating(MavenPublication::class) {
            // Add any other artifacts here that you would like to publish!
            artifact(tasks.jar) {
                builtBy(tasks.build)
            }
            artifact(sourcesJar) {
                builtBy(sourcesJar)
            }
            artifact(devJar) {
                builtBy(devJar)
            }
        }
    }

    // This block selects the repositories you want to publish to.
    repositories {
        // Add the repositories you want to publish to here.
    }
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
