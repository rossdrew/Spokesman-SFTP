package com.himex;

import com.himex.s3.S3FileSystemFactory;
import com.upplication.s3fs.S3Iterator;
import com.upplication.s3fs.S3Path;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @Author Ross W. Drew
 */
@SpringBootApplication
public class Spokesman {
    static final private Logger LOG = LoggerFactory.getLogger(Spokesman.class);

    public static void main(String[] args) {
        LOG.debug("Starting Spokesman via Spring Boot...");

        ApplicationContext ctx = SpringApplication.run(Spokesman.class, args);
    }

}
