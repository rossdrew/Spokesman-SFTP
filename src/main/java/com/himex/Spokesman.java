package com.himex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Created by rossdrew on 28/04/16.
 *
 * XXX - Logging isn't working?!
 */
@SpringBootApplication
public class Spokesman {
    static final private Logger LOG = LoggerFactory.getLogger(Spokesman.class);

    public static void main(String[] args) {
        LOG.debug("Starting Spokesman via Spring Boot...");
        ApplicationContext ctx = SpringApplication.run(Spokesman.class, args);
    }
}
