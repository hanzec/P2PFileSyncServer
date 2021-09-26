package com.hanzec.P2PFileSyncServer.repository.file;

import com.hanzec.P2PFileSyncServer.model.data.file.FileBlock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileBlockRepository extends JpaRepository<FileBlock, String> {
}
