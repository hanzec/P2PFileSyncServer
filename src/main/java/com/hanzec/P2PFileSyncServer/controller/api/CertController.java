package com.hanzec.P2PFileSyncServer.controller.api;

import com.hanzec.P2PFileSyncServer.model.api.LoginRequest;
import com.hanzec.P2PFileSyncServer.model.api.Response;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.UserAccount;
import com.hanzec.P2PFileSyncServer.model.exception.auth.PasswordNotMatchException;
import com.hanzec.P2PFileSyncServer.service.CertificateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;

@RestController
@RequestMapping("/api/v1/cert")
@Api(tags = "RestAPI Related to Certificate Acquire or validation")
public class CertController {
    private final CertificateService certificateService;

    public CertController(CertificateService certificateService){
        this.certificateService = certificateService;
    }

    @ApiOperation("Request Server public certifies")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/server-cert", produces = MediaType.TEXT_PLAIN_VALUE)
    public byte[] RequestCerts() throws CertificateEncodingException, OperatorCreationException, CMSException, IOException {
       return certificateService.getClientSignPublicCertificate().getEncoded();
    }
}
