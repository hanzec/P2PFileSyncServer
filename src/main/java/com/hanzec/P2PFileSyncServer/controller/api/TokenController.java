package com.hanzec.P2PFileSyncServer.controller.api;

import com.hanzec.P2PFileSyncServer.model.api.Response;
import com.hanzec.P2PFileSyncServer.model.data.manage.authenticate.UserToken;
import com.hanzec.P2PFileSyncServer.model.exception.auth.TokenNotFoundException;
import com.hanzec.P2PFileSyncServer.service.TokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/token")
@Api(tags = "RestAPI related user token Managerment")
public class TokenController {
    final TokenService tokenService;
    /*
        todo: refresh/ update/ create
     */

    public TokenController(TokenService tokenService){
        this.tokenService = tokenService;
    }

    @ResponseBody
    @GetMapping(value = "/")
    @ApiOperation("get All existed token")
    @PreAuthorize("hasAuthority('modify_credential')")
    public Response getAllToken(Principal principal){
        Response response = new Response();
        tokenService.getAllTokenBelongToUser(UUID.fromString(principal.getName()))
            .forEach(V -> { response.getResponseBody().put(V.getTokenID().toString(),V);});
        return response;
    }

    @ResponseBody
    @DeleteMapping(value = "/{tokenID}")
    @ApiOperation("delete current login Token")
    @PreAuthorize("hasAuthority('modify_credential')")
    public Response revokeToken(Principal principal,
                                @PathVariable String tokenID) throws TokenNotFoundException {
        Response response = new Response();

        UserToken userToken = tokenService.getTokenObject(UUID.fromString(tokenID),principal.getName());
        if(userToken != null) {
            tokenService.delete(userToken);
            return response;
        }else{
            throw new TokenNotFoundException(tokenID);
        }
    }
    //    @PostMapping(value = "/refresh")
//    @ApiOperation("Used for refresh Token")
//    public Response register(HttpServletRequest request, @Validated TokenRequest tokenRequest){
//        UserToken userToken = userTokenService.getTokenObject(tokenRequest.getTokenID());
//
//        if(userToken.)
//        //Trying to Register new Account to Server
//        accountService.createUser(user);
//
//        logger.debug("User [ " + user.getEmail() + " ] is success registered");
//
//        return new Response().send(request.getRequestURI()).Created();
//    }
}
