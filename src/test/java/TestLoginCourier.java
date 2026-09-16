import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TestLoginCourier {

    static Courier courier = new Courier("test140", "123456", "Polly");
    private Integer courierId = null;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru/";
        given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201);
    }

    // ----------------------------------------------------

    //1. если какого-то поля нет, запрос возвращает ошибку;
    @Test
    public void loginCourierErrorNotEnoughData() {
        sendLoginRequestWithoutLogin();
        sendLoginRequestWithoutPassword();
    }

    @Step("Авторизация без поля login невозможна - ошибка 400")
    private void sendLoginRequestWithoutLogin() {
            String bodyWithoutLogin = "{\"password\":\"123456\"}";
            given()
                    .header("Content-type", "application/json")
                    .body(bodyWithoutLogin)
                    .when()
                    .post("/api/v1/courier/login")
                    .then()
                    .statusCode(400)
                    .body("message", is("Недостаточно данных для входа"));
        }

        @Step("Авторизация без поля password невозможна - ошибка 400")
        private void sendLoginRequestWithoutPassword() {
            String bodyWithoutPassword = "{\"login\":\"123456\", \"password\":\"\"}";
            given()
                    .header("Content-type", "application/json")
                    .body(bodyWithoutPassword)
                    .when()
                    .post("/api/v1/courier/login")
                    .then()
                    .statusCode(400)
                    .body("message", is("Недостаточно данных для входа"));
        }

        // ----------------------------------------------------

    // 2. система вернёт ошибку, если неправильно указать логин или пароль;
    //+ если авторизоваться под несуществующим пользователем, запрос возвращает ошибку;

    @Test
    public void loginCourierErrorIncorrectData() {
        sendLoginRequestWithoutIncorrectLogin();
        sendLoginRequestWithoutIncorrectPassword();
    }

    @Step("Авторизация с неправильным логином - ошибка 404")
    private void sendLoginRequestWithoutIncorrectLogin() {
        String bodyIncorrectLogin = "{\"login\":\"test140F\", \"password\":\"123456\"}";
        given()
                .header("Content-type", "application/json")
                .body(bodyIncorrectLogin)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("message", is("Учетная запись не найдена"));
    }

    @Step("Авторизация с неправильным паролем - ошибка 404")
    private void sendLoginRequestWithoutIncorrectPassword() {
        String bodyIncorrectPassword = "{\"login\":\"test140\", \"password\":\"111111\"}";
        given()
                .header("Content-type", "application/json")
                .body(bodyIncorrectPassword)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("message", is("Учетная запись не найдена"));
    }

    // ----------------------------------------------------

    // 3. курьер может авторизоваться;
    //+ для авторизации нужно передать все обязательные поля;
    //+ успешный запрос возвращает id.

    @Test
    @Step("Успешная авторизация курьера")
    public void loginCourierSuccess() {
        String bodyCorrectLoginAndPassword = "{\"login\":\"test140\", \"password\":\"123456\"}";
        given()
                .header("Content-type", "application/json")
                .body(bodyCorrectLoginAndPassword)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }


    @AfterEach
    public void deleteCourier() {

        courierId =
                given()
                        .header("Content-Type", "application/json")
                        .body(courier)
                        .when()
                        .post("/api/v1/courier/login")
                        .then()
                        .extract()
                        .path("id");

            given()
                    .header("Content-Type", "application/json")
                    .when()
                    .delete("/api/v1/courier/" + courierId)
                    .then()
                    .statusCode(200);
        }


}

