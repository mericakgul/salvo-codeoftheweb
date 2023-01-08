package com.codeoftheweb.salvo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    @JsonIgnore
    @OneToMany(mappedBy = "ship")
    private Set<ShipLocation> shipLocations = new HashSet<>();

    public Ship(String shipType, GamePlayer gamePlayer){
        this.shipType = shipType;
        this.gamePlayer = gamePlayer;
    }
}
