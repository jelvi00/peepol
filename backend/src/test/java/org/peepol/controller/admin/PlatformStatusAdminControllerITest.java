package org.peepol.controller.admin;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.peepol.client.WebAppService;
import org.peepol.controller.ControllerIT;
import org.peepol.domain.enums.Role;
import org.peepol.domain.enums.Status;
import org.peepol.domain.model.User;
import org.peepol.domain.repo.UserRepo;
import org.peepol.dto.LoginDTO;
import org.peepol.dto.PlatformStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

class PlatformStatusAdminControllerITest extends ControllerIT {

    @MockitoBean
    private WebAppService webAppService;

    private String adminToken;
    private String userToken;

    @BeforeAll
    static void setupUsers(
            @Autowired UserRepo userRepo,
            @Autowired PasswordEncoder passwordEncoder
    ) {
        userRepo.deleteAllInBatch();

        User admin = new User();
        admin.setUsername("admin_status");
        admin.setPassword(passwordEncoder.encode("AdminPass123!"));
        admin.setRole(Role.ADMIN);
        admin.setStatus(Status.ENABLED);
        admin.setCreatedBy("_system_");
        userRepo.save(admin);

        User user = new User();
        user.setUsername("user_status");
        user.setPassword(passwordEncoder.encode("UserPass123!"));
        user.setRole(Role.USER);
        user.setStatus(Status.ENABLED);
        user.setCreatedBy("_system_");
        userRepo.save(user);
    }

    @BeforeEach
    void doLogin() {
        adminToken = given()
                .contentType(ContentType.JSON)
                .body(new LoginDTO.Request("admin_status", "AdminPass123!"))
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");

        userToken = given()
                .contentType(ContentType.JSON)
                .body(new LoginDTO.Request("user_status", "UserPass123!"))
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    @Test
    void getStatusAsAdminSuccess() {
        when(webAppService.health()).thenReturn(new PlatformStatusDTO.WebAppHealth("UP", "Healthy"));

        given()
                .header("Authorization", "Bearer " + adminToken)
        .when()
                .get("/admin/status")
        .then()
                .statusCode(200)
                .body("api.status", equalTo("OK"))
                .body("web.status", equalTo("UP"));
    }

    @Test
    void getStatusAsAdminWhenWebDown() {
        when(webAppService.health()).thenThrow(new RuntimeException("Connection refused"));

        given()
                .header("Authorization", "Bearer " + adminToken)
        .when()
                .get("/admin/status")
        .then()
                .statusCode(200)
                .body("api.status", equalTo("OK"))
                .body("web.status", equalTo("DOWN"));
    }

    @Test
    void getStatusAsUserForbidden() {
        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .get("/admin/status")
        .then()
                .statusCode(403);
    }

    @Test
    void getStatusUnauthenticatedUnauthorized() {
        given()
        .when()
                .get("/admin/status")
        .then()
                .statusCode(403);
    }
}
