package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.model.entity.*;
import com.codeoftheweb.salvo.repository.*;
import com.codeoftheweb.salvo.service.SalvoUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository,
                                      GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository,
                                      ShipLocationRepository shipLocationRepository, SalvoRepository salvoRepository,
                                      SalvoLocationRepository salvoLocationRepository, ScoreRepository scoreRepository) {
        return (args) -> {
            // Players
            Player player1 = new Player("j.bauer@ctu.gov", passwordEncoder().encode("24"));
            Player player2 = new Player("c.obrian@ctu.gov", passwordEncoder().encode("42"));
            Player player3 = new Player("kim_bauer@gmail.com", passwordEncoder().encode("kb"));
            Player player4 = new Player("t.almeida@ctu.gov", passwordEncoder().encode("mole"));
            playerRepository.save(player1);
            playerRepository.save(player2);
            playerRepository.save(player3);
            playerRepository.save(player4);

            // Games
            Game game1 = new Game(new Date());
            Game game2 = new Game(Date.from(new Date().toInstant().plusSeconds(3600)));
            Game game3 = new Game(Date.from(new Date().toInstant().plusSeconds(5400)));
            Game game4 = new Game(Date.from(new Date().toInstant().plusSeconds(7200)));
            Game game5 = new Game(Date.from(new Date().toInstant().plusSeconds(9000)));
            Game game6 = new Game(Date.from(new Date().toInstant().plusSeconds(10800)));
            Game game7 = new Game(Date.from(new Date().toInstant().plusSeconds(12600)));
            Game game8 = new Game(Date.from(new Date().toInstant().plusSeconds(14400)));
            gameRepository.save(game1);
            gameRepository.save(game2);
            gameRepository.save(game3);
            gameRepository.save(game4);
            gameRepository.save(game5);
            gameRepository.save(game6);
            gameRepository.save(game7);
            gameRepository.save(game8);

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
            gamePlayerRepository.save(gamePlayer1);
            gamePlayerRepository.save(gamePlayer2);
            gamePlayerRepository.save(gamePlayer3);
            gamePlayerRepository.save(gamePlayer4);
            gamePlayerRepository.save(gamePlayer5);
            gamePlayerRepository.save(gamePlayer6);
            gamePlayerRepository.save(gamePlayer7);
            gamePlayerRepository.save(gamePlayer8);
            gamePlayerRepository.save(gamePlayer9);
            gamePlayerRepository.save(gamePlayer10);
            gamePlayerRepository.save(gamePlayer11);
            gamePlayerRepository.save(gamePlayer12);
            gamePlayerRepository.save(gamePlayer13);
            gamePlayerRepository.save(gamePlayer14);

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
            shipRepository.save(ship1);
            shipRepository.save(ship2);
            shipRepository.save(ship3);
            shipRepository.save(ship4);
            shipRepository.save(ship5);
            shipRepository.save(ship6);
            shipRepository.save(ship7);
            shipRepository.save(ship8);
            shipRepository.save(ship9);
            shipRepository.save(ship10);
            shipRepository.save(ship11);
            shipRepository.save(ship12);
            shipRepository.save(ship13);
            shipRepository.save(ship14);
            shipRepository.save(ship15);
            shipRepository.save(ship16);
            shipRepository.save(ship17);
            shipRepository.save(ship18);
            shipRepository.save(ship19);
            shipRepository.save(ship20);
            shipRepository.save(ship21);
            shipRepository.save(ship22);
            shipRepository.save(ship23);
            shipRepository.save(ship24);
            shipRepository.save(ship25);
            shipRepository.save(ship26);
            shipRepository.save(ship27);

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
            shipLocations.add(new ShipLocation(ship18, "b5"));
            shipLocations.add(new ShipLocation(ship18, "C5"));
            shipLocations.add(new ShipLocation(ship18, "D5"));
            //Ship19 Locations
            shipLocations.add(new ShipLocation(ship19, "c6"));
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
            shipLocationRepository.saveAll(shipLocations);

            // Salvoes
            Salvo salvo1 = new Salvo(gamePlayer1, 1);
            Salvo salvo2 = new Salvo(gamePlayer2, 1);
            Salvo salvo3 = new Salvo(gamePlayer1, 2);
            Salvo salvo4 = new Salvo(gamePlayer2, 2);
            Salvo salvo5 = new Salvo(gamePlayer3, 1);
            Salvo salvo6 = new Salvo(gamePlayer4, 1);
            Salvo salvo7 = new Salvo(gamePlayer3, 2);
            Salvo salvo8 = new Salvo(gamePlayer4, 2);
            Salvo salvo9 = new Salvo(gamePlayer5, 1);
            Salvo salvo10 = new Salvo(gamePlayer6, 1);
            Salvo salvo11 = new Salvo(gamePlayer5, 2);
            Salvo salvo12 = new Salvo(gamePlayer6, 2);
            Salvo salvo13 = new Salvo(gamePlayer7, 1);
            Salvo salvo14 = new Salvo(gamePlayer8, 1);
            Salvo salvo15 = new Salvo(gamePlayer7, 2);
            Salvo salvo16 = new Salvo(gamePlayer8, 2);
            Salvo salvo17 = new Salvo(gamePlayer9, 1);
            Salvo salvo18 = new Salvo(gamePlayer10, 1);
            Salvo salvo19 = new Salvo(gamePlayer9, 2);
            Salvo salvo20 = new Salvo(gamePlayer10, 2);
            Salvo salvo21 = new Salvo(gamePlayer10, 3);
            salvoRepository.save(salvo1);
            salvoRepository.save(salvo2);
            salvoRepository.save(salvo3);
            salvoRepository.save(salvo4);
            salvoRepository.save(salvo5);
            salvoRepository.save(salvo6);
            salvoRepository.save(salvo7);
            salvoRepository.save(salvo8);
            salvoRepository.save(salvo9);
            salvoRepository.save(salvo10);
            salvoRepository.save(salvo11);
            salvoRepository.save(salvo12);
            salvoRepository.save(salvo13);
            salvoRepository.save(salvo14);
            salvoRepository.save(salvo15);
            salvoRepository.save(salvo16);
            salvoRepository.save(salvo17);
            salvoRepository.save(salvo18);
            salvoRepository.save(salvo19);
            salvoRepository.save(salvo20);
            salvoRepository.save(salvo21);

            // SALVO LOCATIONS
            List<SalvoLocation> salvoLocations = new ArrayList<>();
            // Salvo1 Locations
            salvoLocations.add(new SalvoLocation(salvo1, "B5"));
            salvoLocations.add(new SalvoLocation(salvo1, "C5"));
            salvoLocations.add(new SalvoLocation(salvo1, "F1"));
            // Salvo2 Locations
            salvoLocations.add(new SalvoLocation(salvo2, "B4"));
            salvoLocations.add(new SalvoLocation(salvo2, "B5"));
            salvoLocations.add(new SalvoLocation(salvo2, "B6"));
            // Salvo3 Locations
            salvoLocations.add(new SalvoLocation(salvo3, "F2"));
            salvoLocations.add(new SalvoLocation(salvo3, "D5"));
            // Salvo4 Locations
            salvoLocations.add(new SalvoLocation(salvo4, "E1"));
            salvoLocations.add(new SalvoLocation(salvo4, "H3"));
            salvoLocations.add(new SalvoLocation(salvo4, "A2"));
            // Salvo5 Locations
            salvoLocations.add(new SalvoLocation(salvo5, "A2"));
            salvoLocations.add(new SalvoLocation(salvo5, "A4"));
            salvoLocations.add(new SalvoLocation(salvo5, "G6"));
            // Salvo6 Locations
            salvoLocations.add(new SalvoLocation(salvo6, "B5"));
            salvoLocations.add(new SalvoLocation(salvo6, "D5"));
            salvoLocations.add(new SalvoLocation(salvo6, "C7"));
            // Salvo7 Locations
            salvoLocations.add(new SalvoLocation(salvo7, "A3"));
            salvoLocations.add(new SalvoLocation(salvo7, "H6"));
            // Salvo8 Locations
            salvoLocations.add(new SalvoLocation(salvo8, "C5"));
            salvoLocations.add(new SalvoLocation(salvo8, "C6"));
            // Salvo9 Locations
            salvoLocations.add(new SalvoLocation(salvo9, "G6"));
            salvoLocations.add(new SalvoLocation(salvo9, "H6"));
            salvoLocations.add(new SalvoLocation(salvo9, "A4"));
            // Salvo10 Locations
            salvoLocations.add(new SalvoLocation(salvo10, "H1"));
            salvoLocations.add(new SalvoLocation(salvo10, "H2"));
            salvoLocations.add(new SalvoLocation(salvo10, "H3"));
            // Salvo11 Locations
            salvoLocations.add(new SalvoLocation(salvo11, "A2"));
            salvoLocations.add(new SalvoLocation(salvo11, "A3"));
            salvoLocations.add(new SalvoLocation(salvo11, "D8"));
            // Salvo12 Locations
            salvoLocations.add(new SalvoLocation(salvo12, "E1"));
            salvoLocations.add(new SalvoLocation(salvo12, "F2"));
            salvoLocations.add(new SalvoLocation(salvo12, "G3"));
            // Salvo13 Locations
            salvoLocations.add(new SalvoLocation(salvo13, "A3"));
            salvoLocations.add(new SalvoLocation(salvo13, "A4"));
            salvoLocations.add(new SalvoLocation(salvo13, "F7"));
            // Salvo14 Locations
            salvoLocations.add(new SalvoLocation(salvo14, "B5"));
            salvoLocations.add(new SalvoLocation(salvo14, "C6"));
            salvoLocations.add(new SalvoLocation(salvo14, "H1"));
            // Salvo15 Locations
            salvoLocations.add(new SalvoLocation(salvo15, "A2"));
            salvoLocations.add(new SalvoLocation(salvo15, "G6"));
            salvoLocations.add(new SalvoLocation(salvo15, "H6"));
            // Salvo16 Locations
            salvoLocations.add(new SalvoLocation(salvo16, "C5"));
            salvoLocations.add(new SalvoLocation(salvo16, "C7"));
            salvoLocations.add(new SalvoLocation(salvo16, "D5"));
            // Salvo17 Locations
            salvoLocations.add(new SalvoLocation(salvo17, "A1"));
            salvoLocations.add(new SalvoLocation(salvo17, "A2"));
            salvoLocations.add(new SalvoLocation(salvo17, "A3"));
            // Salvo18 Locations
            salvoLocations.add(new SalvoLocation(salvo18, "B5"));
            salvoLocations.add(new SalvoLocation(salvo18, "B6"));
            salvoLocations.add(new SalvoLocation(salvo18, "C7"));
            // Salvo19 Locations
            salvoLocations.add(new SalvoLocation(salvo19, "G6"));
            salvoLocations.add(new SalvoLocation(salvo19, "G7"));
            salvoLocations.add(new SalvoLocation(salvo19, "G8"));
            // Salvo20 Locations
            salvoLocations.add(new SalvoLocation(salvo20, "C6"));
            salvoLocations.add(new SalvoLocation(salvo20, "D6"));
            salvoLocations.add(new SalvoLocation(salvo20, "E6"));
            // Salvo21 Locations
            salvoLocations.add(new SalvoLocation(salvo21, "H1"));
            salvoLocations.add(new SalvoLocation(salvo21, "H8"));
            salvoLocationRepository.saveAll(salvoLocations);

            //Scores
            List<Score> scores = new ArrayList<>();
            scores.add(new Score(game1, player1, 1.0, new Date()));
            scores.add(new Score(game1, player2, 0.0, new Date()));
            scores.add(new Score(game2, player1, 0.5, new Date()));
            scores.add(new Score(game2, player2, 0.5, new Date()));
            scores.add(new Score(game3, player2, 1.0, new Date()));
            scores.add(new Score(game3, player4, 0.0, new Date()));
            scores.add(new Score(game4, player2, 0.5, new Date()));
            scores.add(new Score(game4, player1, 0.5, new Date()));
            scoreRepository.saveAll(scores);
        };
    }
}

@Configuration
@RequiredArgsConstructor
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
    private final SalvoUserDetailsService salvoUserDetailsService;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(salvoUserDetailsService);
    }
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] whiteList = {"/web/games.html", "/web/game.html", "/scripts/**", "/styles/**", "/rest/**", "/api/games", "/api/players"};

        http.authorizeRequests()
                .antMatchers(whiteList).permitAll()
                .antMatchers("/**").hasAuthority("USER")
                .and()
                .formLogin();

        http.formLogin()
                .loginProcessingUrl("/api/login")
                .successHandler((req, res, auth) -> clearAuthenticationAttributes(req))
                .failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        http.logout().logoutUrl("/api/logout");

        http.csrf().disable();

//        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendRedirect("/web/games.html")); // Bu satir oldugu surece, wrong credential hatasi da failureHandler'dan sonra burada da yakalaniyor ve unauthorised hatasini alamiyorum hep 302 aliyorum.

        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}

