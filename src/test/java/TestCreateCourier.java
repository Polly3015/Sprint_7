import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TestCreateCourier {

    Courier courier = new Courier("test140", "123456", "Polly");
    private Integer courierId = null;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru/";
    }

    // ----------------------------------------------------

    // Если одного из полей нет, запрос возвращает ошибку;
    @Test
    public void testCreateCourierError() {
        sendCreateRequestWithoutLogin();
        sendCreateRequestWithoutPassword();
    }

    @Step("Создание курьера без поля login невозможно - ошибка 400")
    private void sendCreateRequestWithoutLogin() {
        String bodyWithoutLogin = "{\"password\":\"123456\",\"firstName\":\"Polly\"}";
        given()
                .header("Content-type", "application/json")
                .body(bodyWithoutLogin)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", is("Недостаточно данных для создания учетной записи"));
    }

    @Step("Создание курьера без поля password невозможно - ошибка 400")
    private void sendCreateRequestWithoutPassword() {
        String bodyWithoutPassword = "{\"login\":\"test140\",\"firstName\":\"Polly\"}";
                given()
                .header("Content-type", "application/json")
                .body(bodyWithoutPassword)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", is("Недостаточно данных для создания учетной записи"));
}

    // ----------------------------------------------------


    // Успешное создание курьера: курьера можно создать
    // + запрос возвращает правильный код ошибки
    // + успешный запрос возвращает ok: true
    @Test
    @Step("Успешное создание курьера")
    public void testCreateCourierSuccess() {

        given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", is(true));
    }

    // ----------------------------------------------------

    // Нельзя создать двух одинаковых курьеров;
    // Если создать пользователя с логином, который уже есть, возвращается ошибка
    @Test
    @Step("Повторное создание курьера с одинаковым логином неовзможно - ошибка 409")
    public void testErrorCreateSameCourier() {
        given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", is(true));;

        given()
                .header("Content-Type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(409)
                .body("message", is("Этот логин уже используется. Попробуйте другой."));
    }

    // ----------------------------------------------------

    @AfterEach
    public void deleteCourier(){

        courierId =
                given()
                .header("Content-Type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .extract()
                .path("id");

        if (courierId == null) {
            return; // Если нет id, то выходим из метода и не удаляем курьера
        } else {
            // Если есть id, то удаляем курьера
            given()
                    .header("Content-Type", "application/json")
                    .when()
                    .delete("/api/v1/courier/" + courierId)
                    .then()
                    .statusCode(200);
        }
    }


}
