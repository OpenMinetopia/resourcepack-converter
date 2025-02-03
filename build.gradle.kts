plugins {
    id("java")
    id("io.freefair.lombok") version "8.12"
}

group = "nl.openminetopia.resourceconverter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")
    implementation("commons-io:commons-io:2.18.0")
}

tasks.test {
    useJUnitPlatform()
}