import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.21" apply false
}



group = "NettyChat"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}