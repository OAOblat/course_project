package test;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import data.DataHelper;
import data.SQLHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import page.PaymentPage;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

public class CreditCardTest {
    private final SelenideElement notificationStatusOk = $(".notification_status_ok .notification__content");
    private final SelenideElement notification = $(".notification_visible .notification__content");
    private final SelenideElement notificationStatusError = $(".notification_status_error .notification__content");
    private final SelenideElement buttonPayByCreditCard = $(".button.button_view_extra.button_size_m.button_theme_alfa-on-white > span.button__content > span.button__text");
//  private final SelenideElement buttonPayByCreditCard = $(byXpath("//span[text()='Купить в кредит']"));
    private final SelenideElement headingCreditPay = $(byText("Кредит по данным карты"));
//  private final SelenideElement headingCreditPay = $(byXpath("//h3[text()='Кредит по данным карты']"));
    private final SelenideElement inputInvalid = $(".input_invalid");

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        open("http://localhost:8080");
        buttonPayByCreditCard.click();
        headingCreditPay.shouldBe(visible);
    }

    @Test
    @DisplayName("Успешная покупка с валидными данными карты со статусом APPROVED")
    void test2_1_SuccessfulPurchaseWithValidCardDataAndApprovedStatus() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);

        step("Шаг 1: Осуществление оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        var expectedStatus = "APPROVED";
        var actualStatus = SQLHelper.getStatusFromCreditPayment();
        var bankID = SQLHelper.getBankIDFromCreditPayment();
        var paymentID = SQLHelper.getIDFromOrder();
        assertAll(
                () ->
                        step("Шаг 2: Проверка успешного уведомления", () -> {
                            assertEquals("Операция одобрена Банком.", notification.getText());
                        }),
                () ->
                        step("Шаг 3: Проверка отсутствия ошибки", () -> {
                            assertFalse(notificationStatusError.isDisplayed());
                        }),
                () ->
                        step("Шаг 4: Проверка статуса платежа в БД", () -> {
                            assertEquals(expectedStatus, actualStatus);
                        }),
                () ->
                        step("Шаг 5: Проверка отражения платежа в таблице заказов в БД", () -> {
                            assertEquals(bankID, paymentID);
                        }));
    }

    @Test
    @DisplayName("Отклонение оплаты с номером карты со статусом DECLINED")
    void test2_2_PaymentRejectionWithCardNumberAndDeclinedStatus() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.declinedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);

        step("Шаг 1: Осуществление оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        var expectedStatus = "DECLINED";
        var actualStatus = SQLHelper.getStatusFromCreditPayment();
        var bankID = SQLHelper.getBankIDFromCreditPayment();
        var paymentID = SQLHelper.getIDFromOrder();

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("Банк отказал в проведении операции.", notificationStatusError.getText());
                        }),
                () ->
                        step("Шаг 3: Проверка видимости уведомления об ошибке", () -> {
                            assertTrue(notificationStatusError.isDisplayed());
                        }),
                () ->
                        step("Шаг 4: Проверка отсутствия видимости уведомления об успехе", () -> {
                            assertFalse(notificationStatusOk.isDisplayed());
                        }),
                () ->
                        step("Шаг 5: Проверка статуса платежа в БД", () -> {
                            assertEquals(expectedStatus, actualStatus);
                        }),
                () ->
                        step("Шаг 6: Проверка отсутствия платежа со статусом Declined в таблице заказов", () -> {
                            assertNotEquals(bankID, paymentID);
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты без указания номера карты")
    void test2_3_UnsuccessfulPaymentWithoutCardNumber() {
        var paymentPage = new PaymentPage();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo("", cardMonth, cardYear, cardHolderName, cardCode);

        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.errorPayByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("Номер карты\n" + "Неверный формат", inputInvalid.getText());
                        })
        );
    }

    @Test
    @DisplayName("Отклонение оплаты с недействительным номером карты")
    void test2_4_PaymentRejectionWithInvalidCardNumber() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.generateRandomCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Осуществление оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("Ошибка! Банк отказал в проведении операции.", notificationStatusError.getText());
                        }),
                () ->
                        step("Шаг 3: Проверка видимости уведомления об ошибке", () -> {
                            assertTrue(notificationStatusError.isDisplayed());
                        }),
                () ->
                        step("Шаг 4: Проверка отсутствия уведомления об успехе", () -> {
                            assertFalse(notificationStatusOk.isDisplayed());
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с 15-значным номером карты")
    void test2_5_UnsuccessfulPaymentAttemptWith15DigitCardNumber() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.generateInvalidCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.errorPayByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("Номер карты\n" + "Неверный формат", inputInvalid.getText());
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с буквами вместо цифр в номере карты")
    void test2_6_UnsuccessfulPaymentAttemptWithLettersInCardNumber() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.getCardNumberNotNumber().getCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.errorPayByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("Номер карты\n" + "Неверный формат", inputInvalid.getText());
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты без указания месяца")
    void test2_7_UnsuccessfulPaymentAttemptWithoutSpecifyingMonth() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, "", cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.errorPayByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("Месяц\n" + "Неверный формат", inputInvalid.getText());
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с истекшим сроком действия карты (месяц меньше текущего, год - текущий)")
    void test2_8_UnsuccessfulPaymentAttemptWithExpiredCardExpirationDate() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateExpiredCardMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateExpiredCardMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.errorPayByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("Месяц\n" + "Неверно указан срок действия карты", inputInvalid.getText());
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле МЕСЯЦ (один символ)")
    void test2_9_UnsuccessfulPaymentAttemptWithInvalidValueInMonthFieldOneSymbol() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateInvalidCardMonthOneSymbol().getCardMonth();
        var cardYear = DataHelper.generateExpiredCardMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.errorPayByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("Месяц\n" + "Неверный формат", inputInvalid.getText());
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле МЕСЯЦ (больше 12)")
    void test2_10_UnsuccessfulPaymentAttemptWithInvalidValueInMonthFieldSymbolMore12() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateInvalidCardMonthFrom13To99().getCardMonth();
        var cardYear = DataHelper.generateExpiredCardMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.errorPayByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("Месяц\n" + "Неверно указан срок действия карты", inputInvalid.getText());
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле МЕСЯЦ (00)")
    void test2_11_UnsuccessfulPaymentAttemptWithInvalidValueInMonthField00() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.getInvalidCardMonth00().getCardMonth();
        var cardYear = DataHelper.generateExpiredCardMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.errorPayByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("Месяц\n" + "Неверно указан срок действия карты", inputInvalid.getText());
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле МЕСЯЦ (буквы)")
    void test2_12_UnsuccessfulPaymentAttemptWithInvalidValueInMonthFieldLetters() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateInvalidCardMonthAndYearNotNumber().getCardMonth();
        var cardYear = DataHelper.generateExpiredCardMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.errorPayByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("Месяц\n" + "Неверный формат", inputInvalid.getText());
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты без указания года")
    void test2_13_UnsuccessfulPaymentAttemptWithoutSpecifyingYear() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardHolderName = DataHelper.generateRandomCardholderName().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, "", cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.errorPayByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("Год\n" + "Неверный формат", inputInvalid.getText());
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с истекшим сроком действия карты (год - из прошлого)")
    void test2_14_UnsuccessfulPaymentAttemptWithExpiredCardYearFromPast() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateInvalidCardMonthAndYearUntilValidYears().getCardMonth();
        var cardYear = DataHelper.generateInvalidCardMonthAndYearUntilValidYears().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.errorPayByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("Год\n" + "Истёк срок действия карты", inputInvalid.getText());
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле ГОД (> 5 лет от текущего года)")
    void test2_15_UnsuccessfulPaymentAttemptWithInvalidValueInYearFieldMoreThan5YearsFromCurrentYear() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateInvalidCardMonthAndYearAfterValidYears().getCardMonth();
        var cardYear = DataHelper.generateInvalidCardMonthAndYearAfterValidYears().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.errorPayByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("Год\n" + "Неверно указан срок действия карты", inputInvalid.getText());
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле ГОД (буквы)")
    void test2_16_UnsuccessfulPaymentAttemptWithInvalidValueInYearFieldLetters() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateInvalidCardMonthAndYearAfterValidYears().getCardMonth();
        var cardYear = DataHelper.generateInvalidCardMonthAndYearNotNumber().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.errorPayByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("Год\n" + "Неверный формат", inputInvalid.getText());
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты без указания владельца")
    void test2_17_UnsuccessfulPaymentAttemptWithoutSpecifyingCardholder() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, "", cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.errorPayByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("Владелец\n" + "Поле обязательно для заполнения", inputInvalid.getText());
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле ВЛАДЕЛЕЦ (цифры)")
    void test2_18_UnsuccessfulPaymentAttemptWithInvalidValueInCardHolderFieldNumbers() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.getInvalidCardholderNameNumber().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("Владелец\n" + "Неверный формат", inputInvalid.getText());
                        }),
                () ->
                        step("Шаг 3: Проверка видимости уведомления об ошибке", () -> {
                            assertTrue(inputInvalid.isDisplayed());
                        }),
                () ->
                        step("Шаг 4: Проверка отсутствия видимости успешной отправки формы", () -> {
                            assertFalse(notification.isDisplayed());
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле ВЛАДЕЛЕЦ (на кириллице)")
    void test2_19_UnsuccessfulPaymentAttemptWithInvalidValueInCardHolderFieldInCyrillic() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.getInvalidCardholderNameCyrillic().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("Владелец\n" + "Допустимы только латинские символы, пробелы и дефисы", inputInvalid.getText());
                        }),
                () ->
                        step("Шаг 3: Проверка видимости уведомления об ошибке", () -> {
                            assertTrue(inputInvalid.isDisplayed());
                        }),
                () ->
                        step("Шаг 4: Проверка отсутствия видимости успешной отправки формы", () -> {
                            assertFalse(notification.isDisplayed());
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле ВЛАДЕЛЕЦ (1 символ)")
    void test2_20_UnsuccessfulPaymentAttemptWithInvalidValueInCardHolderFieldOneSymbol() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.getInvalidCardholderNameOneSymbol().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("Владелец\n" + "Введенное имя некорректно", inputInvalid.getText());
                        }),
                () ->
                        step("Шаг 3: Проверка видимости уведомления об ошибке", () -> {
                            assertTrue(inputInvalid.isDisplayed());
                        }),
                () ->
                        step("Шаг 4: Проверка отсутствия видимости успешной отправки формы", () -> {
                            assertFalse(notification.isDisplayed());
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле ВЛАДЕЛЕЦ (более 26 символов)")
    void test2_21_UnsuccessfulPaymentAttemptWithInvalidValueInCardHolderFieldMoreThen26Symbol() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.getInvalidCardholderNameSymbolMore26().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("Владелец\n" + "Превышено допустимое количество символов", inputInvalid.getText());
                        }),
                () ->
                        step("Шаг 3: Проверка видимости уведомления об ошибке", () -> {
                            assertTrue(inputInvalid.isDisplayed());
                        }),
                () ->
                        step("Шаг 4: Проверка отсутствия видимости успешной отправки формы", () -> {
                            assertFalse(notification.isDisplayed());
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле ВЛАДЕЛЕЦ (спец. символы)")
    void test2_22_UnsuccessfulPaymentAttemptWithInvalidValueInCardHolderFieldSpecialSymbols() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.getInvalidCardholderNameSpecSymbol().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("Владелец\n" + "Допустимы только латинские символы, пробелы и дефисы", inputInvalid.getText());
                        }),
                () ->
                        step("Шаг 3: Проверка видимости уведомления об ошибке", () -> {
                            assertTrue(inputInvalid.isDisplayed());
                        }),
                () ->
                        step("Шаг 4: Проверка отсутствия видимости успешной отправки формы", () -> {
                            assertFalse(notification.isDisplayed());
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты без указания CVV/CVC")
    void test2_23_UnsuccessfulPaymentAttemptWithoutSpecifyingCVV() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName().getCardholderName();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, "");
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.errorPayByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("CVC/CVV\n" + "Неверный формат", inputInvalid.getText());
                        }),
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке в поле Владелец", () -> {
                            assertEquals("Владелец\n" + "Поле обязательно для заполнения", inputInvalid.getText());
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением CVV/CVC (1 цифра)")
    void test2_24_UnsuccessfulPaymentAttemptWithInvalidValueInCVVField1Digit() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName().getCardholderName();
        var cardCode = DataHelper.generateInvalidCardCodeOne().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.errorPayByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("CVC/CVV\n" + "Неверный формат", inputInvalid.getText());
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением CVV/CVC (буквы)")
    void test2_25_UnsuccessfulPaymentAttemptWithInvalidValueInCVVFieldLetters() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName().getCardholderName();
        var cardCode = DataHelper.generateInvalidCardCodeNotNumbers().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.errorPayByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("CVC/CVV\n" + "Неверный формат", inputInvalid.getText());
                        }),
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке в поле Владелец", () -> {
                            assertEquals("Владелец\n" + "Поле обязательно для заполнения", inputInvalid.getText());
                        })
        );
    }

    @Test
    @DisplayName("Успешная покупка с номером карты со статусом APPROVED, месяц и год - текущие")
    void test2_26_SuccessfulPurchaseWithCardNumberAndApprovedStatusCurrentMonthAndYear() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateCurrentMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateCurrentMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);

        step("Шаг 1: Осуществление оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        var expectedStatus = "APPROVED";
        var actualStatus = SQLHelper.getStatusFromCreditPayment();
        var bankID = SQLHelper.getBankIDFromCreditPayment();
        var paymentID = SQLHelper.getIDFromOrder();
        assertAll(
                () ->
                        step("Шаг 2: Проверка успешного уведомления", () -> {
                            assertEquals("Операция одобрена Банком.", notification.getText());
                        }),
                () ->
                        step("Шаг 3: Проверка отсутствия ошибки", () -> {
                            assertFalse(notificationStatusError.isDisplayed());
                        }),
                () ->
                        step("Шаг 4: Проверка статуса платежа в БД", () -> {
                            assertEquals(expectedStatus, actualStatus);
                        }),
                () ->
                        step("Шаг 5: Проверка отражения платежа в таблице заказов в БД", () -> {
                            assertEquals(bankID, paymentID);
                        }));
    }

    @Test
    @DisplayName("Успешная покупка с номером карты со статусом APPROVED, Владелец - разрешенные 2 символа")
    void test2_27_SuccessfulPurchaseWithCardNumberAndAPPROVEDStatusCardholder2Symbols() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateCurrentMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateCurrentMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.getValidCardholderNameTwoSymbol().getCardholderName();
        var cardCode = DataHelper.generateRandomCardCode().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);

        step("Шаг 1: Осуществление оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        var expectedStatus = "APPROVED";
        var actualStatus = SQLHelper.getStatusFromCreditPayment();
        var bankID = SQLHelper.getBankIDFromCreditPayment();
        var paymentID = SQLHelper.getIDFromOrder();
        assertAll(
                () ->
                        step("Шаг 2: Проверка успешного уведомления", () -> {
                            assertEquals("Операция одобрена Банком.", notification.getText());
                        }),
                () ->
                        step("Шаг 3: Проверка отсутствия ошибки", () -> {
                            assertFalse(notificationStatusError.isDisplayed());
                        }),
                () ->
                        step("Шаг 4: Проверка статуса платежа в БД", () -> {
                            assertEquals(expectedStatus, actualStatus);
                        }),
                () ->
                        step("Шаг 5: Проверка отражения платежа в таблице заказов в БД", () -> {
                            assertEquals(bankID, paymentID);
                        }));
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением CVV/CVC (2 цифры)")
    void test2_28_UnsuccessfulPaymentAttemptWithInvalidValueInCVVorCVCField2Digits() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber().getCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName().getCardholderName();
        var cardCode = DataHelper.generateInvalidCardCodeTwo().getCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.errorPayByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            assertEquals("CVC/CVV\n" + "Неверный формат", inputInvalid.getText());
                        })
        );
    }
}
