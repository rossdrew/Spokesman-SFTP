package com.himex;

import com.himex.s3.S3FileSystemFactory;
import com.upplication.s3fs.S3Iterator;
import com.upplication.s3fs.S3Path;
import org.apache.sshd.common.file.FileSystemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Arrays;

/**
 * Entry point and properties source for Spokesman services
 *
 * --> SFTP ---> S3
 *
 * @Author Ross W. Drew
 */
@SpringBootApplication
public class Spokesman {
    static final private Logger LOG = LoggerFactory.getLogger(Spokesman.class);

    private String sftpPublicKeyFile = null;
    private Integer sftpPort = null;

    public Integer getSftpPort() {
        if (sftpPort == null){
            String tmp = System.getProperty("sftp.port");
            sftpPort = Integer.parseInt(tmp);
        }

        return sftpPort;
    }

    public String getSftpPublicKeyFile() {
        if (sftpPublicKeyFile == null){
            sftpPublicKeyFile = System.getProperty("sftp.pubkey.file");
        }

        return sftpPublicKeyFile;
    }

    public void markForRefresh(){
        sftpPublicKeyFile = null;
        sftpPort = null;
    }

    public static void main(String[] args) {
        LOG.debug("Starting Spokesman via Spring Boot...");

        //ApplicationContext ctx = SpringApplication.run(Spokesman.class, args);
        SpokesmanTest.testS3fs();
    }
}
