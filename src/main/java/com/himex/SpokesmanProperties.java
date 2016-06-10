package com.himex;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties
public class SpokesmanProperties {
    private Map<String, UserConfig> users = new HashMap<>();
    private URI amazonURI;

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

    public URI getAmazonURI() {
        return amazonURI;
    }

    public void setAmazonURI(String amazonURI) {
        this.amazonURI = URI.create(amazonURI);
    }
}
