package com.codeoftheweb.salvo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "game_players")
@Getter
@Setter
@NoArgsConstructor
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "join_date")
    private Date joinDate;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @JsonIgnore
    @OneToMany(mappedBy = "gamePlayer")
    private Set<Ship> ships = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "gamePlayer")
    private Set<Salvo> salvoes = new HashSet<>();


    public GamePlayer(Game game, Player player, Date joinDate){
        this.game = game;
        this.player = player;
        this.joinDate = joinDate;
    }

//    @JsonIgnore
//    public Set<GamePlayer> getGames(){ // 2nd task 2nd plan of attack. What is it asking by getPlayer() and getGame() methods.
//        return this.player.getGamePlayers();
//    }
//
//    @JsonIgnore
//    public Set<GamePlayer> getPlayers(){
//        return this.game.getGamePlayers();
//    }
}
