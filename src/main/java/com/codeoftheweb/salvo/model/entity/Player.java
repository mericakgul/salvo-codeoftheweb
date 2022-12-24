package com.codeoftheweb.salvo.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
//@Where(clause = "DELETED_DATE = '1970-01-01 00:00:00.000'") // For Soft deleting
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;

//    @Column(name = "deleted_date")
//    private Date deletedDate = DeletedDateUtil.getDefaultDeletedDate();

    public Player(String username){
        this.username = username;
    }


//    public Player(PlayerDto playerDTO) {
//        this.username = playerDTO.getUsername();
//    }


}
