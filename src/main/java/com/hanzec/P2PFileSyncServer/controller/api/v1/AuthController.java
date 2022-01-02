package com.hanzec.P2PFileSyncServer.controller.api.v1;

import com.hanzec.P2PFileSyncServer.model.api.RegisterClientRequest;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.ClientAccount;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.UserAccount;
import com.hanzec.P2PFileSyncServer.model.api.RegisterUserRequest;
import com.hanzec.P2PFileSyncServer.model.api.Response;
import com.hanzec.P2PFileSyncServer.model.exception.certificate.CertificateGenerateException;
import com.hanzec.P2PFileSyncServer.service.CertificateService;
import com.nimbusds.jose.JOSEException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import com.hanzec.P2PFileSyncServer.service.AccountService;
import com.hanzec.P2PFileSyncServer.service.TokenService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "RestAPI Related Registration")
public class AuthController {
    private final TokenService tokenService;
    private final AccountService accountService;
    private final CertificateService certificateService;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AuthController(TokenService tokenService,
                          AccountService accountService,
                          CertificateService certificateService) {
        this.tokenService = tokenService;
        this.accountService = accountService;
        this.certificateService = certificateService;
    }

    @ResponseBody
    @PostMapping(value = "/register_client")
    @Operation(summary = "Used for register new account")
    @ResponseStatus(HttpStatus.CREATED)
    public Response register_client(@RequestBody @Validated RegisterClientRequest client) throws CertificateGenerateException, IOException, JOSEException {
        //Trying to Register new Account to Server
        Pair<ClientAccount, Integer> newClient = accountService.createNewClient(client);

        // generate client active link
        String path = "/api/v1/client/" + newClient.getFirst().getId() + "/enable?timestamp=" + System.currentTimeMillis() / 1000L;
        String sig = certificateService.signUrl(path);

        return new Response()
                .addResponse("client_id", newClient.getFirst().getId())
                .addResponse("enable_url", path + "&sig=" + sig)
                .addResponse("register_code", newClient.getSecond())
                .addResponse("login_token", accountService.generateClientToken(newClient.getFirst()));
    }

    @ResponseBody
    @PostMapping(value = "/register_account")
    @Operation(summary = "Used for register new account")
    @ResponseStatus(HttpStatus.CREATED)
    public Response register_account(@RequestBody @Validated RegisterUserRequest user) {
        //Trying to Register new Account to Server
        UserAccount newUser = accountService.createUser(user);
        return new Response().addResponse("user_id", newUser.getId());
    }
}
