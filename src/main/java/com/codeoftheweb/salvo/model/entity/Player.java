package com.codeoftheweb.salvo.model.entity;

import com.codeoftheweb.salvo.core.util.DeletedDateUtil;
import com.codeoftheweb.salvo.model.dto.PlayerDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Where(clause = "DELETED_DATE = '1970-01-01 00:00:00.000'") // For Soft deleting
public class Player {

    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    private UUID uuid = UUID.randomUUID();
    @Column(unique = true)
    private String userName;

    @Column(name = "deleted_date")
    private Date deletedDate = DeletedDateUtil.getDefaultDeletedDate();

    public Player(PlayerDto playerDTO) {
        this.userName = playerDTO.getUserName();
    }

}
