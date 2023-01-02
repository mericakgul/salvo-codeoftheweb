package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.model.entity.Game;
import com.codeoftheweb.salvo.model.entity.GamePlayer;
import com.codeoftheweb.salvo.model.entity.Player;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class SalvoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository) {
        return (args) -> {
            Player player1 = new Player("j.bauer@ctu.gov");
            Player player2 = new Player("c.obrian@ctu.gov");
            Player player3 = new Player("kim_bauer@gmail.com");
            Player player4 = new Player("t.almeida@ctu.gov");
            playerRepository.save(player1);
            playerRepository.save(player2);
            playerRepository.save(player3);
            playerRepository.save(player4);

            Game game1 = new Game(new Date());
            Game game2 = new Game(Date.from(new Date().toInstant().plusSeconds(3600)));
            Game game3 = new Game(Date.from(new Date().toInstant().plusSeconds(7200)));
            gameRepository.save(game1);
            gameRepository.save(game2);
            gameRepository.save(game3);

            GamePlayer gamePlayerOne = new GamePlayer(game1, player1, new Date());
            GamePlayer gamePlayerTwo = new GamePlayer(game1, player2, new Date());
            GamePlayer gamePlayerThree = new GamePlayer(game2, player1, new Date());
            GamePlayer gamePlayerFour = new GamePlayer(game2, player4, new Date());
            GamePlayer gamePlayerFive = new GamePlayer(game3, player1, new Date());
            GamePlayer gamePlayerSix = new GamePlayer(game3, player3, new Date());
            gamePlayerRepository.save(gamePlayerOne);
            gamePlayerRepository.save(gamePlayerTwo);
            gamePlayerRepository.save(gamePlayerThree);
            gamePlayerRepository.save(gamePlayerFour);
            gamePlayerRepository.save(gamePlayerFive);
            gamePlayerRepository.save(gamePlayerSix);
        };
    }

}
