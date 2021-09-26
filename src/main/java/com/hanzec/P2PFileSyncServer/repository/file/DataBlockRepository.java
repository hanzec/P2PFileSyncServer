package com.hanzec.P2PFileSyncServer.repository.file;

import com.hanzec.P2PFileSyncServer.model.data.file.DataBlock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataBlockRepository extends JpaRepository<DataBlock, String> {
}
