package com.hanzec.P2PFileSyncServer.repository.file;

import com.hanzec.P2PFileSyncServer.model.data.file.FileDatabase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileDatabaseRepository extends JpaRepository<FileDatabase, UUID>  {
}
