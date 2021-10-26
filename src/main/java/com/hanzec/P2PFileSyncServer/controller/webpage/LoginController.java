package com.hanzec.P2PFileSyncServer.controller.webpage;

import com.hanzec.P2PFileSyncServer.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

    @RequestMapping(value = "/loginSuccess",method = RequestMethod.GET)
    public String loginSuccess() throws IOException {
        return "login";
    }
}
