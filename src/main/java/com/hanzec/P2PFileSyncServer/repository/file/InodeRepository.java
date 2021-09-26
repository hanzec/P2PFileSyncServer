package com.hanzec.P2PFileSyncServer.repository.file;

import com.hanzec.P2PFileSyncServer.model.data.file.Inode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InodeRepository extends JpaRepository<Inode, String> {
}
