package com.hanzec.P2PFileSyncServer.controller.api;

import com.hanzec.P2PFileSyncServer.model.api.RegisterClientRequest;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.ClientAccount;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.UserAccount;
import com.hanzec.P2PFileSyncServer.model.exception.auth.PasswordNotMatchException;
import com.hanzec.P2PFileSyncServer.model.api.LoginRequest;
import com.hanzec.P2PFileSyncServer.model.api.RegisterUserRequest;
import com.hanzec.P2PFileSyncServer.model.api.Response;
import com.hanzec.P2PFileSyncServer.model.exception.certificate.CertificateGenerateException;
import com.hanzec.P2PFileSyncServer.service.CertificateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.util.encoders.Base64;
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
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;

@RestController
@RequestMapping("/api/v1")
@Api(tags = "RestAPI Related Registration")
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
    @ApiOperation("Used for register new account")
    @ResponseStatus(HttpStatus.CREATED)
    public Response register_client(@RequestBody @Validated RegisterClientRequest client) throws CertificateGenerateException, IOException {
        //Trying to Register new Account to Server
        Pair<ClientAccount,Integer> newClient = accountService.createNewClient(client);
        PKCS12PfxPdu newCertificate = certificateService.generateNewClientCertificate(newClient.getFirst());

        // generate client active link
        String path = "/api/v1/client/" + newClient.getFirst().getId() + "/enable?timestamp=" + System.currentTimeMillis() / 1000L;
        String sig = certificateService.signUrl(path);

        return new Response()
                .addResponse("client_id", newClient.getFirst().getId())
                .addResponse("enable_url", path + "&sig=" + sig)
                .addResponse("register_code", newClient.getSecond())
                .addResponse("PSCK12_certificate", Base64.toBase64String(newCertificate.getEncoded()));
    }

    @ResponseBody
    @PostMapping(value = "/register_account")
    @ApiOperation("Used for register new account")
    @ResponseStatus(HttpStatus.CREATED)
    public Response register_account(@RequestBody @Validated RegisterUserRequest user){
        //Trying to Register new Account to Server
        UserAccount newUser = accountService.createUser(user);
        return new Response().addResponse("user_id", newUser.getId());
    }
}
