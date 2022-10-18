package com.codeoftheweb.salvo.model.dto;

import com.codeoftheweb.salvo.model.entity.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerDTO {
    private Long id;
    private String userName;
    public PlayerDTO(Player player){
        this.id = player.getId();
        this.userName = player.getUserName();
    }
}
