import org.gradle.jvm.tasks.Jar

plugins {
    java
}

group = "dev.example"
version = "1.0.0"
val paperApiVersion: String by project

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$paperApiVersion")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(17)
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

val jarTask = tasks.named<Jar>("jar")

val exportJar by tasks.registering(Copy::class) {
    dependsOn(jarTask)
    from(jarTask.flatMap { it.archiveFile })
    into(layout.projectDirectory.dir("jar"))
}

tasks.named("build") {
    finalizedBy(exportJar)
}
