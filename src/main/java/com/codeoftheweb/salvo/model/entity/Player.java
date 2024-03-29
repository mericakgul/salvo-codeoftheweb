package com.codeoftheweb.salvo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Table(name = "players")
@Setter
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "player")
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "player")
    private List<Score> scores = new ArrayList<>();

    public Player(String username, String password){
        this.username = username;
        this.password = password;
    }
}
