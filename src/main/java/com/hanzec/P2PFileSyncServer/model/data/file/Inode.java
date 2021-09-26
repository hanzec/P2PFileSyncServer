package com.hanzec.P2PFileSyncServer.model.data.file;

import lombok.Getter;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "inode")
public class Inode {
    @Id @Column(name = "inode_path")
    private String path;

    @Getter
    private ZonedDateTime atime;

    @Getter
    private ZonedDateTime ctime;

    @Getter
    private ZonedDateTime mtime;

    @Column(name = "user_id")
    private Integer userID;

    @Column(name = "group_id")
    private Integer groupID;

    @Column(name = "inode_name")
    private String INodeName;

    @ManyToOne
    @JoinColumn(
            name="parent_inode",
            referencedColumnName = "inode_path"
    )
    private Inode parentInode;

    @OneToMany(mappedBy="path",cascade={CascadeType.ALL})
    private Set<Inode> subInode = new HashSet<>();
}
