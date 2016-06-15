package com.himex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Single point of application properties, spring boot loaded from application.yml
 */
@Configuration
@ConfigurationProperties
public class SpokesmanProperties {
    static final private Logger LOG = LoggerFactory.getLogger(SpokesmanProperties.class);

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
        private File authorizedKeysFile;

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

        public void setAuthorizedKeysFile(String authorizedKeysFileName){
            authorizedKeysFile = new java.io.File(authorizedKeysFileName);
            if (!authorizedKeysFile.exists()){
                LOG.error("Cannot find authorized keys file '" + authorizedKeysFileName + "'");
            }
        }

        public File getAuthorizedKeysFile(){
            return authorizedKeysFile;
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
