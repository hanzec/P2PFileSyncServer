package com.hanzec.P2PFileSyncServer.controller.api;

import com.hanzec.P2PFileSyncServer.model.api.LoginRequest;
import com.hanzec.P2PFileSyncServer.model.api.Response;
import com.hanzec.P2PFileSyncServer.model.data.manage.User;
import com.hanzec.P2PFileSyncServer.model.exception.auth.PasswordNotMatchException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cert")
@Api(tags = "RestAPI Related to Certificate Acquire or validation")
public class CertController {


}
