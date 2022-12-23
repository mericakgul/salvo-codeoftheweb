package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.model.dto.PlayerDto;
import com.codeoftheweb.salvo.model.entity.Player;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SalvoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(PlayerRepository playerRepository) {
        return (args) -> {
            playerRepository.save(new Player("j.bauer@ctu.gov"));
            playerRepository.save(new Player("c.obrian@ctu.gov"));
            playerRepository.save(new Player("kim_bauer@gmail.com"));
            playerRepository.save(new Player("t.almeida@ctu.gov"));
        };
    }

}
