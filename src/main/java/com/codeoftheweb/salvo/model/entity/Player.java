package com.codeoftheweb.salvo.model.entity;

import com.codeoftheweb.salvo.model.dto.PlayerDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String userName;
    public Player(PlayerDTO playerDTO){
        this.userName = playerDTO.getUserName();
    }

}
