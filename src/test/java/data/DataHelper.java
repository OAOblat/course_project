package data;

import com.github.javafaker.Faker;
import lombok.Value;
import java.util.Random;
import java.time.LocalDate;
import java.util.Locale;


public class DataHelper {
    private DataHelper() {
    }

    @Value
    public static class CardInfo {
        String cardNumber;
        String cardMonth;
        String cardYear;
        String cardHolderName;
        String cardCode;
    }

    private static Faker faker = new Faker(new Locale("en"));

    /* ====== Номер карты ====== */

    @Value
    public static class CardNumber {
        String cardNumber;
    }

    public static CardNumber approvedCardNumber() {
        return new CardNumber("1111 2222 3333 4444");
    }

    public static CardNumber declinedCardNumber() {
        return new CardNumber("5555 6666 7777 8888");
    }

    public static CardNumber getCardNumberNotNumber() {
        return new CardNumber("фффф фффф фффф фффф");
    }

    public static CardNumber generateRandomCardNumber() {
        return new CardNumber(faker.numerify("################"));
    }

    public static CardNumber generateInvalidCardNumber() {
        return new CardNumber(faker.numerify("###############"));
    }

    /*  ====== Месяц и год ====== */

    @Value
    public static class CardMonth {
        String cardMonth;
    }

    @Value
    public static class CardMonthAndYear {
        String cardMonth;
        String cardYear;
    }

    public static CardMonthAndYear generateCurrentMonthAndYear() { //месяц и год в данный момент времени
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        String formattedMonth = String.format("%02d", currentMonth);
        int currentYear = currentDate.getYear();
        String formattedYear = String.format("%04d", currentYear).substring(2);
        return new CardMonthAndYear(formattedMonth, formattedYear);
    }

    public static CardMonthAndYear generateValidMonthAndYear() { //Валидный месяц и год (срок действия в течение 5 лет)
        LocalDate currentDate = LocalDate.now();
        String randomMonth = String.format("%02d", faker.number().numberBetween(1, 12));
        int randomYear = Faker.instance().number().numberBetween(currentDate.getYear() + 1, currentDate.getYear() + 5);
        String formattedYear = String.format("%04d", randomYear).substring(2);
        return new CardMonthAndYear(randomMonth, formattedYear);
    }

    public static CardMonthAndYear generateExpiredCardMonthAndYear() { //Просроченная карта (срок закончился в этом году)
        LocalDate currentDate = LocalDate.now();
        int lastMonth = currentDate.getMonthValue() - 1;
        String formattedMonth = String.format("%02d", lastMonth);
        int currentYear = currentDate.getYear();
        String formattedYear = String.format("%04d", currentYear).substring(2);
        return new CardMonthAndYear(formattedMonth, formattedYear);
    }

    public static CardMonthAndYear generateInvalidCardMonthAndYearUntilValidYears() { //Валидный месяц и невалидный год (до текущего года)
        LocalDate currentDate = LocalDate.now();
        String randomMonth = String.format("%02d", faker.number().numberBetween(1, 12));
        int randomYear = Faker.instance().number().numberBetween(currentDate.getYear() - 23, currentDate.getYear() - 1);
        String formattedYear = String.format("%04d", randomYear).substring(2);
        return new CardMonthAndYear(randomMonth, formattedYear);
    }

    public static CardMonthAndYear generateInvalidCardMonthAndYearAfterValidYears() { //Валидный месяц и невалидный год (после 5 лет)
        LocalDate currentDate = LocalDate.now();
        String randomMonth = String.format("%02d", faker.number().numberBetween(1, 12));
        int randomYear = Faker.instance().number().numberBetween(currentDate.getYear() + 6, currentDate.getYear() + 99);
        String formattedYear = String.format("%04d", randomYear).substring(2);
        return new CardMonthAndYear(randomMonth, formattedYear);
    }

    public static CardMonth generateInvalidCardMonthFrom13To99() {
        return new CardMonth(String.format("%02d", faker.number().numberBetween(13, 99)));
    }

    public static CardMonth getInvalidCardMonth00() {
        return new CardMonth("00");
    }

    public static CardMonth generateInvalidCardMonthOneSymbol() {
        return new CardMonth(faker.numerify("#"));
    }

    public static CardMonthAndYear generateInvalidCardMonthAndYearNotNumber() {
        String[] invalidMonthOrYear = {"ab", "аб", "?!", "+)", ":%", "{="};
        Random random = new Random();
        String monthOrYear = invalidMonthOrYear[random.nextInt(invalidMonthOrYear.length)];
        return new CardMonthAndYear(monthOrYear, monthOrYear);
    }

    /*  ====== Владелец ====== */

    @Value
    public static class CardholderName {
        String cardholderName;
    }

    public static CardholderName generateRandomCardholderName() {
        String firstName = faker.name().firstName().replaceAll("'", "");
        String middleName = faker.name().firstName().replaceAll("'", "");;
        String lastName = faker.name().lastName().replaceAll("'", "");;

        String cardholderName = firstName + "-" + middleName + " " + lastName;
        if (cardholderName.length() > 26) {
            int diff = cardholderName.length() - 26;
            String[] names = {firstName, middleName, lastName};
            for (int i = names.length - 1; i >= 0 && diff > 0; i--) {
                String name = names[i];
                if (name.length() <= diff) {
                    diff -= name.length() + 1; // Учитываем пробел или дефис
                    names[i] = "";
                }
            }
            cardholderName = String.join(" ", names).trim().replaceAll("\\s+", " ");
        }

        return new CardholderName(cardholderName);
    }

    public static CardholderName getValidCardholderNameTwoSymbol() {
        return new CardholderName("ab");
    }

    public static CardholderName getInvalidCardholderNameOneSymbol() {
        return new CardholderName("a");
    }

    public static CardholderName getInvalidCardholderNameNumber() {
        return new CardholderName("12345");
    }

    public static CardholderName getInvalidCardholderNameSymbolMore26() {
        return new CardholderName("Anderson Whitmore-Smithsonian");
    }

    public static CardholderName getInvalidCardholderNameCyrillic() {
        return new CardholderName("Александр Иванов");
    }

    public static CardholderName getInvalidCardholderNameSpecSymbol() {
        return new CardholderName("??+!");
    }

    /*  ====== CVV ====== */

    @Value
    public static class CardCode {
        String cardCode;
    }

    public static CardCode generateRandomCardCode() {
        return new CardCode(faker.numerify("###"));
    }

    public static CardCode generateInvalidCardCodeOne() {
        return new CardCode(faker.numerify("#"));
    }

    public static CardCode generateInvalidCardCodeTwo() {
        return new CardCode(faker.numerify("##"));
    }

    public static CardCode generateInvalidCardCodeNotNumbers() {
        String[] invalidCardCode = {"abc", "абв", "?!!", "+)(", ":%%", "{}="};
        Random random = new Random();
        String cardCode = invalidCardCode[random.nextInt(invalidCardCode.length)];
        return new CardCode(cardCode);
    }
}
