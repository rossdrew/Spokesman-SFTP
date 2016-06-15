package com.himex.controller;

import com.himex.service.SFTPService;
import com.himex.service.SpokesmanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Essentially an endpoint that reports the state of running services
 * TODO doesn't work at the moment
 *
 * @Author Ross W. Drew
 */
//@Controller
public class ServiceStatusController {
    private Map<String, SpokesmanService> services = new HashMap<>();

    @Autowired
    public ServiceStatusController(SFTPService sftpService){
        services.put("sftpservice", sftpService);
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public @ResponseBody String health() {
        String servicesStates = "";
        for (String serviceKey : services.keySet()){
            servicesStates += "\n" + serviceKey + " : " + services.get(serviceKey).getStatus();
        }

        return "Services\n--------\n" + servicesStates;
    }
}
