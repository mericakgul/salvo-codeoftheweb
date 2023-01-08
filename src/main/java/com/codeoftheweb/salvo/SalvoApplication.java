package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.model.entity.*;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.*;

@SpringBootApplication
public class SalvoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository,
                                      GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository,
                                      LocationRepository locationRepository) {
        return (args) -> {
            // Players
            Player player1 = new Player("j.bauer@ctu.gov");
            Player player2 = new Player("c.obrian@ctu.gov");
            Player player3 = new Player("kim_bauer@gmail.com");
            Player player4 = new Player("t.almeida@ctu.gov");
            playerRepository.save(player1); playerRepository.save(player2);
            playerRepository.save(player3); playerRepository.save(player4);

            // Games
            Game game1 = new Game(new Date());
            Game game2 = new Game(Date.from(new Date().toInstant().plusSeconds(3600)));
            Game game3 = new Game(Date.from(new Date().toInstant().plusSeconds(5400)));
            Game game4 = new Game(Date.from(new Date().toInstant().plusSeconds(7200)));
            Game game5 = new Game(Date.from(new Date().toInstant().plusSeconds(9000)));
            Game game6 = new Game(Date.from(new Date().toInstant().plusSeconds(10800)));
            Game game7 = new Game(Date.from(new Date().toInstant().plusSeconds(12600)));
            Game game8 = new Game(Date.from(new Date().toInstant().plusSeconds(14400)));
            gameRepository.save(game1); gameRepository.save(game2); gameRepository.save(game3); gameRepository.save(game4);
            gameRepository.save(game5); gameRepository.save(game6); gameRepository.save(game7); gameRepository.save(game8);

            // GamePlayers
            GamePlayer gamePlayer1 = new GamePlayer(game1, player1, new Date());
            GamePlayer gamePlayer2 = new GamePlayer(game1, player2, new Date());
            GamePlayer gamePlayer3 = new GamePlayer(game2, player1, new Date());
            GamePlayer gamePlayer4 = new GamePlayer(game2, player2, new Date());
            GamePlayer gamePlayer5 = new GamePlayer(game3, player2, new Date());
            GamePlayer gamePlayer6 = new GamePlayer(game3, player4, new Date());
            GamePlayer gamePlayer7 = new GamePlayer(game4, player2, new Date());
            GamePlayer gamePlayer8 = new GamePlayer(game4, player1, new Date());
            GamePlayer gamePlayer9 = new GamePlayer(game5, player4, new Date());
            GamePlayer gamePlayer10 = new GamePlayer(game5, player1, new Date());
            GamePlayer gamePlayer11 = new GamePlayer(game6, player3, new Date());
            GamePlayer gamePlayer12 = new GamePlayer(game7, player4, new Date());
            GamePlayer gamePlayer13 = new GamePlayer(game8, player3, new Date());
            GamePlayer gamePlayer14 = new GamePlayer(game8, player4, new Date());
            gamePlayerRepository.save(gamePlayer1); gamePlayerRepository.save(gamePlayer2); gamePlayerRepository.save(gamePlayer3);
            gamePlayerRepository.save(gamePlayer4); gamePlayerRepository.save(gamePlayer5); gamePlayerRepository.save(gamePlayer6);
            gamePlayerRepository.save(gamePlayer7); gamePlayerRepository.save(gamePlayer8); gamePlayerRepository.save(gamePlayer9);
            gamePlayerRepository.save(gamePlayer10); gamePlayerRepository.save(gamePlayer11); gamePlayerRepository.save(gamePlayer12);
            gamePlayerRepository.save(gamePlayer13); gamePlayerRepository.save(gamePlayer14);

            // Ships
            Ship ship1 = new Ship("Destroyer", gamePlayer1);
            Ship ship2 = new Ship("Submarine", gamePlayer1);
            Ship ship3 = new Ship("Patrol Boat", gamePlayer1);
            Ship ship4 = new Ship("Destroyer", gamePlayer2);
            Ship ship5 = new Ship("Patrol Boat", gamePlayer2);
            Ship ship6 = new Ship("Destroyer", gamePlayer3);
            Ship ship7 = new Ship("Patrol Boat", gamePlayer3);
            Ship ship8 = new Ship("Submarine", gamePlayer4);
            Ship ship9 = new Ship("Patrol Boat", gamePlayer4);
            Ship ship10 = new Ship("Destroyer", gamePlayer5);
            Ship ship11 = new Ship("Patrol Boat", gamePlayer5);
            Ship ship12 = new Ship("Submarine", gamePlayer6);
            Ship ship13 = new Ship("Patrol Boat", gamePlayer6);
            Ship ship14 = new Ship("Destroyer", gamePlayer7);
            Ship ship15 = new Ship("Patrol Boat", gamePlayer7);
            Ship ship16 = new Ship("Submarine", gamePlayer8);
            Ship ship17 = new Ship("Patrol Boat", gamePlayer8);
            Ship ship18 = new Ship("Destroyer", gamePlayer9);
            Ship ship19 = new Ship("Patrol Boat", gamePlayer9);
            Ship ship20 = new Ship("Submarine", gamePlayer10);
            Ship ship21 = new Ship("Patrol Boat", gamePlayer10);
            Ship ship22 = new Ship("Destroyer", gamePlayer11);
            Ship ship23 = new Ship("Patrol Boat", gamePlayer11);
            Ship ship24 = new Ship("Destroyer", gamePlayer13);
            Ship ship25 = new Ship("Patrol Boat", gamePlayer13);
            Ship ship26 = new Ship("Submarine", gamePlayer14);
            Ship ship27 = new Ship("Patrol Boat", gamePlayer14);
            shipRepository.save(ship1); shipRepository.save(ship2); shipRepository.save(ship3); shipRepository.save(ship4);
            shipRepository.save(ship5); shipRepository.save(ship6); shipRepository.save(ship7); shipRepository.save(ship8);
            shipRepository.save(ship9); shipRepository.save(ship10); shipRepository.save(ship11); shipRepository.save(ship12);
            shipRepository.save(ship13); shipRepository.save(ship14); shipRepository.save(ship15); shipRepository.save(ship16);
            shipRepository.save(ship17); shipRepository.save(ship18); shipRepository.save(ship19); shipRepository.save(ship20);
            shipRepository.save(ship21); shipRepository.save(ship22); shipRepository.save(ship23); shipRepository.save(ship24);
            shipRepository.save(ship25); shipRepository.save(ship26); shipRepository.save(ship27);

            // SHIP LOCATIONS
            Set<ShipLocation> shipLocations = new HashSet<>();
            // Ship1 Locations
            shipLocations.add(new ShipLocation(ship1, "H2"));
            shipLocations.add(new ShipLocation(ship1, "H3"));
            shipLocations.add(new ShipLocation(ship1, "H4"));
            //Ship2 Locations
            shipLocations.add(new ShipLocation(ship2, "E1"));
            shipLocations.add(new ShipLocation(ship2, "F1"));
            shipLocations.add(new ShipLocation(ship2, "G1"));
            //Ship3 Locations
            shipLocations.add(new ShipLocation(ship3, "B4"));
            shipLocations.add(new ShipLocation(ship3, "B5"));
            //Ship4 Locations
            shipLocations.add(new ShipLocation(ship4, "B5"));
            shipLocations.add(new ShipLocation(ship4, "C5"));
            shipLocations.add(new ShipLocation(ship4, "D5"));
            //Ship5 Locations
            shipLocations.add(new ShipLocation(ship5, "F1"));
            shipLocations.add(new ShipLocation(ship5, "F2"));
            //Ship6 Locations
            shipLocations.add(new ShipLocation(ship6, "B5"));
            shipLocations.add(new ShipLocation(ship6, "C5"));
            shipLocations.add(new ShipLocation(ship6, "D5"));
            //Ship7 Locations
            shipLocations.add(new ShipLocation(ship7, "C6"));
            shipLocations.add(new ShipLocation(ship7, "C7"));
            //Ship8 Locations
            shipLocations.add(new ShipLocation(ship8, "A2"));
            shipLocations.add(new ShipLocation(ship8, "A3"));
            shipLocations.add(new ShipLocation(ship8, "A4"));
            //Ship9 Locations
            shipLocations.add(new ShipLocation(ship9, "G6"));
            shipLocations.add(new ShipLocation(ship9, "H6"));
            //Ship10 Locations
            shipLocations.add(new ShipLocation(ship10, "B5"));
            shipLocations.add(new ShipLocation(ship10, "C5"));
            shipLocations.add(new ShipLocation(ship10, "D5"));
            //Ship11 Locations
            shipLocations.add(new ShipLocation(ship11, "C6"));
            shipLocations.add(new ShipLocation(ship11, "C7"));
            //Ship12 Locations
            shipLocations.add(new ShipLocation(ship12, "A2"));
            shipLocations.add(new ShipLocation(ship12, "A3"));
            shipLocations.add(new ShipLocation(ship12, "A4"));
            //Ship13 Locations
            shipLocations.add(new ShipLocation(ship13, "G6"));
            shipLocations.add(new ShipLocation(ship13, "H6"));
            //Ship14 Locations
            shipLocations.add(new ShipLocation(ship14, "B5"));
            shipLocations.add(new ShipLocation(ship14, "C5"));
            shipLocations.add(new ShipLocation(ship14, "D5"));
            //Ship15 Locations
            shipLocations.add(new ShipLocation(ship15, "C6"));
            shipLocations.add(new ShipLocation(ship15, "C7"));
            //Ship16 Locations
            shipLocations.add(new ShipLocation(ship16, "A2"));
            shipLocations.add(new ShipLocation(ship16, "A3"));
            shipLocations.add(new ShipLocation(ship16, "A4"));
            //Ship17 Locations
            shipLocations.add(new ShipLocation(ship17, "G6"));
            shipLocations.add(new ShipLocation(ship17, "H6"));
            //Ship18 Locations
            shipLocations.add(new ShipLocation(ship18, "B5"));
            shipLocations.add(new ShipLocation(ship18, "C5"));
            shipLocations.add(new ShipLocation(ship18, "D5"));
            //Ship19 Locations
            shipLocations.add(new ShipLocation(ship19, "C6"));
            shipLocations.add(new ShipLocation(ship19, "C7"));
            //Ship20 Locations
            shipLocations.add(new ShipLocation(ship20, "A2"));
            shipLocations.add(new ShipLocation(ship20, "A3"));
            shipLocations.add(new ShipLocation(ship20, "A4"));
            //Ship21 Locations
            shipLocations.add(new ShipLocation(ship21, "G6"));
            shipLocations.add(new ShipLocation(ship21, "H6"));
            //Ship22 Locations
            shipLocations.add(new ShipLocation(ship22, "B5"));
            shipLocations.add(new ShipLocation(ship22, "C5"));
            shipLocations.add(new ShipLocation(ship22, "D5"));
            //Ship23 Locations
            shipLocations.add(new ShipLocation(ship23, "C6"));
            shipLocations.add(new ShipLocation(ship23, "C7"));
            //Ship24 Locations
            shipLocations.add(new ShipLocation(ship24, "B5"));
            shipLocations.add(new ShipLocation(ship24, "C5"));
            shipLocations.add(new ShipLocation(ship24, "D5"));
            //Ship25 Locations
            shipLocations.add(new ShipLocation(ship25, "C6"));
            shipLocations.add(new ShipLocation(ship25, "C7"));
            //Ship26 Locations
            shipLocations.add(new ShipLocation(ship26, "A2"));
            shipLocations.add(new ShipLocation(ship26, "A3"));
            shipLocations.add(new ShipLocation(ship26, "A4"));
            //Ship27 Locations
            shipLocations.add(new ShipLocation(ship27, "G6"));
            shipLocations.add(new ShipLocation(ship27, "H6"));
            locationRepository.saveAll(shipLocations);

        };
    }

}
