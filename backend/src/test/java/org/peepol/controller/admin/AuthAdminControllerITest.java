package org.peepol.controller.admin;


import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.peepol.controller.ControllerIT;
import org.peepol.domain.enums.Role;
import org.peepol.domain.enums.Status;
import org.peepol.domain.model.User;
import org.peepol.domain.repo.UserRepo;
import org.peepol.dto.LoginDTO;
import org.peepol.dto.RegistrationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class AuthAdminControllerITest extends ControllerIT {

    @BeforeAll
    static void setUp(
            @Autowired UserRepo userRepo,
            @Autowired PasswordEncoder passwordEncoder
    ) {

        userRepo.deleteAll();
        User admin = new User();
        admin.setUsername("administrator");
        admin.setPassword(passwordEncoder.encode("AdminPass123!"));
        admin.setRole(Role.ADMIN);
        admin.setStatus(Status.ENABLED);
        admin.setCreatedBy("_system_");
        userRepo.save(admin);
    }

    @Test
    void registerAndLoginSuccess() {
        String adminToken = given()
                .contentType(ContentType.JSON)
                .body(new LoginDTO.Request("administrator", "AdminPass123!"))
                .post("/admin/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");

        RegistrationDTO.Request regRequest = new RegistrationDTO.Request("newuser123", "Pass123!", "USER");

        // Register
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(regRequest)
                .when()
                .post("/admin/auth/registration")
                .then()
                .statusCode(201)
                .body("username", equalTo("newuser123"));
    }
}
