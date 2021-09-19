package com.hanzec.P2PFileSyncServer.controller.api;

import com.hanzec.P2PFileSyncServer.model.data.manage.User;
import com.hanzec.P2PFileSyncServer.service.AccountService;
import com.hanzec.P2PFileSyncServer.service.TokenService;
import com.hanzec.P2PFileSyncServer.model.exception.auth.PasswordNotMatchException;
import com.hanzec.P2PFileSyncServer.model.api.ChangePasswordRequest;
import com.hanzec.P2PFileSyncServer.model.api.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/user")
@Api(tags = "RestAPI Related user Account")
public class UserController {

    final AccountService accountService;

    public UserController(AccountService accountService,
                          TokenService tokenService) {
        this.accountService = accountService;
    }

    @ResponseBody
    @GetMapping(value = "/")
    @ApiOperation("Get user information")
    @PreAuthorize("hasAuthority('user_details')")
    public Response getUserInformation(Principal principal){
        User user = (User) accountService .loadUserByUsername(principal.getName());

        return new Response()
                .addResponse("email", user.getEmail())
                .addResponse("username", user.getUsername())
                .addResponse("lastName", user.getLastName())
                .addResponse("firstName", user.getFirstName());
    }

    @ResponseBody
    @PostMapping(value = "/password")
    @ApiOperation("Reset password aip")
    @PreAuthorize("hasAuthority('modify_password')")
    public Response resetPassword(
            Principal principal,
            @RequestBody @Validated ChangePasswordRequest changePasswordRequest) throws PasswordNotMatchException {

        accountService.resetPassword(
                changePasswordRequest.getNewPassword(),
                changePasswordRequest.getOldPassword(),
                principal);

        return new Response();
    }
}