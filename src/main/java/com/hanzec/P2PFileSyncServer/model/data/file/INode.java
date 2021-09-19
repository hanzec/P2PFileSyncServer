package com.hanzec.P2PFileSyncServer.model.data.file;

import lombok.Getter;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;


@Entity
@Table(name = "inode")
public class INode {
    @Id @Column(name = "inode_path")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer inodePath;

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
    private INode parentInode;

    @OneToMany(mappedBy="inodePath",cascade={CascadeType.ALL})
    private List<INode> subInode;
}
