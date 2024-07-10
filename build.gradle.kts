plugins {
    id("java")
}

group = "me.tofaa"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("net.minestom:minestom-snapshots:90fb708739")
    testImplementation("net.minestom:minestom-snapshots:90fb708739")
}