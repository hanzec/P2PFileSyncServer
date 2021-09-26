package com.hanzec.P2PFileSyncServer.model.data.manage;

import com.hanzec.P2PFileSyncServer.model.data.manage.account.UserAccount;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "USER_GROUP"
)
public class Group {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    @NotNull
    @Column(name = "NAME")
    private String name;

    @ManyToMany
    private List<UserAccount> groupUserAccount;
}
