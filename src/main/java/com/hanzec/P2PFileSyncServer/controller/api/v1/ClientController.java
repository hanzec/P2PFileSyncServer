package com.hanzec.P2PFileSyncServer.controller.api.v1;

import com.hanzec.P2PFileSyncServer.model.api.Response;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.ClientAccount;
import com.hanzec.P2PFileSyncServer.model.exception.certificate.CertificateGenerateException;
import com.hanzec.P2PFileSyncServer.service.AccountService;
import com.hanzec.P2PFileSyncServer.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@RestController
@RequestMapping("/api/v1/client")
@Tag(name = "RestAPI Related to manage connected client")
public class ClientController {

    private final AccountService accountService;
    private final CertificateService certificateService;

    public ClientController(AccountService accountService, CertificateService certificateService){
        this.accountService = accountService;
        this.certificateService = certificateService;
    }

    @Operation(summary = "Activate Registered Client")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{clientID}/enable")
    @PreAuthorize("hasAuthority('enable_client')")
    public void enableClient(@AuthenticationPrincipal UserDetails principal, @PathVariable String clientID, @RequestParam String timestamp, @RequestParam String sig){
        accountService.enableClient(clientID,  principal.getUsername());
    }


    @Operation(summary = "Request current client information")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "")
    @PreAuthorize("hasAuthority('client_operation')")
    public Response getClientInformation(@AuthenticationPrincipal UserDetails principal){
        var account = (ClientAccount) principal;
        return new Response()
                .addResponse("client_id", account.getId())
                .addResponse("machine_id", account.getMachineID())
                .addResponse("ip_address", account.getIpAddress())
                .addResponse("register_by", account.getRegister().getId());
    }


    @Operation(summary = "get the registered devices by registration device")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/peer")
    @PreAuthorize("hasAuthority('client_operation')")
    public Response getClientPeers(@AuthenticationPrincipal UserDetails principal){
        var account = (ClientAccount) principal;
        return new Response()
                .addResponse("peers", accountService.getClientPeer(account));
    }

    @Operation(summary = "get the group information of current device")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/group")
    @PreAuthorize("hasAuthority('client_operation')")
    public Response getClientGroup(@AuthenticationPrincipal UserDetails principal){
        return new Response()
                .addResponse("group",((ClientAccount) principal).getGroup());
    }


    @Operation(summary = "Request client certificate")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/certificate")
    @PreAuthorize("hasAuthority('client_operation')")
    public Response getClientCertificate(@AuthenticationPrincipal UserDetails principal) throws CertificateGenerateException, IOException {
        PKCS12PfxPdu newCertificate = certificateService.generateNewClientCertificate((ClientAccount) principal);
        OutputStream stream = new FileOutputStream("./client.p12");
        stream.write(newCertificate.getEncoded(ASN1Encoding.DL));
        stream.close();
        return new Response()
                .addResponse("client_id", ((ClientAccount) principal).getId())
                .addResponse("PSCK12_certificate", Base64.toBase64String(newCertificate.getEncoded(ASN1Encoding.DL)));
    }
}
