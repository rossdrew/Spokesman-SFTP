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
    private SftpConfig sftpConfig;

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

    public static class SftpConfig{
        private Integer port;
        private String publicKeyFile;

        public Integer getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = Integer.parseInt(port);
        }

        public String getPublicKeyFile() {
            return publicKeyFile;
        }

        public void setPublicKeyFile(String publicKeyFile) {
            this.publicKeyFile = publicKeyFile;
        }
    }

    public URI getAmazonURI() {
        return amazonURI;
    }

    public void setAmazonURI(String amazonURI) {
        this.amazonURI = URI.create(amazonURI);
    }

    public void setAmazonURI(URI amazonURI) {
        this.amazonURI = amazonURI;
    }

    public SftpConfig getSftpConfig() {
        return sftpConfig;
    }

    public void setSftpConfig(SftpConfig sftpConfig) {
        this.sftpConfig = sftpConfig;
    }

    public void setUsers(Map<String, UserConfig> users) {
        this.users = users;
    }

    public Map<String, UserConfig> getUsers() {
        return users;
    }
}
