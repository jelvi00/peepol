package org.peepol.controller.admin;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.peepol.controller.ControllerIT;
import org.peepol.domain.enums.Role;
import org.peepol.domain.enums.Status;
import org.peepol.domain.model.User;
import org.peepol.domain.repo.UserRepo;
import org.peepol.dto.LoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static io.restassured.RestAssured.given;

class PersonAdminControllerITest extends ControllerIT {

    private String token;

    @BeforeAll
    static void setup(
            @Autowired UserRepo userRepo,
            @Autowired PasswordEncoder passwordEncoder
    ) {

        userRepo.deleteAllInBatch();
        User admin = new User();
        admin.setUsername("administrator");
        admin.setPassword(passwordEncoder.encode("AdminPass123!"));
        admin.setRole(Role.ADMIN);
        admin.setStatus(Status.ENABLED);
        admin.setCreatedBy("_system_");
        userRepo.save(admin);

    }

    @BeforeEach
    void doLogin() {
        token = given()
                .contentType(ContentType.JSON)
                .body(new LoginDTO.Request("administrator", "AdminPass123!"))
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }


    @Test
    void listPersonsAsAdmin() {
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/admin/persons")
        .then()
                .statusCode(200);
    }
}
