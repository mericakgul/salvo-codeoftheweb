package com.codeoftheweb.salvo.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "scores")
@Getter
@Setter
@NoArgsConstructor
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @Column(name = "score_number")
    private Double scoreNumber;

    @Column(name = "finish_date")
    private Date finishDate;

    public Score(Game game, Player player, Double scoreNumber, Date finishDate){
        this.game = game;
        this.player = player;
        this.scoreNumber = scoreNumber;
        this.finishDate = finishDate;
    }
}
