package com.hanzec.P2PFileSyncServer.model.data.file;

import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "file_block")
public class FileBlock {
    @Id @Column(name = "file_block_id")
    private String fileBlockID;

    @Setter
    @NotNull
    @Column(name = "file_index")
    private Integer fileIndex;

    public FileBlock() { }

    public FileBlock(DataBlock dataBlock, int index){
        this.fileIndex = index;
        this.fileBlockID = dataBlock.getBlockID();
    }
}
