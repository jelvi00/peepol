plugins {
    id("peepol.spring-library-conventions")
    id("peepol.junit-conventions")
    id("org.springframework.boot") version Versions.SPRING_BOOT
}

springBoot {
    mainClass.set("org.peepol.Application")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-undertow")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.github.nbaars:paseto4j-version4:${Versions.PASETO}")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("org.postgresql:postgresql")

    implementation("org.liquibase:liquibase-core")

    implementation("org.mapstruct:mapstruct:${Versions.MAPSTRUCT}")
    annotationProcessor("org.mapstruct:mapstruct-processor:${Versions.MAPSTRUCT}")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    testImplementation("com.h2database:h2")
    testImplementation("io.rest-assured:rest-assured")
}
