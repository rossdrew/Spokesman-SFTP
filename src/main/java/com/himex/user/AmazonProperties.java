package com.himex.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author rossdrew
 * @Created 10/06/16.
 */
@ConfigurationProperties
@Configuration
public class AmazonProperties {
    private Map<String, UserConfig> users = new HashMap<>();

    public static class UserConfig {
        private String home;

        public String getHome() {
            return home;
        }

        public void setHome(String home) {
            this.home = home;
        }

        public UserConfig() {
        }
    }

    public void setUsers(Map<String, UserConfig> users) {
        this.users = users;
    }

    public Map<String, UserConfig> getUsers() {
        return users;
    }
}
