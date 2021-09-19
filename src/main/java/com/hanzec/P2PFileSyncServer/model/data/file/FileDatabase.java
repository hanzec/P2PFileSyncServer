package com.hanzec.P2PFileSyncServer.model.data.file;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "file_database")
public class FileDatabase {
    @GeneratedValue
    @Id @Column(name = "database_id")
    private UUID databaseID;
}
