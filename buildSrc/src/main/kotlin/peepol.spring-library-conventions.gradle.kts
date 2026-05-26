plugins {
    id("peepol.java-conventions")
    id("io.spring.dependency-management")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${Versions.SPRING_BOOT}")
    }
}
