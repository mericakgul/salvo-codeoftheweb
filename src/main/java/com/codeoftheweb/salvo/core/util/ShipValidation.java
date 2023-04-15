package com.codeoftheweb.salvo.core.util;

import com.codeoftheweb.salvo.model.dto.ShipDto;
import com.codeoftheweb.salvo.model.dto.ShipDtoListWrapper;
import com.codeoftheweb.salvo.model.entity.GamePlayer;
import com.codeoftheweb.salvo.model.entity.ShipLocation;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ShipValidation {

    public static final Map<String, Integer> shipTypesAndSizes = ShipTypes.shipTypesAndSizes;

    public static void checkIfShipTypesAndLocationsValid(ShipDtoListWrapper shipDtoListWrapper, GamePlayer gamePlayer) {
        List<String> shipTypesOfShipDtoList = getShipTypesOfShipDtoList(shipDtoListWrapper);
        List<List<String>> shipLocationsListOfShipDtoList = getShipLocationsListOfShipDtoList(shipDtoListWrapper);
        boolean isNumberOfShipsValid = shipDtoListWrapper.getShips().size() >= 1 && shipDtoListWrapper.getShips().size() <= 5;
        boolean areAllShipsUnique = areAllShipsUnique(shipTypesOfShipDtoList);
        boolean doShipTypesHaveCorrectName = haveShipTypesCorrectName(shipTypesOfShipDtoList);
        boolean doShipLocationsHaveCorrectSyntax = hasCorrectShipLocationSyntax(shipLocationsListOfShipDtoList);
        boolean doShipsHaveCorrectSize = hasCorrectShipSize(shipDtoListWrapper);
        boolean areShipLocationsInConsecutiveOrder = areShipLocationsConsecutive(shipLocationsListOfShipDtoList);
        boolean areShipLocationsOverlapping = doShipLocationsOverlap(shipLocationsListOfShipDtoList, gamePlayer);

        if (!isNumberOfShipsValid) {
            throw new IllegalArgumentException("The number of ships has to be between 1 and 5.");
        } else if (!areAllShipsUnique) {
            throw new IllegalArgumentException("Duplicated ships.");
        } else if (!doShipTypesHaveCorrectName) {
            throw new IllegalArgumentException("Wrong ship names.");
        } else if (!doShipLocationsHaveCorrectSyntax) {
            throw new IllegalArgumentException("Wrong Ship Location Syntax.");
        } else if (!doShipsHaveCorrectSize) {
            throw new IllegalArgumentException("Wrong ship size.");
        } else if (!areShipLocationsInConsecutiveOrder) {
            throw new IllegalArgumentException("Ships are not consecutive order.");
        } else if (areShipLocationsOverlapping) {
            throw new IllegalArgumentException("Ships are overlapping");
        }
    }

    public static boolean areAllShipsUnique(List<String> shipTypesList) {
        Set<String> shipTypesSet = new HashSet<>(shipTypesList);
        return shipTypesList.size() == shipTypesSet.size();
    }

    public static boolean haveShipTypesCorrectName(List<String> shipTypesList) {
        return shipTypesList.stream()
                .allMatch(shipTypesAndSizes::containsKey);
    }

    public static boolean hasCorrectShipLocationSyntax(List<List<String>> shipLocationsList) {
        return shipLocationsList.stream()
                .allMatch(shipLocations -> shipLocations.stream()
                        .allMatch(location -> !location.contains(" ") &&
                                location.length() >= 2 &&
                                isRowLetterValid(location.charAt(0)) &&
                                isColNumberValid(location.substring(1))));
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

    public static boolean hasCorrectShipSize(ShipDtoListWrapper shipDtoListWrapper) {
        return shipDtoListWrapper.getShips().stream()
                .allMatch(shipDto -> {
                    String shipType = shipDto.getShipType().toLowerCase();
                    Integer shipLocationsSize = shipDto.getShipLocations().size();
                    return Objects.equals(shipTypesAndSizes.get(shipType), shipLocationsSize);
                });
    }

    public static boolean areShipLocationsConsecutive(List<List<String>> shipLocationsList) {
        return shipLocationsList.stream()
                .allMatch(shipLocations -> {
                    List<Character> rowLetters = getShipLocationsRowLetters(shipLocations);
                    List<Integer> colNumbers = getShipLocationsColNumbers(shipLocations);
                    return (areColNumbersConsecutive(colNumbers) && areAllRawLettersSame(rowLetters)) ||
                            (areRowLettersConsecutive(rowLetters) && areAllColNumbersSame(colNumbers));
                });
    }

    public static boolean areColNumbersConsecutive(List<Integer> colNumbers) {
        for (int i = 1; i < colNumbers.size(); i++) {
            if (colNumbers.get(i) != colNumbers.get(i - 1) + 1) {
                return false;
            }
        }
        return true;
    }

    public static boolean areRowLettersConsecutive(List<Character> rowLetters) {
        for (int i = 1; i < rowLetters.size(); i++) {
            if (rowLetters.get(i) != rowLetters.get(i - 1) + 1) {
                return false;
            }
        }
        return true;
    }

    public static boolean areAllColNumbersSame(List<Integer> colNumbers) {
        return colNumbers.stream().distinct().count() == 1;
    }

    public static boolean areAllRawLettersSame(List<Character> rowLetters) {
        return rowLetters.stream().distinct().count() <= 1;
    }

    public static boolean doShipLocationsOverlap(List<List<String>> shipLocationsList, GamePlayer gamePlayer){
        Set<String> existingShipLocations = gamePlayer.getShips().stream()
                .flatMap(ship -> ship.getShipLocations().stream())
                .map(ShipLocation::getGridCell)
                .collect(Collectors.toSet());
        Set<String> combinedShipLocationsSet = new HashSet<>(existingShipLocations);
        AtomicInteger totalSizeOfShipLocations = new AtomicInteger(0);
        shipLocationsList.forEach(shipLocations -> {
            combinedShipLocationsSet.addAll(shipLocations);
            totalSizeOfShipLocations .addAndGet(shipLocations.size());
        });
        return combinedShipLocationsSet.size() < existingShipLocations.size() + totalSizeOfShipLocations.get();
    }


    public static List<String> getShipTypesOfShipDtoList(ShipDtoListWrapper shipDtoListWrapper) {
        return shipDtoListWrapper.getShips().stream()
                .map(ShipDto::getShipType)
                .map(String::toLowerCase)
                .toList();
    }

    public static List<List<String>> getShipLocationsListOfShipDtoList(ShipDtoListWrapper shipDtoListWrapper) {
        return shipDtoListWrapper.getShips().stream()
                .map(ShipDto::getShipLocations)
                .toList();
    }

    public static List<Character> getShipLocationsRowLetters(List<String> shipLocations) {
        List<Character> rowLetters = new ArrayList<>(shipLocations.stream()
                .map(gridName -> gridName.charAt(0))
                .toList());
        Collections.sort(rowLetters);
        return rowLetters;
    }

    public static List<Integer> getShipLocationsColNumbers(List<String> shipLocations) {
        List<Integer> colNumbers = new ArrayList<>(shipLocations.stream()
                .map(gridName -> Integer.parseInt(gridName.substring(1)))
                .toList());
        Collections.sort(colNumbers);
        return colNumbers;
    }

}
