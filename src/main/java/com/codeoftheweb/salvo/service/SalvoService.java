package com.codeoftheweb.salvo.service;

import com.codeoftheweb.salvo.core.helper.ObjectExistence;
import com.codeoftheweb.salvo.core.util.SalvoValidation;
import com.codeoftheweb.salvo.core.util.ShipValidation;
import com.codeoftheweb.salvo.model.dto.*;
import com.codeoftheweb.salvo.model.entity.*;
import com.codeoftheweb.salvo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalvoService {

    private final GameRepository gameRepository;
    private final GamePlayerRepository gamePlayerRepository;
    private final PlayerRepository playerRepository;
    private final ShipRepository shipRepository;
    private final ShipLocationRepository shipLocationRepository;
    private final SalvoRepository salvoRepository;
    private final SalvoLocationRepository salvoLocationRepository;
    private final ObjectExistence objectExistence;
    private final PasswordEncoder passwordEncoder;

    public Map<String, Object> getGamesPageData(Authentication authentication) {
        Map<String, Object> mapOfGamesPage = new HashMap<>();
        Map<String, Object> mappedPlayer = new HashMap<>();
        if (authentication != null) {
            Player authenticatedPlayer = this.getAuthenticatedUser(authentication);
            mappedPlayer = this.makePlayerMap(authenticatedPlayer);
        }
        mapOfGamesPage.put("player", mappedPlayer);
        mapOfGamesPage.put("games", this.getGames());
        return mapOfGamesPage;
    }

    public Map<String, Object> getGameView(Long gamePlayerId, Authentication authentication) {
        GamePlayer gamePlayer = this.objectExistence.checkIfGamePlayerExistAndReturn(gamePlayerId);
        boolean isPlayerAuthorized = authentication != null && this.isPlayerAuthenticatedForTheGame(gamePlayerId, authentication);
        if (isPlayerAuthorized) {
            Game game = gamePlayer.getGame();
            Map<String, Object> mapOfGameView = this.makeGameMap(game);
            mapOfGameView.put("gamePlayerId", gamePlayerId);
            mapOfGameView.put("ships", this.createShipListOfPlayer(gamePlayer.getShips()));
            mapOfGameView.put("salvoes", this.createSalvoesOfGameForEachPlayer(gamePlayer));
            if (game.getGamePlayers().size() == 2) {
                mapOfGameView.put("gameHistory", this.createGameHistory(gamePlayer));
            }
            return new TreeMap<>(mapOfGameView);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to see this games details.");
        }
    }

    public PlayerResponse signUp(PlayerRequest playerRequest) {
        try {
            playerRequest.setPassword(this.passwordEncoder.encode(playerRequest.getPassword()));
            Player player = new Player(playerRequest.getUsername(), playerRequest.getPassword());
            Player savedPlayer = this.playerRepository.save(player);
            return new PlayerResponse(savedPlayer.getUsername());
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "error: Name in use");
        }
    }

    public Map<String, Long> createGame(Authentication authentication) {
        if (authentication != null) {
            Player authenticatedPlayer = this.getAuthenticatedUser(authentication);
            Game createdGame = new Game(new Date());
            Game savedGame = this.gameRepository.save(createdGame);
            return this.createGamePlayer(savedGame, authenticatedPlayer, createdGame.getCreationDate());
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You need to log in to create a game.");
        }
    }

    public Map<String, Long> joinGame(Long gameId, Authentication authentication) {
        if (authentication != null) {
            Player authenticatedPlayer = this.getAuthenticatedUser(authentication);
            Game gameRequestedToJoin = this.gameRepository.findById(gameId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "No such game with this id."));
            int playerNumberInTheGame = gameRequestedToJoin.getGamePlayers().size();
            if (playerNumberInTheGame > 1) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Game is full!");
            } else if (isPlayerAlreadyInTheGame(gameRequestedToJoin, authenticatedPlayer.getUsername())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are already in this game");
            } else {
                return this.createGamePlayer(gameRequestedToJoin, authenticatedPlayer, new Date());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You need to log in to join a game.");
        }
    }

    public ShipDtoListWrapper getShips(Long gamePlayerId, Authentication authentication) {
        GamePlayer gamePlayer = this.objectExistence.checkIfGamePlayerExistAndReturn(gamePlayerId);
        boolean isPlayerAuthorizedToGetShips = authentication != null && this.isPlayerAuthenticatedForTheGame(gamePlayerId, authentication);
        if (!isPlayerAuthorizedToGetShips) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to get ships.");
        } else {
            List<ShipDto> ships = this.createShipListOfPlayer(gamePlayer.getShips());
            return new ShipDtoListWrapper(ships);
        }
    }

    public void saveShips(Long gamePlayerId, ShipDtoListWrapper shipDtoListWrapper, Authentication authentication) {
        GamePlayer gamePlayer = this.objectExistence.checkIfGamePlayerExistAndReturn(gamePlayerId);
        boolean isPlayerAuthorizedToPlaceShips = authentication != null && this.isPlayerAuthenticatedForTheGame(gamePlayerId, authentication);
        boolean isTryingToPlaceExistingShip = isThereAnyShipAlreadyPlaced(shipDtoListWrapper, gamePlayer);
        if (!isPlayerAuthorizedToPlaceShips) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to place ships.");
        }
        if (isTryingToPlaceExistingShip) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "One or more of the ships that you try to place was already placed.");
        }
        try {
            ShipValidation.checkIfShipTypesAndLocationsValid(shipDtoListWrapper, gamePlayer);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        shipDtoListWrapper.getShips().forEach(shipDto -> {
            Ship savedShip = this.saveAndReturnShip(shipDto, gamePlayer);
            shipDto.getShipLocations()
                    .forEach(gridCell -> this.saveShipLocation(savedShip, gridCell));
        });
    }

    public List<SalvoDto> getOwnerSalvoes(Long gamePlayerId, Authentication authentication) {
        GamePlayer gamePlayer = this.objectExistence.checkIfGamePlayerExistAndReturn(gamePlayerId);
        boolean isPlayerAuthorizedToGetSalvoes = authentication != null && this.isPlayerAuthenticatedForTheGame(gamePlayerId, authentication);
        if (!isPlayerAuthorizedToGetSalvoes) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to get salvoes.");
        } else {
            return this.createSalvoTurnsAndLocations(gamePlayer.getSalvoes());
        }
    }

    public void saveSalvo(Long gamePlayerId, SalvoDto salvoDto, Authentication authentication) {
        GamePlayer ownerGamePlayer = this.objectExistence.checkIfGamePlayerExistAndReturn(gamePlayerId);
        boolean isPlayerAuthorizedToPlaceSalvo = authentication != null && this.isPlayerAuthenticatedForTheGame(gamePlayerId, authentication);
        if (!isPlayerAuthorizedToPlaceSalvo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to place ships.");
        }
        if(ownerGamePlayer.getGame().getGamePlayers().size() == 2){
            Map<Object, Object> gameHistory = this.createGameHistory(ownerGamePlayer);
            String gameStatus = (String) gameHistory.get("gameStatus");
            try {
                SalvoValidation.checkIfPlayerCanSubmitSalvo(ownerGamePlayer, salvoDto, this.getOpponentGamePlayer(ownerGamePlayer), gameStatus);
            } catch (IllegalStateException e) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
            }
            try {
                SalvoValidation.checkIfSalvoLocationsValid(salvoDto, ownerGamePlayer);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wait for opponent player to be able to submit a salvo.");
        }
        Salvo savedSalvo = this.saveAndReturnSalvo(ownerGamePlayer, salvoDto);
        salvoDto.getSalvoLocations()
                .forEach(gridCell -> this.saveSalvoLocation(savedSalvo, gridCell));
    }


    private Salvo saveAndReturnSalvo(GamePlayer gamePlayer, SalvoDto salvoDto) {
        Salvo salvo = new Salvo(gamePlayer, salvoDto.getTurnNumber());
        return this.salvoRepository.save(salvo);
    }

    private void saveSalvoLocation(Salvo salvo, String gridCell) {
        SalvoLocation salvoLocation = new SalvoLocation(salvo, gridCell);
        this.salvoLocationRepository.save(salvoLocation);
    }

    private Ship saveAndReturnShip(ShipDto shipDto, GamePlayer gamePlayer) {
        Ship ship = new Ship(shipDto.getShipType(), gamePlayer);
        return this.shipRepository.save(ship);
    }

    private void saveShipLocation(Ship ship, String gridCell) {
        ShipLocation shipLocation = new ShipLocation(ship, gridCell);
        this.shipLocationRepository.save(shipLocation);
    }

    private Map<String, Long> createGamePlayer(Game game, Player player, Date joinDate) {
        GamePlayer createdGamePlayer = new GamePlayer(game, player, joinDate);
        GamePlayer savedGamePlayer = this.gamePlayerRepository.save(createdGamePlayer);
        Long gpid = savedGamePlayer.getId();
        return Collections.singletonMap("gpid", gpid);
    }

    private Player getAuthenticatedUser(Authentication authentication) {
        return this.playerRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with this username"));
    }

    private Boolean isPlayerAuthenticatedForTheGame(Long gamePlayerId, Authentication authentication) {
        Player authenticatedPlayer = this.getAuthenticatedUser(authentication);
        Set<GamePlayer> gamePlayersOfAuthPlayer = authenticatedPlayer.getGamePlayers();
        Set<Long> gamePlayerIdsOfAuthPlayer = gamePlayersOfAuthPlayer.stream()
                .map(GamePlayer::getId)
                .collect(Collectors.toSet());
        return gamePlayerIdsOfAuthPlayer.contains(gamePlayerId);
    }

    private Boolean isThereAnyShipAlreadyPlaced(ShipDtoListWrapper shipDtoListWrapper, GamePlayer gamePlayer) {
        List<String> shipTypesOfShipDtoList = ShipValidation.getShipTypesOfShipDtoList(shipDtoListWrapper);
        List<String> existingShipsOfGamePlayer = gamePlayer.getShips().stream()
                .map(Ship::getShipType)
                .map(String::toLowerCase)
                .toList();
        return shipTypesOfShipDtoList.stream()
                .anyMatch(existingShipsOfGamePlayer::contains);
    }

    private Boolean isPlayerAlreadyInTheGame(Game game, String loggedInPlayerUsername) {
        Set<GamePlayer> gamePlayersOfTheGame = game.getGamePlayers();
        GamePlayer[] gamePlayersOfTheGame_Array = gamePlayersOfTheGame.toArray(new GamePlayer[0]);
        String usernameOfPlayerAlreadyInGame = gamePlayersOfTheGame_Array[0].getPlayer().getUsername();
        return Objects.equals(usernameOfPlayerAlreadyInGame, loggedInPlayerUsername);
    }

    private List<Object> getGames() {
        List<Game> games = this.gameRepository.findAll();
        return games.stream()
                .map(this::makeGameMap)
                .collect(Collectors.toList());
    }

    private Map<String, Object> makeGameMap(Game game) {
        Map<String, Object> gameMap = new HashMap<>();
        gameMap.put("gameId", game.getId());
        gameMap.put("created", game.getCreationDate());
        gameMap.put("gamePlayers", this.makeMapOfGamePlayers(game.getGamePlayers()));
        return gameMap;
    }

    private List<Map<String, Object>> makeMapOfGamePlayers(Set<GamePlayer> gamePlayers) {
        return gamePlayers.stream()
                .map(gamePlayer -> {
                    Map<String, Object> gamePlayerMap = new HashMap<>();
                    gamePlayerMap.put("id", gamePlayer.getId());
                    gamePlayerMap.put("player", makePlayerMap(gamePlayer.getPlayer()));
                    gamePlayerMap.put("score", this.getGamePlayerScore(gamePlayer));
                    return gamePlayerMap;
                })
                .sorted(Comparator.comparingLong((Map<String, Object> gamePlayerMap) -> (Long) gamePlayerMap.get("id"))) //This sorted() method has been added to be able to send the gamePlayers sorted depending on their id's because the gamePlayer that has the smaller id means that the player in that gamePlayer is the creator of the game. We want to show the creator before the second player on frontend.
                // .sorted(Comparator.comparingLong((Map<String, Object> gamePlayerMap) -> (Long) gamePlayerMap.get("id")).reversed()) // This line is added for practice reason. It sorts in descending.
                .collect(Collectors.toList());
    }

    private Map<String, Object> makePlayerMap(Player player) {
        Map<String, Object> playerMap = new HashMap<>();
        playerMap.put("id", player.getId());
        playerMap.put("username", player.getUsername());
        return playerMap;
    }

    private Double getGamePlayerScore(GamePlayer gamePlayer) {
        List<Score> scoresOfGame = gamePlayer.getGame().getScores();
        Long playerId = gamePlayer.getPlayer().getId();
        Optional<Score> scoreOfGamePlayer = scoresOfGame.stream()
                .filter(score -> Objects.equals(score.getPlayer().getId(), playerId))
                .findFirst();
        return scoreOfGamePlayer.map(Score::getScoreNumber).orElse(null);
    }

    private List<ShipDto> createShipListOfPlayer(Set<Ship> ships) {
        return ships.stream()
                .map(ship -> {
                    String shipType = ship.getShipType();
                    List<String> shipLocations = ship.getShipLocations().stream()
                            .map(ShipLocation::getGridCell)
                            .toList();
                    return new ShipDto(shipType, shipLocations);
                })
                .collect(Collectors.toList());
    }

    private Map<Long, Object> createSalvoesOfGameForEachPlayer(GamePlayer gamePlayerRequested) {
        Map<Long, Object> mapOfSalvoes = new HashMap<>();
        Set<GamePlayer> gamePlayers = gamePlayerRequested.getGame().getGamePlayers();
        gamePlayers.forEach(gamePlayer ->
                mapOfSalvoes.put(gamePlayer.getPlayer().getId(), this.createSalvoTurnsAndLocations(gamePlayer.getSalvoes())));
        return mapOfSalvoes;
    }

    private List<SalvoDto> createSalvoTurnsAndLocations(Set<Salvo> salvoes) {
        return salvoes.stream()
                .map(salvo -> {
                    Integer turnNumber = salvo.getTurnNumber();
                    List<String> salvoLocations = salvo.getSalvoLocations()
                            .stream()
                            .map(SalvoLocation::getGridCell)
                            .toList();
                    return new SalvoDto(turnNumber, salvoLocations);
                }).toList();
    }

    private Map<Object, Object> createGameHistory(GamePlayer ownerGamePlayer) {
        Long ownerPlayerId = ownerGamePlayer.getPlayer().getId();
        GamePlayer opponentGamePlayer = this.getOpponentGamePlayer(ownerGamePlayer);
        Long opponentPlayerId = opponentGamePlayer.getPlayer().getId();
        Map<Object, Object> gameHistory = new HashMap<>();
        List<String> ownerSunkShipsList = new ArrayList<>();
        List<String> opponentSunkShipsList = new ArrayList<>();
        gameHistory.put(ownerPlayerId, this.createHitsOnPlayer(ownerGamePlayer, opponentGamePlayer, ownerSunkShipsList));
        gameHistory.put(opponentPlayerId, this.createHitsOnPlayer(opponentGamePlayer, ownerGamePlayer, opponentSunkShipsList));
        gameHistory.put("gameStatus", getGameStatus(ownerGamePlayer, opponentGamePlayer, ownerSunkShipsList, opponentSunkShipsList));
        return gameHistory;
    }

    private GamePlayer getOpponentGamePlayer(GamePlayer ownerGamePlayer) {
        Set<GamePlayer> gamePlayersOfGame = ownerGamePlayer.getGame().getGamePlayers();
        Optional<GamePlayer> opponentGamePlayer = gamePlayersOfGame.stream()
                .filter(gamePlayerOfGame -> !Objects.equals(gamePlayerOfGame.getPlayer().getId(), ownerGamePlayer.getPlayer().getId()))
                .findFirst();
        return opponentGamePlayer.orElse(null);
    }

    private String getGameStatus(GamePlayer ownerGamePlayer, GamePlayer opponentGamePlayer, List<String> ownerSunkShipsList, List<String> opponentSunkShipsList){
        boolean isTurnNumbersEqual = ownerGamePlayer.getSalvoes().size() == opponentGamePlayer.getSalvoes().size();
        if(ownerGamePlayer.getShips().size() < 5){
            return "Place Ships";
        } else if (opponentGamePlayer.getShips().size() < 5) {
            return "Wait for opponent to place ships.";
        } else if (ownerSunkShipsList.size() == 5 && isTurnNumbersEqual) {
            return "GameOver: You lost!";
        } else if (opponentSunkShipsList.size() == 5 && isTurnNumbersEqual) {
            return "GameOver: You win!";
        } else if (!SalvoValidation.isTurnOfOwnerPlayer(ownerGamePlayer, opponentGamePlayer)) {
            return "Wait for opponent's turn";
        } else return "Enter Salvo";
    }

    private List<Map<Integer, Object>> createHitsOnPlayer(GamePlayer gamePlayerGotHits, GamePlayer gamePlayerMadeSalvo, List<String> sunkShipsList) {
        List<Map<Integer, Object>> historyOfHitsOnPlayer = new ArrayList<>();
        Set<Salvo> salvoesMade = gamePlayerMadeSalvo.getSalvoes();
        Set<Salvo> sortedSalvoesMade = salvoesMade.stream()
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingInt(Salvo::getTurnNumber))));
        Map<String, Integer> remainingLocationsSizeOfShips = this.getRemainingLocationsSizeOfShips(gamePlayerGotHits.getShips());
        sortedSalvoesMade.forEach(salvoMade -> {
            Map<Integer, Object> mapOfEachTurn = new HashMap<>();
            mapOfEachTurn.put(salvoMade.getTurnNumber(), this.createHitInfoOfPlayerGotHitForEachTurn(gamePlayerGotHits.getShips(), salvoMade, remainingLocationsSizeOfShips, sunkShipsList));
            historyOfHitsOnPlayer.add(mapOfEachTurn);
        });
        return historyOfHitsOnPlayer;
    }

    private Map<String, Integer> getRemainingLocationsSizeOfShips(Set<Ship> playerShips) {
        Map<String, Integer> remainingLocationsSizeOfShips = new HashMap<>();
        playerShips.forEach(firstShipOfPlayer ->
                remainingLocationsSizeOfShips.put(firstShipOfPlayer.getShipType(), firstShipOfPlayer.getShipLocations().size()));
        return remainingLocationsSizeOfShips;
    }

    private Object createHitInfoOfPlayerGotHitForEachTurn(Set<Ship> shipsOfPlayerGotHits, Salvo salvoOnPlayerGotHits, Map<String, Integer> remainingLocationsSizeOfShips, List<String> sunkShipList) {
        Map<String, Object> hitInfoOfPlayerGotHit = new HashMap<>();
        hitInfoOfPlayerGotHit.put("ships_hit", this.createHitShipsAndLocations(shipsOfPlayerGotHits, salvoOnPlayerGotHits, remainingLocationsSizeOfShips));
        hitInfoOfPlayerGotHit.put("ship_number_left", this.getNumberOfRemainingShips(remainingLocationsSizeOfShips));
        hitInfoOfPlayerGotHit.put("ships_sunk", this.getShipsSunk(remainingLocationsSizeOfShips, sunkShipList));
        return hitInfoOfPlayerGotHit;
    }

    private Object createHitShipsAndLocations(Set<Ship> shipsOfPlayerGotHits, Salvo salvoOnPlayerGotHits, Map<String, Integer> remainingLocationsSizeOfShips) {
        Map<String, List<String>> hitShipsAndLocations = new HashMap<>();
        List<String> salvoLocations = salvoOnPlayerGotHits.getSalvoLocations()
                .stream()
                .map(SalvoLocation::getGridCell)
                .map(String::toLowerCase)
                .toList();

        shipsOfPlayerGotHits.forEach(ship -> {
            List<String> shipLocations = ship.getShipLocations().stream()
                    .map(ShipLocation::getGridCell)
                    .map(String::toLowerCase)
                    .toList();
            List<String> hitLocations = new ArrayList<>(salvoLocations);
            hitLocations.retainAll(shipLocations);
            if (hitLocations.size() > 0) {
                hitShipsAndLocations.put(ship.getShipType(), hitLocations);
                Integer currentLocationsSizeOfShip = remainingLocationsSizeOfShips.get(ship.getShipType());
                remainingLocationsSizeOfShips.put(ship.getShipType(), currentLocationsSizeOfShip - hitLocations.size());
            }
        });
        return hitShipsAndLocations;
    }

    private Long getNumberOfRemainingShips(Map<String, Integer> remainingLocationsSizeOfShips) {
        return remainingLocationsSizeOfShips.values().stream()
                .filter(value -> value != 0)
                .count();
    }

    private List<String> getShipsSunk(Map<String, Integer> remainingLocationsSizeOfShips, List<String> sunkShipsList) {
        List<String> allShipsSunk = remainingLocationsSizeOfShips.entrySet().stream()
                .filter(entry -> entry.getValue() == 0)
                .map(Map.Entry::getKey)
                .toList();
        List<String> shipsSunkInThisTurn = allShipsSunk.stream()
                .filter(shipSunk -> !sunkShipsList.contains(shipSunk))
                .toList();
        sunkShipsList.addAll(shipsSunkInThisTurn);
        return shipsSunkInThisTurn;
    }
}
