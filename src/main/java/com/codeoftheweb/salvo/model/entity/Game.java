package com.codeoftheweb.salvo.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "game")
@Getter
@Setter
@NoArgsConstructor
//@Where(clause = "DELETED_DATE = '1970-01-01 00:00:00.000'") // For Soft deleting
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creation_date")
    private Date creationDate;

    @OneToMany(mappedBy = "game")
    private Set<GamePlayer> gamePlayers = new HashSet<>();

//    @Column(name = "deleted_date")
//    private Date deletedDate = DeletedDateUtil.getDefaultDeletedDate();

    public Game(Date creationDate){
        this.creationDate = creationDate;
    }

}
