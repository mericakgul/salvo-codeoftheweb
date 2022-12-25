package com.codeoftheweb.salvo.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "game_player")
@Getter
@Setter
@NoArgsConstructor
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creation_date")
    private Date creationDate;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;


    public GamePlayer(Game game, Player player, Date creationDate){
        this.game = game;
        this.player = player;
        this.creationDate = creationDate;
    }

//    public Set<GamePlayer> getGames(){ // 2nd task 2nd plan of attack. What is it asking by getPlayer() and getGame() methods.
//        return this.player.getGamePlayers();
//    }
//
//    public Set<GamePlayer> getPlayers(){
//        return this.game.getGamePlayers();
//    }
}
