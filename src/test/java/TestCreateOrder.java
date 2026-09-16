import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TestCreateOrder {

    static Order order = new Order("Иван", "Иванович", "Арбат", "3", "891611111111", 5, "2026-09-18", "Плачу наличными", new String[]{"BLACK", "GRAY"});

    private static Stream<List<String>> provideColorOptions() {
        return Stream.of(
                List.of("BLACK"),
                List.of("GREY"),
                List.of("BLACK", "GREY"),
                List.of()
        );
    }

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru/";
    }

    @ParameterizedTest
    @MethodSource("provideColorOptions")
    @Step("Создание задаказа с указанным цветом самоката")
    public void createOrderSuccess(List<String> colors) {

        String[] colorArray = colors.toArray(new String[0]);

        Order order = new Order(
                "Иван",
                "Иванович",
                "Арбат",
                "3",
                "891611111111",
                5,
                "2026-09-18",
                "Плачу наличными",
                colorArray
        );

        given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/v1/orders")
                .then()
                .statusCode(201)
                .body("track", notNullValue());

    }

}
