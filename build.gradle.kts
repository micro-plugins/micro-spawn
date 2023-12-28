import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.sxtanna.mc.micro"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven(url = uri("https://repo.aikar.co/content/groups/aikar/"))
    maven(url = uri("https://papermc.io/repo/repository/maven-public/"))
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")

    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
}

java.toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
}


tasks.assemble {
    dependsOn += tasks.shadowJar
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")

    relocate("co.aikar.commands", "com.sxtanna.mc.micro.libs.commands")
    relocate("co.aikar.locales", "com.sxtanna.mc.micro.libs.locales")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

tasks.withType<ProcessResources> {
    filesMatching("*.yml") {
        expand(
            "version" to rootProject.version,
        )
    }
}