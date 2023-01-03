package com.codeoftheweb.salvo.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ships")
@Getter
@Setter
@NoArgsConstructor
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String shipType;

    @ManyToOne
    @JoinColumn(name = "game_player_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name = "locations")
    private List<String> locations = new ArrayList<>();

    public Ship(String shipType, GamePlayer gamePlayer, List<String> locations){
        this.shipType = shipType;
        this.gamePlayer = gamePlayer;
        this.locations = locations;
    }
}
