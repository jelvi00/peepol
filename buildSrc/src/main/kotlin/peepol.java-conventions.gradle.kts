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

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}
