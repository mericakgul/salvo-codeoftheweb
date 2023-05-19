package com.codeoftheweb.salvo.core.util;

import com.codeoftheweb.salvo.model.dto.SalvoDto;
import com.codeoftheweb.salvo.model.entity.GamePlayer;
import com.codeoftheweb.salvo.model.entity.Salvo;
import com.codeoftheweb.salvo.model.entity.SalvoLocation;

import java.util.*;
import java.util.stream.Collectors;

public class SalvoValidation {

    public static void checkIfPlayerCanSubmitSalvo(GamePlayer ownerGamePlayer, SalvoDto salvoDto, GamePlayer opponentGamePlayer, String gameStatus) {
        if (ownerGamePlayer.getShips().size() < 5) {
            throw new IllegalStateException("First save all your ships, then submit your salvo.");
        }
        if (gameStatus.contains("GameOver")) {
            throw new IllegalStateException("Game is over, you cannot submit another salvo.");
        }
        if (opponentGamePlayer.getShips().size() < 5) {
            throw new IllegalStateException("Wait for your opponent to place their ships."); // This validation is done only on backend side since client cannot reach the ships of the opponent
        }
        if (isItOwnerPlayerTurn(ownerGamePlayer, opponentGamePlayer)) {
            throw new IllegalStateException("It is not your turn, wait for your opponent to play.");
        }
        if (!isTurnNumberCorrect(ownerGamePlayer, salvoDto)) {
            throw new IllegalStateException("Wrong turn number.");
        }
    }

    public static void checkIfSalvoLocationsValid(SalvoDto salvoDto, GamePlayer ownerGamePlayer, GamePlayer opponentGamePlayer, Map<Object, Object> gameHistory) {
        Long maxSelectableSalvoLocationNumber = getMaxSelectableSalvoLocationNumber(ownerGamePlayer, opponentGamePlayer, gameHistory);
        if (salvoDto.getSalvoLocations().size() > maxSelectableSalvoLocationNumber) {
            throw new IllegalArgumentException(String.format("You can only fire a maximum of %d locations", maxSelectableSalvoLocationNumber));
        }
        if (hasSalvoDuplicatedLocation(salvoDto, ownerGamePlayer)) {
            throw new IllegalArgumentException("Duplicated salvo locations.");
        }
        if (!CommonSyntaxValidation.hasCorrectLocationSyntax(salvoDto.getSalvoLocations())) {
            throw new IllegalArgumentException("Wrong Salvo Location Syntax.");
        }
    }

    public static boolean isItOwnerPlayerTurn(GamePlayer ownerGamePlayer, GamePlayer opponentGamePlayer) {
        return ownerGamePlayer.getId() < opponentGamePlayer.getId()  // This means the owner is the creator of the game. And the creator has the right to play first
                ? ownerGamePlayer.getSalvoes().size() > opponentGamePlayer.getSalvoes().size()
                : ownerGamePlayer.getSalvoes().size() >= opponentGamePlayer.getSalvoes().size();
    }

    public static boolean isTurnNumberCorrect(GamePlayer gamePlayer, SalvoDto salvoDto) {
        Integer turnNumberRequested = salvoDto.getTurnNumber();
        List<Integer> alreadyPlayedTurns = gamePlayer.getSalvoes()
                .stream()
                .map(Salvo::getTurnNumber)
                .toList();
        if (alreadyPlayedTurns.isEmpty()) {
            return turnNumberRequested == 1;
        } else {
            return turnNumberRequested == Collections.max(alreadyPlayedTurns) + 1;
        }
    }

    @SuppressWarnings("unchecked") // gameHistory.get(ownerPlayerId) for sure returns List<Map<Integer, Object>> type
    public static Long getMaxSelectableSalvoLocationNumber(GamePlayer ownerGamePlayer, GamePlayer opponentGamePlayer, Map<Object, Object> gameHistory) {
        Long ownerPlayerId = ownerGamePlayer.getPlayer().getId();
        boolean isOwnerCreator = ownerGamePlayer.getId() < opponentGamePlayer.getId();
        List<Map<Integer, Object>> salvoObjectsOnOwner = (List<Map<Integer, Object>>) gameHistory.getOrDefault(ownerPlayerId, new ArrayList<>());

        return salvoObjectsOnOwner.isEmpty()
                ? (long) ownerGamePlayer.getShips().size()
                : isOwnerCreator
                    ? (Long) ((Map<String, Object>) getLatestTurnSalvoObject(salvoObjectsOnOwner).values().iterator().next()).get("ship_number_left")
                    : getMaxSelectableSalvoLocationNumberForJoinerOwner(getLatestTurnSalvoObject(salvoObjectsOnOwner), salvoObjectsOnOwner, ownerGamePlayer);
    }

    public static Map<Integer, Object> getLatestTurnSalvoObject(List<Map<Integer, Object>> salvoTurnsOnOwner) {
        return salvoTurnsOnOwner.stream()
                .reduce(salvoTurnsOnOwner.get(0), (previousTurn, currentTurn) ->
                        currentTurn.keySet().iterator().next() > previousTurn.keySet().iterator().next()
                                ? currentTurn
                                : previousTurn
                );
    }

    @SuppressWarnings("unchecked")
    public static Long getMaxSelectableSalvoLocationNumberForJoinerOwner(Map<Integer, Object> lastSalvoObjectOnOwner, List<Map<Integer, Object>> salvoObjectsOnOwner, GamePlayer ownerGamePlayer) {
        Integer secondToLastTurnNumberOfJoinerOwner = lastSalvoObjectOnOwner.keySet().iterator().next() - 1; // This is done because the creatorOwner has the right to play first and if the creator player sinks some ships of the opponent in the first play of a turn, then the opponent's turn number (on opponent side it is joinerOwner) will be automatically decreased.

        return salvoObjectsOnOwner.stream()
                .filter(salvoObjectOnJoinerOwner -> salvoObjectOnJoinerOwner.containsKey(secondToLastTurnNumberOfJoinerOwner))
                .map(secondToLastSalvoObjectOnJoinerOwner -> (Map<String, Object>) secondToLastSalvoObjectOnJoinerOwner.get(secondToLastTurnNumberOfJoinerOwner))
                .findFirst()
                .map(secondToLastSalvoInfoOfJoinerOwner -> (Long) secondToLastSalvoInfoOfJoinerOwner.get("ship_number_left"))
                .orElse((long) ownerGamePlayer.getShips().size());
    }

    public static boolean hasSalvoDuplicatedLocation(SalvoDto salvoDto, GamePlayer gamePlayer) {
        Set<String> existingSalvoLocations = gamePlayer.getSalvoes().stream()
                .flatMap(salvo -> salvo.getSalvoLocations().stream())
                .map(SalvoLocation::getGridCell)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        List<String> requestedSalvoLocations = salvoDto.getSalvoLocations().stream()
                .map(String::toLowerCase)
                .toList();
        Set<String> combinedSalvoLocations = new HashSet<>(existingSalvoLocations);
        combinedSalvoLocations.addAll(requestedSalvoLocations);
        return combinedSalvoLocations.size() < salvoDto.getSalvoLocations().size() + existingSalvoLocations.size();
    }
}
