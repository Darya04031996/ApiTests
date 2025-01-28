package tests;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import models.lombok.CreateUserRequest;
import models.lombok.RegistrationRequest;
import models.lombok.UpdateUserRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import specs.LoginSpec;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;
import static specs.LoginSpec.*;

public class ReqresTests {

    private static RequestSpecification spec;

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://reqres.in";
    }

    @Test
    @DisplayName("Проверка аватаров всех пользователей на странице")
    void allUsersHaveValidAvatarsTest() {
        step("Проверяем, что все пользователи на странице имеют валидные аватары", () ->
                given()
                        .spec(LoginSpec.loginRequestSpec)
                        .when()
                        .get("/users?page=2")
                        .then()
                        .statusCode(200)
                        .body("data.every { it.avatar != null && it.avatar.startsWith('https://') }", is(true))
        );
    }

    @Test
    @DisplayName("Проверка, что email всех пользователей уникальны")
    void allUsersHaveUniqueEmailsTest() {
        step("Проверяем, что email пользователей уникальны", () ->
                given()
                        .spec(LoginSpec.loginRequestSpec)
                        .when()
                        .get("/users?page=2")
                        .then()
                        .statusCode(200)
                        .body("data.collect { it.email }.unique().size()", is(6))
        );
    }

    @Test
    @DisplayName("Ошибка при регистрации без пароля")
    void registrationWithoutPasswordTest() {
        RegistrationRequest authData = new RegistrationRequest("eve.holt@reqres.in", null);

        step("Отправляем запрос на регистрацию без пароля", () ->
                given()
                        .spec(LoginSpec.loginRequestSpec)
                        .body(authData)
                        .when()
                        .post("/register")
                        .then()
                        .spec(LoginSpec.missingPasswordResponseSpec)
                        .body("error", is("Missing password"))
        );
    }

    @Test
    @DisplayName("Получение данных несуществующего пользователя")
    void getNonExistingUserTest() {
        step("Проверяем, что запрос на несуществующего пользователя возвращает 404", () ->
                given()
                        .spec(LoginSpec.loginRequestSpec)
                        .when()
                        .get("/users/999")
                        .then()
                        .statusCode(404)
        );
    }

    @Test
    @DisplayName("Проверка успешного создания и обновления пользователя")
    void createAndUpdateUserTest() {
        UpdateUserRequest authData = new UpdateUserRequest("darya", "Middle QA");

        String userId = step("Создаём нового пользователя", () ->
                given()
                        .spec(LoginSpec.loginRequestSpec)
                        .body(authData)
                        .when()
                        .post("/users")
                        .then()
                        .statusCode(201)
                        .body("name", is("darya"))
                        .body("job", is("Middle QA"))
                        .extract().path("id")
        );

        authData.setName("darya");
        authData.setJob("Senior QA");

        step("Обновляем данные пользователя", () ->
                given()
                        .spec(LoginSpec.loginRequestSpec)
                        .body(authData)
                        .when()
                        .put("/users/" + userId)
                        .then()
                        .statusCode(200)
                        .body("name", is("darya"))
                        .body("job", is("Senior QA"))
        );
    }

    @Test
    @DisplayName("Создание пользователя с некорректным JSON")
    void createUserWithInvalidJsonTest() {
        String invalidJson = "{name: 'alex'}";

        step("Отправляем запрос с некорректным JSON", () ->
                given()
                        .spec(LoginSpec.loginRequestSpec)
                        .body(invalidJson)
                        .when()
                        .post("/users")
                        .then()
                        .statusCode(400)
        );
    }

    @Test
    @DisplayName("Проверка количества пользователей на странице")
    void userCountPerPageTest() {
        step("Проверяем количество пользователей на странице", () ->
                given()
                        .spec(LoginSpec.loginRequestSpec)
                        .when()
                        .get("/users?page=2")
                        .then()
                        .statusCode(200)
                        .body("data.size()", is(6))
                        .body("per_page", is(6))
        );
    }
}