package com.codeoftheweb.salvo.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "salvo_locations")
@Getter
@Setter
@NoArgsConstructor
public class SalvoLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grid_cell")
    private String gridCell;

    @ManyToOne
    @JoinColumn(name = "salvo_id")
    private Salvo salvo;

    public SalvoLocation(Salvo salvo, String gridCell){
        this.salvo = salvo;
        this.gridCell = gridCell;
    }

}
