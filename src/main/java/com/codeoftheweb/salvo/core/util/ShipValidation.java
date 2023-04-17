package com.codeoftheweb.salvo.core.util;

import com.codeoftheweb.salvo.model.dto.ShipDto;
import com.codeoftheweb.salvo.model.dto.ShipDtoListWrapper;
import com.codeoftheweb.salvo.model.entity.GamePlayer;
import com.codeoftheweb.salvo.model.entity.ShipLocation;

import java.util.*;
import java.util.stream.Collectors;

public class ShipValidation {

    public static final Map<String, Integer> shipTypesAndSizes = ShipTypes.shipTypesAndSizes;

    public static void checkIfShipTypesAndLocationsValid(ShipDtoListWrapper shipDtoListWrapper, GamePlayer gamePlayer) {
        List<String> shipTypesOfShipDtoList = getShipTypesOfShipDtoList(shipDtoListWrapper);
        List<List<String>> shipLocationsListOfShipDtoList = getShipLocationsListOfShipDtoList(shipDtoListWrapper);
        boolean isNumberOfShipsValid = shipDtoListWrapper.getShips().size() >= 1 && shipDtoListWrapper.getShips().size() <= 5;
        boolean haveShipsCorrectLocationSyntax = CommonSyntaxValidation.hasCorrectLocationSyntax(combineListOfShipLocationList(shipLocationsListOfShipDtoList));

        if (!isNumberOfShipsValid) {
            throw new IllegalArgumentException("The number of ships has to be between 1 and 5.");
        } else if (!areAllShipsUnique(shipTypesOfShipDtoList)) {
            throw new IllegalArgumentException("Duplicated ships.");
        } else if (!haveShipTypesCorrectName(shipTypesOfShipDtoList)) {
            throw new IllegalArgumentException("Wrong ship names.");
        } else if (!haveShipsCorrectLocationSyntax) {
            throw new IllegalArgumentException("Wrong Ship Location Syntax.");
        } else if (!hasCorrectShipSize(shipDtoListWrapper)) {
            throw new IllegalArgumentException("Wrong ship size.");
        } else if (!areShipLocationsConsecutive(shipLocationsListOfShipDtoList)) {
            throw new IllegalArgumentException("Ships are not consecutive order.");
        } else if (doShipLocationsOverlap(shipLocationsListOfShipDtoList, gamePlayer)) {
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

    public static boolean doShipLocationsOverlap(List<List<String>> shipLocationsList, GamePlayer gamePlayer) {
        Set<String> existingShipLocations = gamePlayer.getShips().stream()
                .flatMap(ship -> ship.getShipLocations().stream())
                .map(ShipLocation::getGridCell)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        List<String> requestedShipLocations = combineListOfShipLocationList(shipLocationsList).stream()
                .map(String::toLowerCase)
                .toList();

        Set<String> combinedShipLocationsSet = new HashSet<>(existingShipLocations);
        combinedShipLocationsSet.addAll(requestedShipLocations);

        return combinedShipLocationsSet.size() < existingShipLocations.size() + requestedShipLocations.size();
    }

    public static List<String> combineListOfShipLocationList(List<List<String>> shipLocationsListOfShipList){
        return shipLocationsListOfShipList.stream()
                .flatMap(Collection::stream)
                .toList();
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
                .map(gridName -> Character.toLowerCase(gridName.charAt(0)))
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
