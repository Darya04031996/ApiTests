package tests;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.lombok.RegistrationRequest;
import models.lombok.UpdateUserRequest;
import models.pojo.LoginBodyModel;
import models.pojo.LoginResponseModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import specs.LoginSpec;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static specs.LoginSpec.*;

public class ReqresTests {

    private static RequestSpecification spec;

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://reqres.in";
    }

    @Test
    @DisplayName("Успешный логин с правильными данными")
    void testSuccessfulLogin() {
        step("Отправляем запрос на логин с корректными данными", () -> {
            LoginBodyModel loginBody = new LoginBodyModel();
            loginBody.setEmail("eve.holt@reqres.in");
            loginBody.setPassword("cityslicka");

            Response response = given()
                    .spec(LoginSpec.loginRequestSpec)
                    .body(loginBody)
                    .post();

            step("Проверяем, что статус ответа равен 200 и токен присутствует", () -> {
                response.then().spec(LoginSpec.loginResponseSpec);

                LoginResponseModel loginResponse = response.as(LoginResponseModel.class);
                assertNotNull(loginResponse.getToken(), "Токен должен быть не null");
                assertTrue(loginResponse.getToken().length() > 0, "Токен должен быть не пустым");
            });
        });
    }

    @Test
    @DisplayName("Ошибка логина с неверными данными")
    void testLoginWithInvalidCredentials() {
        step("Отправляем запрос на логин с неверным email и паролем", () -> {
            LoginBodyModel loginBody = new LoginBodyModel();
            loginBody.setEmail("invalid@example.com");
            loginBody.setPassword("wrongpassword");

            Response response = given()
                    .spec(LoginSpec.loginRequestSpec)
                    .body(loginBody)
                    .post();

            step("Проверяем, что статус ответа равен 400 и сообщение об ошибке корректное", () -> {
                response.then()
                        .spec(LoginSpec.invalidCredentialsResponseSpec);

                String error = response.jsonPath().getString("error");
                assertEquals("Invalid login credentials", error, "Сообщение об ошибке некорректно");
            });
        });
    }

    @Test
    @DisplayName("Успешная регистрация нового пользователя")
    void testSuccessfulRegistration() {
        step("Отправляем запрос на регистрацию с корректными данными", () -> {
            RegistrationRequest registrationBody = new RegistrationRequest();
            registrationBody.setEmail("newuser@example.com");
            registrationBody.setPassword("newpassword");

            Response response = given()
                    .spec(LoginSpec.loginRequestSpec)
                    .body(registrationBody)
                    .post("https://reqres.in/api/register");

            step("Проверяем, что статус ответа равен 201 и токен присутствует", () -> {
                response.then().spec(LoginSpec.registrationResponseSpec);

                LoginResponseModel loginResponse = response.as(LoginResponseModel.class);
                assertNotNull(loginResponse.getToken(), "Токен должен быть не null");
                assertTrue(loginResponse.getToken().length() > 0, "Токен должен быть не пустым");
            });
        });
    }

    @Test
    @DisplayName("Ошибка регистрации без пароля")
    void testRegistrationWithMissingPassword() {
        step("Отправляем запрос на регистрацию без указания пароля", () -> {
            RegistrationRequest registrationBody = new RegistrationRequest();
            registrationBody.setEmail("newuser@example.com");

            Response response = given()
                    .spec(LoginSpec.loginRequestSpec)
                    .body(registrationBody)
                    .post("https://reqres.in/api/register");

            step("Проверяем, что статус ответа равен 400 и сообщение об ошибке корректное", () -> {
                response.then().spec(LoginSpec.missingPasswordResponseSpec);

                String error = response.jsonPath().getString("error");
                assertEquals("Missing password", error, "Сообщение об ошибке некорректно");
            });
        });
    }

    @Test
    @DisplayName("Успешное обновление данных пользователя")
    void testUpdateUser() {
        step("Отправляем запрос на обновление данных пользователя", () -> {
            UpdateUserRequest updateUserBody = new UpdateUserRequest();
            updateUserBody.setName("John Doe");
            updateUserBody.setJob("Software Developer");

            Response response = given()
                    .spec(LoginSpec.loginRequestSpec)
                    .body(updateUserBody)
                    .put("https://reqres.in/api/users/2");

            step("Проверяем, что статус ответа равен 200 и данные пользователя обновлены", () -> {
                response.then().statusCode(200);

                UpdateUserRequest updatedUser = response.as(UpdateUserRequest.class);
                assertEquals("John Doe", updatedUser.getName(), "Имя пользователя не обновлено");
                assertEquals("Software Developer", updatedUser.getJob(), "Должность пользователя не обновлена");
            });
        });
    }
}