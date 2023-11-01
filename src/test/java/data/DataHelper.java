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

    public static String approvedCardNumber() {
        return "1111 2222 3333 4444";
    }

    public static String declinedCardNumber() {
        return "5555 6666 7777 8888";
    }

    public static String getCardNumberNotNumber() {
        return "фффф фффф фффф фффф";
    }

    public static String generateRandomCardNumber() {
        return faker.numerify("################");
    }

    public static String generateInvalidCardNumber() {
        return faker.numerify("###############");
    }

    /*  ====== Месяц и год ====== */

    @Value
    public static class CardMonthAndYear {
        String cardMonth;
        String cardYear;
    }

    public static CardMonthAndYear generateCurrentMonthAndYear() { //месяц и год в данный момент времени
        var currentDate = LocalDate.now();
        var currentMonth = currentDate.getMonthValue();
        var formattedMonth = String.format("%02d", currentMonth);
        var currentYear = currentDate.getYear();
        var formattedYear = String.format("%04d", currentYear).substring(2);
        return new CardMonthAndYear(formattedMonth, formattedYear);
    }

    public static CardMonthAndYear generateValidMonthAndYear() { //Валидный месяц и год (срок действия в течение 5 лет)
        var currentDate = LocalDate.now();
        var randomMonth = String.format("%02d", faker.number().numberBetween(1, 12));
        var randomYear = Faker.instance().number().numberBetween(currentDate.getYear() + 1, currentDate.getYear() + 5);
        var formattedYear = String.format("%04d", randomYear).substring(2);
        return new CardMonthAndYear(randomMonth, formattedYear);
    }

    public static CardMonthAndYear generateExpiredCardMonthAndYear() { //Просроченная карта (срок закончился в этом году)
        var currentDate = LocalDate.now();
        var lastMonth = currentDate.getMonthValue() - 1;
        var formattedMonth = String.format("%02d", lastMonth);
        var currentYear = currentDate.getYear();
        var formattedYear = String.format("%04d", currentYear).substring(2);
        return new CardMonthAndYear(formattedMonth, formattedYear);
    }

    public static CardMonthAndYear generateInvalidCardMonthAndYearUntilValidYears() { //Валидный месяц и невалидный год (до текущего года)
        var currentDate = LocalDate.now();
        var randomMonth = String.format("%02d", faker.number().numberBetween(1, 12));
        var randomYear = Faker.instance().number().numberBetween(currentDate.getYear() - 23, currentDate.getYear() - 1);
        var formattedYear = String.format("%04d", randomYear).substring(2);
        return new CardMonthAndYear(randomMonth, formattedYear);
    }

    public static CardMonthAndYear generateInvalidCardMonthAndYearAfterValidYears() { //Валидный месяц и невалидный год (после 5 лет)
        var currentDate = LocalDate.now();
        var randomMonth = String.format("%02d", faker.number().numberBetween(1, 12));
        var randomYear = Faker.instance().number().numberBetween(currentDate.getYear() + 6, currentDate.getYear() + 99);
        var formattedYear = String.format("%04d", randomYear).substring(2);
        return new CardMonthAndYear(randomMonth, formattedYear);
    }

    public static String generateInvalidCardMonthFrom13To99() {
        return String.format("%02d", faker.number().numberBetween(13, 99));
    }

    public static String getInvalidCardMonth00() {
        return "00";
    }

    public static String generateInvalidCardMonthOneSymbol() {
        return faker.numerify("#");
    }

    public static CardMonthAndYear generateInvalidCardMonthAndYearNotNumber() {
        String[] invalidMonthOrYear = {"ab", "аб", "?!", "+)", ":%", "{="};
        var random = new Random();
        var monthOrYear = invalidMonthOrYear[random.nextInt(invalidMonthOrYear.length)];
        return new CardMonthAndYear(monthOrYear, monthOrYear);
    }

    /*  ====== Владелец ====== */

    public static String generateRandomCardholderName() {
        var firstName = faker.name().firstName().replaceAll("'", "");
        var middleName = faker.name().firstName().replaceAll("'", "");;
        var lastName = faker.name().lastName().replaceAll("'", "");;

        var cardholderName = firstName + "-" + middleName + " " + lastName;
        if (cardholderName.length() > 26) {
            var diff = cardholderName.length() - 26;
            String[] names = {firstName, middleName, lastName};
            for (var i = names.length - 1; i >= 0 && diff > 0; i--) {
                var name = names[i];
                if (name.length() <= diff) {
                    diff -= name.length() + 1; // Учитываем пробел или дефис
                    names[i] = "";
                }
            }
            cardholderName = String.join(" ", names).trim().replaceAll("\\s+", " ");
        }

        return cardholderName;
    }

    public static String getValidCardholderNameTwoSymbol() {
        return "ab";
    }

    public static String getInvalidCardholderNameOneSymbol() {
        return "a";
    }

    public static String getInvalidCardholderNameNumber() {
        return "12345";
    }

    public static String getInvalidCardholderNameSymbolMore26() {
        return "Anderson Whitmore-Smithsonian";
    }

    public static String getInvalidCardholderNameCyrillic() {
        return "Александр Иванов";
    }

    public static String getInvalidCardholderNameSpecSymbol() {
        return "??+!";
    }

    /*  ====== CVV ====== */

    public static String generateRandomCardCode() {
        return faker.numerify("###");
    }

    public static String generateInvalidCardCodeOne() {
        return faker.numerify("#");
    }

    public static String generateInvalidCardCodeTwo() {
        return faker.numerify("##");
    }

    public static String generateInvalidCardCodeNotNumbers() {
        String[] invalidCardCode = {"abc", "абв", "?!!", "+)(", ":%%", "{}="};
        var random = new Random();
        var cardCode = invalidCardCode[random.nextInt(invalidCardCode.length)];
        return cardCode;
    }
}
