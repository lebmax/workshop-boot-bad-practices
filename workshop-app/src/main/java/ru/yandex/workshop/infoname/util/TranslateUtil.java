package ru.yandex.workshop.infoname.util;

public class TranslateUtil {

    private TranslateUtil() {
    }

    public static String translateGender(String gender) {
        if (gender.equals("male")) {
            return "мужской";
        } else if (gender.equals("female")) {
            return "женский";
        } else throw new RuntimeException("Invalid gender");
    }

    public static String translateToProbabilityEnding(Double object) {
        return " (вероятность : " + object * 100 + " процентов)";
    }
}
