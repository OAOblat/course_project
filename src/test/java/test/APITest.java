package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.APIHelper;
import data.DataHelper;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import page.PaymentPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;

public class APITest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Успешная покупка с валидными данными карты со статусом APPROVED по вкладке КУПИТЬ")
    void test1() {
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        Response response = APIHelper.payByDebitCard(cardInfo, 200);
        String actualStatus = response.path("status");
        String expectedStatus = "APPROVED";
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("Отклонение оплаты с номером карты со статусом DECLINED по вкладке КУПИТЬ")
    void test2() {
        var cardNumber = DataHelper.declinedCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        Response response = APIHelper.payByDebitCard(cardInfo, 200);
        String actualStatus = response.path("status");
        String expectedStatus = "DECLINED";
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("Успешная покупка с валидными данными карты со статусом APPROVED по вкладке КУПИТЬ В КРЕДИТ")
    void test3() {
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        Response response = APIHelper.payByCreditCard(cardInfo, 200);
        String actualStatus = response.path("status");
        String expectedStatus = "APPROVED";
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("Отклонение оплаты с номером карты со статусом DECLINED по вкладке КУПИТЬ В КРЕДИТ")
    void test4() {
        var cardNumber = DataHelper.declinedCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        Response response = APIHelper.payByCreditCard(cardInfo, 200);
        String actualStatus = response.path("status");
        String expectedStatus = "DECLINED";
        Assertions.assertEquals(expectedStatus, actualStatus);
    }
}


