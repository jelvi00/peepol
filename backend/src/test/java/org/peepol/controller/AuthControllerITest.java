package org.peepol.controller;


import io.restassured.http.ContentType;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.peepol.domain.enums.Role;
import org.peepol.domain.enums.Status;
import org.peepol.domain.model.User;
import org.peepol.domain.repo.UserRepo;
import org.peepol.dto.LoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


class AuthControllerITest extends ControllerIT {

    @BeforeAll
    static void setup(
            @Autowired UserRepo userRepo,
            @Autowired PasswordEncoder passwordEncoder
    ) {
        userRepo.deleteAllInBatch();
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("Password123!"));
        user.setRole(Role.USER);
        user.setStatus(Status.ENABLED);
        user.setCreatedBy("_system_");
        userRepo.save(user);
    }

    @Test
    void loginSuccess() {
        LoginDTO.Request request = new LoginDTO.Request("testuser", "Password123!");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("username", equalTo("testuser"))
                .body("role", equalTo("USER"));
    }

    @Test
    void loginFailure() {
        LoginDTO.Request request = new LoginDTO.Request("testuser", "WrongPassword");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }
}
