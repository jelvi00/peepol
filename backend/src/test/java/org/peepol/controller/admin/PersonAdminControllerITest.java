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
import org.peepol.dto.PersonDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

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

    @Test
    void adminCrudPersonOperations() {
        // 1. Create (POST)
        PersonDTO.AddRequest addRequest = new PersonDTO.AddRequest("Admin Added", "999888777", "Bio");
        Integer id = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(addRequest)
        .when()
                .post("/admin/persons")
        .then()
                .statusCode(201)
                .body("name", equalTo("Admin Added"))
                .extract().path("id");

        // 2. Get Detail
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/admin/persons/" + id + "/detail")
        .then()
                .statusCode(200)
                .body("name", equalTo("Admin Added"));

        // 3. Search
        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("q", "Admin")
        .when()
                .get("/admin/persons/search")
        .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));

        // 4. Update (PUT)
        PersonDTO.UpdateRequest updateRequest = new PersonDTO.UpdateRequest(id.longValue(), "Admin Updated", "999888777", "New Bio");
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(updateRequest)
        .when()
                .put("/admin/persons")
        .then()
                .statusCode(200)
                .body("name", equalTo("Admin Updated"));

        // 5. Delete
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .delete("/admin/persons/" + id + "/person")
        .then()
                .statusCode(200);

        // 6. Verify Deleted
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/admin/persons/" + id + "/detail")
        .then()
                .statusCode(200)
                .body("status", equalTo("DISABLED"));
    }
}
