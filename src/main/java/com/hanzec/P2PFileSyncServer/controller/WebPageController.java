package com.hanzec.P2PFileSyncServer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebPageController {

    @RequestMapping("/test")
    String test(String request){
        return request;
    }

    @RequestMapping("/testGet")
    String testGet(){
        return "I am the cySchedule server!";
    }

    @RequestMapping("/teapot")
    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    void teapot(){}

}
