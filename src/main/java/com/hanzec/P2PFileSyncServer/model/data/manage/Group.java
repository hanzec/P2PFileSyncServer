package com.hanzec.P2PFileSyncServer.model.data.manage;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
public class Group {
    @GeneratedValue
    @Id @Column(name = "group_id")
    private UUID groupID;

    @ManyToMany
    private List<User> groupUser;
}
