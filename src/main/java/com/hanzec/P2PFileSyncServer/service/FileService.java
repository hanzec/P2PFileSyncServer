package com.hanzec.P2PFileSyncServer.service;

import com.hanzec.P2PFileSyncServer.model.data.file.DataBlock;
import com.hanzec.P2PFileSyncServer.model.data.file.Inode;
import com.hanzec.P2PFileSyncServer.repository.file.DataBlockRepository;
import com.hanzec.P2PFileSyncServer.repository.file.FileBlockRepository;
import com.hanzec.P2PFileSyncServer.repository.file.FileDatabaseRepository;
import com.hanzec.P2PFileSyncServer.repository.file.InodeRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;


@Service
public class FileService {
    private final HashSet<String> commitIDCache;
    private final InodeRepository inodeRepository;
    private final FileBlockRepository fileBlockRepository;
    private final DataBlockRepository dataBlockRepository;
    private final FileDatabaseRepository fileDatabaseRepository;

    public FileService(InodeRepository inodeRepository,
                       FileBlockRepository fileBlockRepository,
                       DataBlockRepository dataBlockRepository,
                       FileDatabaseRepository fileDatabaseRepository) {
        this.commitIDCache = new HashSet<>();
        this.inodeRepository = inodeRepository;
        this.fileBlockRepository = fileBlockRepository;
        this.dataBlockRepository = dataBlockRepository;
        this.fileDatabaseRepository = fileDatabaseRepository;
    }

    public String propose_new_commit(String path){
        return UUID.randomUUID().toString();
    }
}
