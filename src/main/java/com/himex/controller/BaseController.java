package com.himex.controller;

import org.springframework.web.bind.annotation.*;

/**
 * Created by rossdrew on 28/04/16.
 */
@RestController
public class BaseController {
    @RequestMapping("/")
    public String index() {
        return "SFTP Spokesman running.\n";
    }
}
