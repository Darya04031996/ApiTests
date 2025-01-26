import io.restassured.RestAssured;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;


public class ReqresTests {
    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
    }
    @Test
    @DisplayName("Проверка  аватаров всех пользователей на странице")
    void allUsersHaveValidAvatarsTest() {
        given()
                .log().uri()

                .when()
                .get("/users?page=2")

                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("data.every { it.avatar != null && it.avatar.startsWith('https://') }", is(true));

    }
    @Test
    @DisplayName("Проверка, что  email всех пользователей на странице уникальный")
    void allUsersHaveUniqueEmailsTest() {
        given()
                .log().uri()

                .when()
                .get("/users?page=2")

                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("data.collect { it.email }.unique().size()", is(6));

    }
    @Test
    @DisplayName("Ошибка при регистрации без пароля")
    void registrationWithoutPasswordTest() {
        String registrationData = "{\"email\": \"eve.holt@reqres.in\"}";

        given()
                .body(registrationData)
                .contentType(JSON)
                .log().uri()

                .when()
                .post("/register")

                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("error", is("Missing password"));
    }
    @Test
    @DisplayName("Получение данных несуществующего пользователя")
    void getNonExistingUserTest() {
        given()
                .log().uri()

                .when()
                .get("/users/999")

                .then()
                .log().status()
                .log().body()
                .statusCode(404);
    }
    @Test
    @DisplayName("Проверка успешного создания и обновления пользователя")
    void createAndUpdateUserTest() {

        String createUserData = "{\"name\": \"darya\", \"job\": \"Middle QA\"}";

        String userId = given()
                .body(createUserData)
                .contentType(JSON)
                .log().uri()

                .when()
                .post("/users")

                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .body("name", is("darya"))
                .body("job", is("Middle QA"))
                .body("id", notNullValue())
                .body("createdAt", notNullValue())
                .extract().path("id");


        String updateUserData = "{\"name\": \"Dasha\", \"job\": \"Senior QA\"}";


        given()
                .body(updateUserData)
                .contentType(JSON)
                .log().uri()

                .when()
                .put("/users/" + userId)

                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("name", is("Dasha"))
                .body("job", is("Senior QA"))
                .body("updatedAt", notNullValue());
    }
    @Test
    @DisplayName("Создание пользователя с некорректным JSON")
    void createUserWithInvalidJsonTest() {
        String invalidJson = "{name: 'alex'}";

        given()
                .body(invalidJson)
                .contentType(JSON)
                .log().uri()

                .when()
                .post("/users")

                .then()
                .log().status()
                .log().body()
                .statusCode(400);
    }
    @Test
    @DisplayName("Проверка количества пользователей на странице")
    void userCountPerPageTest() {
        given()
                .log().uri()

                .when()
                .get("/users?page=2")

                .then()
                .statusCode(200)
                .body("data.size()", is(6))
                .body("per_page", is(6));  
    }
}
