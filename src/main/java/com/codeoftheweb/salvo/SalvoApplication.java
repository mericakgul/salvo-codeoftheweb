package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.model.entity.Game;
import com.codeoftheweb.salvo.model.entity.Player;
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
    public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository) {
        return (args) -> {
            playerRepository.save(new Player("j.bauer@ctu.gov"));
            playerRepository.save(new Player("c.obrian@ctu.gov"));
            playerRepository.save(new Player("kim_bauer@gmail.com"));
            playerRepository.save(new Player("t.almeida@ctu.gov"));

            gameRepository.save(new Game(new Date()));
            gameRepository.save(new Game(Date.from(new Date().toInstant().plusSeconds(3600))));
            gameRepository.save(new Game(Date.from(new Date().toInstant().plusSeconds(7200))));
        };
    }

}
