import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
}

group = "app.fiber"
version = "1.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(Dependencies.stdlib)

    implementation(Dependencies.koin)

    implementation(Dependencies.ktorCore)
    implementation(Dependencies.ktorNetty)
    implementation(Dependencies.ktorGson)

    implementation(Dependencies.logback)

    testImplementation(Dependencies.junit)
    testImplementation(Dependencies.ktorTestEngine)
    testImplementation(Dependencies.mockk)
}

tasks {

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    val checkLib = register("checkExportedDependencies") {
        this.group = "fiber"
        val path = "${buildDir.path}/export"

        file(path).listFiles()
            ?.filter { !configurations.runtimeClasspath.get().map { file -> file.name }.contains(it.name) }
            ?.forEach { it.delete() }
    }

    val exportLib = register("exportDependencies", Copy::class) {
        this.group = "fiber"
        val path = "${buildDir.path}/export"

        into(path)

        val from = configurations.runtimeClasspath.get().filterNot { file(path).listFiles()?.map { file -> file.name }?.contains(it.name) ?: false }
        from(from)

        dependsOn(checkLib)
    }

    build {
        dependsOn(exportLib)
    }

    jar {
        archiveVersion.set("")
    }

}

object Version {
    const val cassandra = "4.3.0"
    const val koin = "2.0.1"
    const val kotlin = "1.3.50"
    const val ktor = "1.2.5"
    const val kubernetes = "4.7.1"
    const val logback = "1.2.3"
    const val redis = "5.2.1.RELEASE"

    const val junit = "5.5.2"
    const val mockk = "1.9.3"
}

object Dependencies {
    const val koin = "org.koin:koin-ktor:${Version.koin}"

    const val ktorCore = "io.ktor:ktor-server-core:${Version.ktor}"

    const val ktorNetty = "io.ktor:ktor-server-netty:${Version.ktor}"
    const val ktorJwt =  "io.ktor:ktor-auth-jwt:${Version.ktor}"
    const val ktorGson =  "io.ktor:ktor-gson:${Version.ktor}"

    const val logback = "ch.qos.logback:logback-classic:${Version.logback}"

    const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Version.kotlin}"


    const val junit = "org.junit.jupiter:junit-jupiter-api:${Version.junit}"
    const val ktorTestEngine = "io.ktor:ktor-server-test-host:${Version.ktor}"
    const val mockk = "io.mockk:mockk:${Version.mockk}"
}