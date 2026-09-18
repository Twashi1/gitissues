plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    kotlin("plugin.jpa") version "2.2.21"
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jlleitschuh.gradle.ktlint") version "12.3.0"
}

group = "gitissues"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mustache")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-flyway")

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // SQLite
    runtimeOnly("org.xerial:sqlite-jdbc")
    implementation("org.hibernate.orm:hibernate-community-dialects")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:6.1.0")
    implementation(project(":gitissues-cli:kotlin:bindings"))
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

ktlint {
    version.set("1.4.0")
    android.set(false)
    outputToConsole.set(true)
    ignoreFailures.set(false)
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    ktlint {
        version.set("1.3.1")
    }
}

tasks.named("check") {
    dependsOn("ktlintCheck")
}

tasks.named("build") {
    dependsOn("ktlintCheck")
    dependsOn(":buildNativeLibrary")
}

tasks.register<Exec>("buildNativeLibrary") {
    group = "build"
    description = "Builds the native JNI library"
    workingDir = file("gitissues-cli")
    // Configure and build the native library with JNI enabled using preset from README
    commandLine = listOf("bash", "-c", "cmake --preset kotlin && cmake --build --preset kotlin")
}

// Ensure the native library is built before running the application
tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    dependsOn("buildNativeLibrary")
    jvmArgs("-Djava.library.path=$projectDir/gitissues-cli/build/kotlin/kotlin/jni")
}

// Also ensure it's built when building the frontend resources
tasks.named("processResources") {
    dependsOn("buildFrontend")
    dependsOn("buildNativeLibrary")
}

tasks.register<Exec>("buildFrontend") {
    workingDir("../client")
    commandLine("npm", "run", "build")
}

// Clean native library build when cleaning
tasks.named("clean") {
    doLast {
        delete(fileTree("gitissues-cli/build/kotlin"))
    }
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("gitissues.jar")
}
