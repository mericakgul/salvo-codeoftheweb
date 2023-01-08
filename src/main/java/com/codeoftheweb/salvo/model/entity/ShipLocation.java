package com.codeoftheweb.salvo.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ship_locations")
@Getter
@Setter
@NoArgsConstructor
public class ShipLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grid_cell")
    private String gridCell;

    @ManyToOne
    @JoinColumn(name = "ship_id")
    private Ship ship;

    public ShipLocation(Ship ship, String gridCell){
        this.ship = ship;
        this.gridCell = gridCell;
    }

}
