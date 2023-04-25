package com.codeoftheweb.salvo.core.util;

import com.codeoftheweb.salvo.model.dto.SalvoDto;
import com.codeoftheweb.salvo.model.entity.GamePlayer;
import com.codeoftheweb.salvo.model.entity.Salvo;
import com.codeoftheweb.salvo.model.entity.SalvoLocation;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SalvoValidation {

    public static void checkIfPlayerCanSubmitSalvo(GamePlayer gamePlayer, SalvoDto salvoDto) {
        if (gamePlayer.getGame().getGamePlayers().size() < 2) {
            throw new IllegalStateException("Wait for opponent player to submit a salvo.");
        }
        if (gamePlayer.getShips().size() < 5) {
            throw new IllegalStateException("First save all your ships, then submit your salvo.");
        }
        if(!isTurnNumberCorrect(gamePlayer, salvoDto)){
            throw new IllegalStateException("Wrong turn number.");
        }
    }

    public static void checkIfSalvoLocationsValid(SalvoDto salvoDto, GamePlayer gamePlayer) {
        if (salvoDto.getSalvoLocations().size() > 5) {
            throw new IllegalArgumentException("You can submit at most 5 shots in a salvo.");
        }
        if (hasSalvoDuplicatedLocation(salvoDto, gamePlayer)) {
            throw new IllegalArgumentException("Duplicated salvo locations.");
        }
        if (!CommonSyntaxValidation.hasCorrectLocationSyntax(salvoDto.getSalvoLocations())) {
            throw new IllegalArgumentException("Wrong Salvo Location Syntax.");
        }
    }

    public static boolean isTurnNumberCorrect(GamePlayer gamePlayer, SalvoDto salvoDto){
        Integer turnNumberRequested = salvoDto.getTurnNumber();
        System.out.println("turnNumberRequested" + turnNumberRequested);
        List<Integer> alreadyPlayedTurns = gamePlayer.getSalvoes()
                .stream()
                .map(Salvo::getTurnNumber)
                .toList();
        if(alreadyPlayedTurns.isEmpty()){
            return turnNumberRequested == 1;
        } else {
            return turnNumberRequested == Collections.max(alreadyPlayedTurns) + 1;
        }
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
