package com.hanzec.P2PFileSyncServer.controller.webpage;

import com.hanzec.P2PFileSyncServer.model.api.Response;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.UserAccount;
import com.hanzec.P2PFileSyncServer.service.AccountService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class LoginController {
    private final AccountService accountService;

    LoginController(AccountService accountService){
        this.accountService = accountService;
    }

    @GetMapping(value = "/login")
    public String loginPage() throws IOException {
        return "login";
    }

    @ResponseBody
    @RequestMapping(value = "/loginSuccess",method = RequestMethod.GET)
    public Response loginSuccess(@AuthenticationPrincipal UserDetails principal) throws IOException {
        UserAccount account = (UserAccount) principal;
        return new Response()
                .addResponse("email",account.getEmail())
                .addResponse("user_id",account.getUsername());
    }
}
