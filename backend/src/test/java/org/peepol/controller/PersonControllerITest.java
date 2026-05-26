package org.peepol.controller;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.peepol.domain.enums.Role;
import org.peepol.domain.enums.Status;
import org.peepol.domain.model.User;
import org.peepol.domain.model.Person;
import org.peepol.domain.repo.UserRepo;
import org.peepol.domain.repo.PersonRepo;
import org.peepol.dto.LoginDTO;
import org.peepol.dto.PersonDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


class PersonControllerITest extends ControllerIT {

    @Autowired
    private PersonRepo personRepo;

    private String token;

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

    @BeforeEach
    void doLogin() {
        token = given()
                .contentType(ContentType.JSON)
                .body(new LoginDTO.Request("testuser", "Password123!"))
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    @AfterEach
    void tearDown() {
        personRepo.deleteAllInBatch();
    }

    @Test
    void crudPersonSuccess() {
        // Create
        PersonDTO.AddRequest addRequest = new PersonDTO.AddRequest("Alice Smith", "111222333", "Some bio");

        Integer id = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(addRequest)
        .when()
                .post("/persons")
        .then()
                .statusCode(201)
                .body("name", equalTo("Alice Smith"))
                .extract()
                .path("id");

        // Get Detail
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/persons/" + id + "/detail")
        .then()
                .statusCode(200)
                .body("name", equalTo("Alice Smith"));

        // List
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/persons")
        .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));

        // Update
        PersonDTO.UpdateRequest updateRequest = new PersonDTO.UpdateRequest(id.longValue(), "Alice Updated", "111222333", "Updated bio");
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(updateRequest)
        .when()
                .put("/persons")
        .then()
                .statusCode(200)
                .body("name", equalTo("Alice Updated"));

        // Delete
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .delete("/persons/" + id + "/person")
        .then()
                .statusCode(200);

        // Verify deleted (status should be DISABLED)
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/persons/" + id + "/detail")
        .then()
                .statusCode(200)
                .body("status", equalTo("DISABLED"));
    }

    @Test
    void searchPersonsSuccess() {
        personRepo.save(Person.builder().name("Searchable Person").phoneNumber("555666777").build());

        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("q", "Searchable")
        .when()
                .get("/persons/search")
        .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1))
                .body("[0].name", containsString("Searchable"));
    }

    @Test
    void findAllByStatusInReturnsDisabledPersons() {
        // Given: One enabled person and one disabled person
        Person enabled = Person.builder().name("Enabled Person").phoneNumber("111111111").build();
        enabled.setStatus(Status.ENABLED);
        personRepo.save(enabled);

        Person disabled = Person.builder().name("Disabled Person").phoneNumber("000000000").build();
        disabled.setStatus(Status.DISABLED);
        personRepo.save(disabled);

        // When: Querying for both statuses (0 and 1)
        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("status", "0,1")
        .when()
                .get("/persons")
        .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("name", hasItems("Enabled Person", "Disabled Person"))
                .body("status", hasItems("ENABLED", "DISABLED"));
    }

    @Test
    void addPersonInvalidPhoneNumber() {
        PersonDTO.AddRequest addRequest = new PersonDTO.AddRequest("Invalid Phone", "123-456", "Bio");

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(addRequest)
        .when()
                .post("/persons")
        .then()
                .statusCode(400);
    }
}
