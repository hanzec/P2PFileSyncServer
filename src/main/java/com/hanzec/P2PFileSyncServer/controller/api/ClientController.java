package com.hanzec.P2PFileSyncServer.controller.api;

import com.hanzec.P2PFileSyncServer.model.data.manage.account.UserAccount;
import com.hanzec.P2PFileSyncServer.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/client")
@Api(tags = "RestAPI Related to manage connected client")
public class ClientController {

    private final AccountService accountService;

    public ClientController(AccountService accountService){
        this.accountService = accountService;
    }

    @ApiOperation("Activate Registered Client")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{clientID}/enable")
    @PreAuthorize("hasAuthority('enable_client')")
    public void enableClient(@AuthenticationPrincipal UserDetails principal, @PathVariable String clientID, @RequestParam String timestamp, @RequestParam String sig){
        accountService.enableClient(clientID,  principal.getUsername());
    }
}
