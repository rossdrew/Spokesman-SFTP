package com.himex.controller;

import com.himex.service.SFTPService;
import com.himex.service.SpokesmanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Essentially a health endpoint...TODO that doesn't work at the moment
 *
 * @Author Ross W. Drew
 */
@RestController
@RequestMapping("/")
public class BaseController {
    private Map<String, SpokesmanService> services = new HashMap<>();

    @Autowired
    public BaseController(SFTPService sftpService){
        services.put("sftpservice", sftpService);
    }

    @RequestMapping(value = "/health", method = RequestMethod.GET)
    public String health() {
        String servicesStates = "";
        for (String serviceKey : services.keySet()){
            servicesStates += "" + serviceKey + " : " + services.get(serviceKey).getStatus();
        }

        return "SFTP Spokesman running.\n";
    }
}
