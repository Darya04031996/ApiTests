package tests;


import io.restassured.RestAssured;
import models.lombok.LoginBodyLombokModel;
import models.lombok.LoginResponseLombokModel;
import models.lombok.MissingPasswordModel;
import models.pojo.LoginBodyModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import specs.LoginSpec;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReqresTests {

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://reqres.in";
    }

    @Test
    void successfulLoginTest() {
        LoginBodyLombokModel authData = new LoginBodyLombokModel();
        authData.setEmail("eve.holt@reqres.in");
        authData.setPassword("cityslicka");

        LoginResponseLombokModel response = step("Make request for successful login", () ->
                given(LoginSpec.loginRequestSpec)
                        .body(authData)

                        .when()
                        .post()

                        .then()
                        .spec(LoginSpec.loginResponseSpec)
                        .extract().as(LoginResponseLombokModel.class)
        );

        step("Check response token", () ->
                assertEquals("QpwL5tke4Pnpja7X4", response.getToken())
        );
    }

    @Test
    void missingPasswordTest() {
        LoginBodyLombokModel authData = new LoginBodyLombokModel();
        authData.setEmail("eve.holt@reqres.in");

        MissingPasswordModel response = step("Make request for login with missing password", () ->
                given(LoginSpec.loginRequestSpec)
                        .body(authData)

                        .when()
                        .post()

                        .then()
                        .spec(LoginSpec.missingPasswordResponseSpec)
                        .extract().as(MissingPasswordModel.class)
        );

        step("Check error message in response", () ->
                assertEquals("Missing password", response.getError())
        );
    }

    @Test
    void unsuccessfulLoginWithInvalidCredentialsTest() {
        LoginBodyLombokModel authData = new LoginBodyLombokModel();
        authData.setEmail("wrong.email@reqres.in");
        authData.setPassword("wrongpassword");

        MissingPasswordModel response = step("Make request for login with invalid credentials", () ->
                given(LoginSpec.loginRequestSpec)
                        .body(authData)

                        .when()
                        .post()

                        .then()
                        .spec(LoginSpec.invalidCredentialsResponseSpec)
                        .extract().as(MissingPasswordModel.class)
        );

        step("Check error message in response", () ->
                assertEquals("user not found", response.getError())
        );
    }

    @Test
    void successfulUserCreationTest() {
        LoginBodyModel authData = new LoginBodyModel();
        authData.setName("morpheus");
        authData.setJob("leader");

        LoginBodyModel response = step("Make request for user creation", () ->
                given()
                        .contentType(JSON)
                        .log().uri()
                        .log().body()
                        .body(authData)

                        .when()
                        .post("/api/users")

                        .then()
                        .statusCode(201) // CREATED
                        .extract().as(LoginBodyModel.class)
        );
                step("Check response contains correct name and job", () -> {
                    assertEquals("morpheus", response.getName());
                    assertEquals("leader", response.getJob());
                });


    }

    @Test
    void successfulUserUpdateTest() {
        LoginBodyModel authData = new LoginBodyModel();
        authData.setName("morpheus");
        authData.setJob("zion resident");

        LoginBodyModel response = step("Make request to update user information", () ->
                given()
                        .contentType(JSON)
                        .log().uri()
                        .log().body()
                        .body(authData)

                        .when()
                        .put("/api/users/2")

                        .then()
                        .statusCode(200) // OK
                        .extract().as(LoginBodyModel.class));


        step("Check response contains updated name and job", () -> {
            assertEquals("morpheus", response.getName());
            assertEquals("zion resident", response.getJob());
        });
    }
}