package tests;
import io.restassured.RestAssured;
import models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;


import specs.ApiSpec;



@Tag("simple")
public class ReqresTests {

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
    }

    @Test
    @DisplayName("Successful login test")
    void successfulLoginTest() {
        LoginRequestModel requestData = new LoginRequestModel();
        requestData.setEmail("eve.holt@reqres.in");
        requestData.setPassword("cityslicka");

        LoginResponseModel response = step("Make request for successful login", () ->
                given(ApiSpec.baseRequestSpec)
                        .body(requestData)
                        .when()
                        .post("/login")
                        .then()
                        .spec(ApiSpec.successResponseSpec)
                        .extract().as(LoginResponseModel.class)
        );

        step("Check response token", () ->
                assertThat(response.getToken()).isEqualTo("QpwL5tke4Pnpja7X4")
        );
    }

    @Test
    @DisplayName("Login test with missing password")
    void missingPasswordTest() {
        LoginRequestModel requestData = new LoginRequestModel();
        requestData.setEmail("eve.holt@reqres.in");

        ErrorResponseModel response = step("Make request for login with missing password", () ->
                given(ApiSpec.baseRequestSpec)
                        .body(requestData)
                        .when()
                        .post("/login")
                        .then()
                        .spec(ApiSpec.errorResponseSpec)
                        .extract().as(ErrorResponseModel.class)
        );

        step("Check error message in response", () ->
                assertThat(response.getError()).isEqualTo("Missing password")
        );
    }

    @Test
    @DisplayName("Unsuccessful login with invalid credentials")
    void unsuccessfulLoginWithInvalidCredentialsTest() {
        LoginRequestModel requestData = new LoginRequestModel();
        requestData.setEmail("wrong.email@reqres.in");
        requestData.setPassword("wrongpassword");

        ErrorResponseModel response = step("Make request for login with invalid credentials", () ->
                given(ApiSpec.baseRequestSpec)
                        .body(requestData)
                        .when()
                        .post("/login")
                        .then()
                        .spec(ApiSpec.errorResponseSpec)
                        .extract().as(ErrorResponseModel.class)
        );

        step("Check error message in response", () ->
                assertThat(response.getError()).isEqualTo("user not found")
        );
    }

    @Test
    @DisplayName("Successful user creation test")
    void successfulUserCreationTest() {
        UserRequestModel requestData = new UserRequestModel();
        requestData.setName("morpheus");
        requestData.setJob("leader");

        UserResponseModel response = step("Make request for user creation", () ->
                given(ApiSpec.baseRequestSpec)
                        .body(requestData)
                        .when()
                        .post("/users")
                        .then()
                        .spec(ApiSpec.createdResponseSpec)
                        .extract().as(UserResponseModel.class)
        );

        step("Check response contains correct name and job", () -> {
            assertThat(response.getName()).isEqualTo("morpheus");
            assertThat(response.getJob()).isEqualTo("leader");
        });
    }

    @Test
    @DisplayName("Successful user update test")
    void successfulUserUpdateTest() {
        UserRequestModel requestData = new UserRequestModel();
        requestData.setName("morpheus");
        requestData.setJob("zion resident");

        UserResponseModel response = step("Make request to update user information", () ->
                given(ApiSpec.baseRequestSpec)
                        .body(requestData)
                        .when()
                        .put("/users/2")
                        .then()
                        .spec(ApiSpec.successResponseSpec)
                        .extract().as(UserResponseModel.class)
        );

        step("Check response contains updated name and job", () -> {
            assertThat(response.getName()).isEqualTo("morpheus");
            assertThat(response.getJob()).isEqualTo("zion resident");
        });
    }
}