package page;

import com.codeborne.selenide.SelenideElement;
import data.DataHelper;
import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;



public class PaymentPage {
    private final SelenideElement cardNumberField = $("form > fieldset > div:nth-child(1) .input input");
    private final SelenideElement cardMonthField = $("form > fieldset > div:nth-child(2) > .input-group > span:nth-child(1) .input input");
    private final SelenideElement cardYearField = $("form > fieldset > div:nth-child(2) > .input-group > span:nth-child(2) .input input");
    private final SelenideElement cardHolderNameField = $("form > fieldset > div:nth-child(3) > .input-group > span:nth-child(1) .input input");
    private final SelenideElement cardCodeField = $("form > fieldset > div:nth-child(3) > .input-group > span:nth-child(2) .input input");
    private final SelenideElement buttonSubmit = $($("form > fieldset > div:nth-child(4) button"));
//  private final SelenideElement buttonSubmit = $(byXpath("//span[text()='Продолжить']"));
    private final SelenideElement notificationStatusOk = $(".notification_status_ok");
    private final SelenideElement notification = $(".notification_visible .notification__content");
    private final SelenideElement notificationForChecking = $(".notification");
    private final SelenideElement notificationStatusError = $(".notification_status_error");
    private final SelenideElement buttonPayByDebitCard = $(".button.button_size_m.button_theme_alfa-on-white > .button__content > span.button__text");
    //  private final SelenideElement buttonPayByDebitCard = $(byXpath("//span[text()='Купить']"));
    private final SelenideElement headingDebitPay = $(byText("Оплата по карте"));
    //  private final SelenideElement headingDebitPay = $(byXpath("//h3[text()='Оплата по карте']"));
    private final SelenideElement buttonPayByCreditCard = $(".button.button_view_extra.button_size_m.button_theme_alfa-on-white > span.button__content > span.button__text");
    //  private final SelenideElement buttonPayByCreditCard = $(byXpath("//span[text()='Купить в кредит']"));
    private final SelenideElement headingCreditPay = $(byText("Кредит по данным карты"));
    //  private final SelenideElement headingCreditPay = $(byXpath("//h3[text()='Кредит по данным карты']"));
    private final SelenideElement inputInvalid = $(".input_invalid");

    public void payByCard(DataHelper.CardInfo info) {

        cardNumberField.setValue(info.getCardNumber());
        cardMonthField.setValue(info.getCardMonth());
        cardYearField.setValue(info.getCardYear());
        cardHolderNameField.setValue(info.getCardHolderName());
        cardCodeField.setValue(info.getCardCode());
        buttonSubmit.click();
    }

    public void openDebitPayPage() {
        buttonPayByDebitCard.click();
        headingDebitPay.shouldBe(visible);
    }

    public void openCreditPayPage() {
        buttonPayByCreditCard.click();
        headingCreditPay.shouldBe(visible);
    }

    public void checkNotificationText (String expectedText) {
        notification.shouldHave(exactText(expectedText)).shouldBe(visible, Duration.ofSeconds(15));
    }

    public void waitingForNotification () {
        notificationForChecking.shouldBe(visible, Duration.ofSeconds(15));
    }

    public void checkErrorNotificationInvisibility () {
        notificationStatusError.shouldNotBe(visible).should(disappear, Duration.ofSeconds(15));
    }

    public void checkErrorNotificationVisibility () {
        notificationStatusError.shouldBe(visible, Duration.ofSeconds(15));
    }

    public void checkOkNotificationInvisibility() {
        notificationStatusOk.shouldNotBe(visible).should(disappear, Duration.ofSeconds(15));
    }

    public void checkInputInvalid (String expectedText) {
        inputInvalid.shouldHave(exactText(expectedText)).shouldBe(visible, Duration.ofSeconds(5));
    }
}
