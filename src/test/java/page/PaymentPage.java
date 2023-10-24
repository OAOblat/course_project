package page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import data.DataHelper;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class PaymentPage {
    private final SelenideElement cardNumberField = $("form > fieldset > div:nth-child(1) .input input");
    private final SelenideElement cardMonthField = $("form > fieldset > div:nth-child(2) > .input-group > span:nth-child(1) .input input");
    private final SelenideElement cardYearField = $("form > fieldset > div:nth-child(2) > .input-group > span:nth-child(2) .input input");
    private final SelenideElement cardHolderNameField = $("form > fieldset > div:nth-child(3) > .input-group > span:nth-child(1) .input input");
    private final SelenideElement cardCodeField = $("form > fieldset > div:nth-child(3) > .input-group > span:nth-child(2) .input input");
    private final SelenideElement buttonSubmit = $($("form > fieldset > div:nth-child(4) button"));

    public PaymentPage payByCard(DataHelper.CardInfo info) {

        cardNumberField.setValue(info.getCardNumber());
        cardMonthField.setValue(info.getCardMonth());
        cardYearField.setValue(info.getCardYear());
        cardHolderNameField.setValue(info.getCardHolderName());
        cardCodeField.setValue(info.getCardCode());
        buttonSubmit.click();
        Selenide.sleep(10000);
        return new PaymentPage();
    }

    public void errorPayByCard(DataHelper.CardInfo info) {
        cardNumberField.setValue(info.getCardNumber());
        cardMonthField.setValue(info.getCardMonth());
        cardYearField.setValue(info.getCardYear());
        cardHolderNameField.setValue(info.getCardHolderName());
        cardCodeField.setValue(info.getCardCode());
        buttonSubmit.click();
    }
}
