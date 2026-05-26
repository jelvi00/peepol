plugins {
    java
    jacoco
}

repositories {
    mavenCentral()
}

group = "org.peepol"
version = "1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(Versions.JAVA))
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}
