package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.DataHelper;
import data.SQLHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import page.PaymentPage;

import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

public class CreditCardTest {
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
        var paymentPage = new PaymentPage();
        paymentPage.openCreditPayPage();
    }
/*
    @Test
    @DisplayName("Успешная покупка с валидными данными карты со статусом APPROVED")
    void test2_1_SuccessfulPurchaseWithValidCardDataAndApprovedStatus() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);

        step("Шаг 1: Осуществление оплаты", () -> {
            paymentPage.payByCard(cardInfo);
            paymentPage.waitingForNotification();
        });

        var expectedStatus = "APPROVED";
        var actualStatus = SQLHelper.getInfoFromCreditPayment().getStatus();
        var transactionID = SQLHelper.getInfoFromCreditPayment().getBank_id();
        var paymentID = SQLHelper.getInfoFromOrder().getPayment_id();
        assertAll(
                () ->
                        step("Шаг 2: Проверка успешного уведомления", () -> {
                            paymentPage.checkNotificationText("Операция одобрена Банком.");
                        }),
                () ->
                        step("Шаг 3: Проверка отсутствия ошибки", paymentPage::checkErrorNotificationInvisibility),
                () ->
                        step("Шаг 4: Проверка статуса платежа в БД", () -> {
                            assertEquals(expectedStatus, actualStatus);
                        }),
                () ->
                        step("Шаг 5: Проверка отражения платежа в таблице заказов в БД", () -> {
                            assertEquals(transactionID, paymentID);
                        }));
    }

    @Test
    @DisplayName("Отклонение оплаты с номером карты со статусом DECLINED")
    void test1_2_PaymentRejectionWithCardNumberAndDeclinedStatus() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.declinedCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);

        step("Шаг 1: Осуществление оплаты", () -> {
            paymentPage.payByCard(cardInfo);
            paymentPage.waitingForNotification();
        });

        var expectedStatus = "DECLINED";
        var actualStatus = SQLHelper.getInfoFromCreditPayment().getStatus();
        var transactionID = SQLHelper.getInfoFromCreditPayment().getBank_id();
        var paymentID = SQLHelper.getInfoFromOrder().getPayment_id();

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkNotificationText("Банк отказал в проведении операции.");
                        }),
                () ->
                        step("Шаг 3: Проверка видимости уведомления об ошибке", paymentPage::checkErrorNotificationVisibility),
                () ->
                        step("Шаг 4: Проверка отсутствия видимости уведомления об успехе", paymentPage::checkOkNotificationInvisibility),
                () ->
                        step("Шаг 5: Проверка статуса платежа в БД", () -> {
                            assertEquals(expectedStatus, actualStatus);
                        }),
                () ->
                        step("Шаг 6: Проверка отсутствия платежа со статусом Declined в таблице заказов", () -> {
                            assertNotEquals(transactionID, paymentID);
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты без указания номера карты")
    void test1_3_UnsuccessfulPaymentWithoutCardNumber() {
        var paymentPage = new PaymentPage();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo("", cardMonth, cardYear, cardHolderName, cardCode);

        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Номер карты Неверный формат");
                        })
        );
    }

    @Test
    @DisplayName("Отклонение оплаты с недействительным номером карты")
    void test1_4_PaymentRejectionWithInvalidCardNumber() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.generateRandomCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Осуществление оплаты", () -> {
            paymentPage.payByCard(cardInfo);
            paymentPage.waitingForNotification();
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkNotificationText("Ошибка! Банк отказал в проведении операции.");
                        }),
                () ->
                        step("Шаг 3: Проверка отсутствия видимости уведомления об успехе", paymentPage::checkOkNotificationInvisibility),
                () ->
                        step("Шаг 4: Проверка видимости уведомления об ошибке", paymentPage::checkErrorNotificationVisibility)
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с 15-значным номером карты")
    void test1_5_UnsuccessfulPaymentAttemptWith15DigitCardNumber() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.generateInvalidCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Номер карты Неверный формат");
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с буквами вместо цифр в номере карты")
    void test1_6_UnsuccessfulPaymentAttemptWithLettersInCardNumber() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.getCardNumberNotNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Номер карты Неверный формат");
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты без указания месяца")
    void test1_7_UnsuccessfulPaymentAttemptWithoutSpecifyingMonth() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, "", cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Месяц Неверный формат");
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с истекшим сроком действия карты (месяц меньше текущего, год - текущий)")
    void test1_8_UnsuccessfulPaymentAttemptWithExpiredCardExpirationDate() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateExpiredCardMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateExpiredCardMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Месяц Неверно указан срок действия карты");
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле МЕСЯЦ (один символ)")
    void test1_9_UnsuccessfulPaymentAttemptWithInvalidValueInMonthFieldOneSymbol() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateInvalidCardMonthOneSymbol();
        var cardYear = DataHelper.generateExpiredCardMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Месяц Неверный формат");
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле МЕСЯЦ (больше 12)")
    void test1_10_UnsuccessfulPaymentAttemptWithInvalidValueInMonthFieldSymbolMore12() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateInvalidCardMonthFrom13To99();
        var cardYear = DataHelper.generateExpiredCardMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Месяц Неверно указан срок действия карты");
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле МЕСЯЦ (00)")
    void test1_11_UnsuccessfulPaymentAttemptWithInvalidValueInMonthField00() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.getInvalidCardMonth00();
        var cardYear = DataHelper.generateExpiredCardMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Месяц Неверно указан срок действия карты");
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле МЕСЯЦ (буквы)")
    void test1_12_UnsuccessfulPaymentAttemptWithInvalidValueInMonthFieldLetters() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateInvalidCardMonthAndYearNotNumber().getCardMonth();
        var cardYear = DataHelper.generateExpiredCardMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Месяц Неверный формат");
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты без указания года")
    void test1_13_UnsuccessfulPaymentAttemptWithoutSpecifyingYear() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, "", cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Год Неверный формат");
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с истекшим сроком действия карты (год - из прошлого)")
    void test1_14_UnsuccessfulPaymentAttemptWithExpiredCardYearFromPast() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateInvalidCardMonthAndYearUntilValidYears().getCardMonth();
        var cardYear = DataHelper.generateInvalidCardMonthAndYearUntilValidYears().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Год Истёк срок действия карты");
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле ГОД (> 5 лет от текущего года)")
    void test1_15_UnsuccessfulPaymentAttemptWithInvalidValueInYearFieldMoreThan5YearsFromCurrentYear() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateInvalidCardMonthAndYearAfterValidYears().getCardMonth();
        var cardYear = DataHelper.generateInvalidCardMonthAndYearAfterValidYears().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Год Неверно указан срок действия карты");
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле ГОД (буквы)")
    void test1_16_UnsuccessfulPaymentAttemptWithInvalidValueInYearFieldLetters() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateInvalidCardMonthAndYearAfterValidYears().getCardMonth();
        var cardYear = DataHelper.generateInvalidCardMonthAndYearNotNumber().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Год Неверный формат");
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты без указания владельца")
    void test1_17_UnsuccessfulPaymentAttemptWithoutSpecifyingCardholder() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, "", cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Владелец Поле обязательно для заполнения");
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле ВЛАДЕЛЕЦ (цифры)")
    void test1_18_UnsuccessfulPaymentAttemptWithInvalidValueInCardHolderFieldNumbers() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.getInvalidCardholderNameNumber();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
            paymentPage.waitingForNotification();
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Владелец Неверный формат");
                        }),
                () ->
                        step("Шаг 3: Проверка отсутствия видимости успешной отправки формы", paymentPage::checkOkNotificationInvisibility)
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле ВЛАДЕЛЕЦ (на кириллице)")
    void test1_19_UnsuccessfulPaymentAttemptWithInvalidValueInCardHolderFieldInCyrillic() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.getInvalidCardholderNameCyrillic();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
            paymentPage.waitingForNotification();
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Владелец Допустимы только латинские символы, пробелы и дефисы");
                        }),
                () ->
                        step("Шаг 3: Проверка отсутствия видимости успешной отправки формы", paymentPage::checkOkNotificationInvisibility)
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле ВЛАДЕЛЕЦ (1 символ)")
    void test1_20_UnsuccessfulPaymentAttemptWithInvalidValueInCardHolderFieldOneSymbol() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.getInvalidCardholderNameOneSymbol();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
            paymentPage.waitingForNotification();
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Владелец Введенное имя некорректно");
                        }),
                () ->
                        step("Шаг 3: Проверка отсутствия видимости успешной отправки формы", paymentPage::checkOkNotificationInvisibility)
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле ВЛАДЕЛЕЦ (более 26 символов)")
    void test1_21_UnsuccessfulPaymentAttemptWithInvalidValueInCardHolderFieldMoreThen26Symbol() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.getInvalidCardholderNameSymbolMore26();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
            paymentPage.waitingForNotification();
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Владелец Превышено допустимое количество символов");
                        }),
                () ->
                        step("Шаг 3: Проверка отсутствия видимости успешной отправки формы", paymentPage::checkOkNotificationInvisibility)
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением в поле ВЛАДЕЛЕЦ (спец. символы)")
    void test1_22_UnsuccessfulPaymentAttemptWithInvalidValueInCardHolderFieldSpecialSymbols() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.getInvalidCardholderNameSpecSymbol();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
            paymentPage.waitingForNotification();
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Владелец Допустимы только латинские символы, пробелы и дефисы");
                        }),
                () ->
                        step("Шаг 3: Проверка отсутствия видимости успешной отправки формы", paymentPage::checkOkNotificationInvisibility)
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты без указания CVV/CVC")
    void test1_23_UnsuccessfulPaymentAttemptWithoutSpecifyingCVV() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, "");
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("CVC/CVV Неверный формат");
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением CVV/CVC (1 цифра)")
    void test1_24_UnsuccessfulPaymentAttemptWithInvalidValueInCVVField1Digit() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateInvalidCardCodeOne();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("CVC/CVV Неверный формат");
                        })
        );
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением CVV/CVC (буквы)")
    void test1_25_UnsuccessfulPaymentAttemptWithInvalidValueInCVVFieldLetters() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateInvalidCardCodeNotNumbers();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("CVC/CVV Неверный формат");
                        })
        );
    }

    @Test
    @DisplayName("Успешная покупка с номером карты со статусом APPROVED, месяц и год - текущие")
    void test1_26_SuccessfulPurchaseWithCardNumberAndApprovedStatusCurrentMonthAndYear() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateCurrentMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateCurrentMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);

        step("Шаг 1: Осуществление оплаты", () -> {
            paymentPage.payByCard(cardInfo);
            paymentPage.waitingForNotification();
        });

        var expectedStatus = "APPROVED";
        var actualStatus = SQLHelper.getInfoFromCreditPayment().getStatus();
        var transactionID = SQLHelper.getInfoFromCreditPayment().getBank_id();
        var paymentID = SQLHelper.getInfoFromOrder().getPayment_id();
        assertAll(
                () ->
                        step("Шаг 2: Проверка успешного уведомления", () -> {
                            paymentPage.checkNotificationText("Операция одобрена Банком.");
                        }),
                () ->
                        step("Шаг 3: Проверка отсутствия ошибки", paymentPage::checkErrorNotificationInvisibility),
                () ->
                        step("Шаг 4: Проверка статуса платежа в БД", () -> {
                            assertEquals(expectedStatus, actualStatus);
                        }),
                () ->
                        step("Шаг 5: Проверка отражения платежа в таблице заказов в БД", () -> {
                            assertEquals(transactionID, paymentID);
                        }));
    }

    @Test
    @DisplayName("Успешная покупка с номером карты со статусом APPROVED, Владелец - разрешенные 2 символа")
    void test1_27_SuccessfulPurchaseWithCardNumberAndAPPROVEDStatusCardholder2Symbols() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateCurrentMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateCurrentMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.getValidCardholderNameTwoSymbol();
        var cardCode = DataHelper.generateRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);

        step("Шаг 1: Осуществление оплаты", () -> {
            paymentPage.payByCard(cardInfo);
            paymentPage.waitingForNotification();
        });

        var expectedStatus = "APPROVED";
        var actualStatus = SQLHelper.getInfoFromCreditPayment().getStatus();
        var transactionID = SQLHelper.getInfoFromCreditPayment().getBank_id();
        var paymentID = SQLHelper.getInfoFromOrder().getPayment_id();
        assertAll(
                () ->
                        step("Шаг 2: Проверка успешного уведомления", () -> {
                            paymentPage.checkNotificationText("Операция одобрена Банком.");
                        }),
                () ->
                        step("Шаг 3: Проверка отсутствия ошибки", paymentPage::checkErrorNotificationInvisibility),
                () ->
                        step("Шаг 4: Проверка статуса платежа в БД", () -> {
                            assertEquals(expectedStatus, actualStatus);
                        }),
                () ->
                        step("Шаг 5: Проверка отражения платежа в таблице заказов в БД", () -> {
                            assertEquals(transactionID, paymentID);
                        }));
    }

    @Test
    @DisplayName("Неуспешная попытка оплаты с невалидным значением CVV/CVC (2 цифры)")
    void test1_28_UnsuccessfulPaymentAttemptWithInvalidValueInCVVorCVCField2Digits() {
        var paymentPage = new PaymentPage();
        var cardNumber = DataHelper.approvedCardNumber();
        var cardMonth = DataHelper.generateValidMonthAndYear().getCardMonth();
        var cardYear = DataHelper.generateValidMonthAndYear().getCardYear();
        var cardHolderName = DataHelper.generateRandomCardholderName();
        var cardCode = DataHelper.generateInvalidCardCodeTwo();
        var cardInfo = new DataHelper.CardInfo(cardNumber, cardMonth, cardYear, cardHolderName, cardCode);
        step("Шаг 1: Попытка оплаты", () -> {
            paymentPage.payByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Шаг 2: Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("CVC/CVV Неверный формат");
                        })
        );
    }
 */
}
