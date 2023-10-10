package ru.sharanov.SearchForMessagesBot.Handler;

public class ConvertMonth {

    public static int convertMonthWordInDigital(String month) {
        int monthOfYear = -1;
        switch (month) {
            case "января", "январь" -> monthOfYear = 1;
            case "февраля", "февраль" -> monthOfYear = 2;
            case "марта", "март" -> monthOfYear = 3;
            case "апреля", "апрель" -> monthOfYear = 4;
            case "мая", "май" -> monthOfYear = 5;
            case "июня", "июнь" -> monthOfYear = 6;
            case "июля", "июль" -> monthOfYear = 7;
            case "августа", "август" -> monthOfYear = 8;
            case "сентября", "сентябрь" -> monthOfYear = 9;
            case "октября", "октябрь" -> monthOfYear = 10;
            case "ноября", "ноябрь" -> monthOfYear = 11;
            case "декабря", "декабрь" -> monthOfYear = 12;
        }
        return monthOfYear;
    }
}
