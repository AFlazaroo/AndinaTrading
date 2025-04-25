package com.edu.unbosque.controller;


import com.edu.unbosque.model.SMSSendRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@Slf4j
public class NotificacionController {
    @PostMapping("/processSMS")
    public String processSMS(@RequestBody SMSSendRequest sendRequest){
        log.info("processSMS Started SendRequest: ", sendRequest.toString());
        return "todo";

    }
}