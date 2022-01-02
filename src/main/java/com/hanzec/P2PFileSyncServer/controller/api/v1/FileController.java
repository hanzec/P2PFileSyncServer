package com.hanzec.P2PFileSyncServer.controller.api.v1;

import com.hanzec.P2PFileSyncServer.service.FileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/file")
@Tag(name = "RestAPI Related to manage file")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService){
        this.fileService = fileService;
    }

//    @ResponseBody
//    @GetMapping(value = "/{*folderPath}")
//    @ApiOperation("Get user information")
//    @PreAuthorize("hasAuthority('user_details')")
//    public Response addNewFile(@RequestBody @Validated AddNewFileRequest newFile, @PathVariable String folderPath){
//
//    }

}
