package com.codeoftheweb.salvo.core.util;

import java.util.List;

public class CommonSyntaxValidation {

    public static boolean hasCorrectLocationSyntax(List<String> locationList) {
        return locationList.stream()
                .allMatch(location -> !location.contains(" ") &&
                        location.length() >= 2 &&
                        isRowLetterValid(location.charAt(0)) &&
                        isColNumberValid(location.substring(1)));
    }

    public static boolean isRowLetterValid(Character rowLetter) {
        return Character.isLetter(rowLetter) &&
                Character.toLowerCase(rowLetter) >= 'a' &&
                Character.toLowerCase(rowLetter) <= 'j';
    }

    public static boolean isColNumberValid(String colNumberAsString) {
        try {
            int colNumber = Integer.parseInt(colNumberAsString);
            return colNumber >= 1 && colNumber <= 10;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
