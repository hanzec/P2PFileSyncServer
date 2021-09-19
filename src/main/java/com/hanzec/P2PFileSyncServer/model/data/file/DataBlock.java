package com.hanzec.P2PFileSyncServer.model.data.file;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "data_block")
public class DataBlock {
    @Id @Column(name = "data_block_id")
    private String blockID;

    @Column(name = "block_path")
    private String blockPath;
}
