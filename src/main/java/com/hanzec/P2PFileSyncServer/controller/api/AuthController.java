package com.hanzec.P2PFileSyncServer.controller.api;

import com.hanzec.P2PFileSyncServer.model.data.manage.account.UserAccount;
import com.hanzec.P2PFileSyncServer.model.exception.auth.PasswordNotMatchException;
import com.hanzec.P2PFileSyncServer.model.api.LoginRequest;
import com.hanzec.P2PFileSyncServer.model.api.RegisterUserRequest;
import com.hanzec.P2PFileSyncServer.model.api.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import com.hanzec.P2PFileSyncServer.service.AccountService;
import com.hanzec.P2PFileSyncServer.service.TokenService;

@RestController
@RequestMapping("/api/v1/auth")
@Api(tags = "RestAPI Related to Authentication")
public class AuthController {
    final TokenService tokenService;

    final AccountService accountService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AuthController(AccountService accountService, TokenService tokenService) {
        this.accountService = accountService;
        this.tokenService = tokenService;
    }

    @ResponseBody
    @ApiOperation("Login API")
    @PostMapping(value = "/password")
    @ResponseStatus(HttpStatus.OK)
    public Response login(@RequestBody @Validated LoginRequest loginRequest) throws PasswordNotMatchException {
        UserAccount userAccountCredential = (UserAccount) accountService.loadUserByUsername(loginRequest.getEmail());

        accountService.checkPassword(loginRequest.getEmail(),loginRequest.getPassword());

        logger.debug("User [ " + loginRequest.getEmail() + " ] is permit to login");
        return new Response()
                .addResponse("loginToken", tokenService.create(userAccountCredential));
    }

    @ResponseBody
    @ApiOperation("Request Server public certifies")
    @PostMapping(value = "/server-cert")
    @ResponseStatus(HttpStatus.OK)
    public Response RequestCerts(@RequestBody @Validated LoginRequest loginRequest) throws PasswordNotMatchException {
        UserAccount userAccountCredential = (UserAccount) accountService.loadUserByUsername(loginRequest.getEmail());

        accountService.checkPassword(loginRequest.getEmail(),loginRequest.getPassword());

        logger.debug("User [ " + loginRequest.getEmail() + " ] is permit to login");
        return new Response()
                .addResponse("loginToken", tokenService.create(userAccountCredential));
    }

    @ResponseBody
    @PostMapping(value = "/register")
    @ApiOperation("Used for register new account")
    @ResponseStatus(HttpStatus.CREATED)
    public Response register(@RequestBody @Validated RegisterUserRequest user){
        //Trying to Register new Account to Server
        accountService.createUser(user);

        logger.debug("User [ " + user.getEmail() + " ] is success registered");
        return new Response();
    }
}
