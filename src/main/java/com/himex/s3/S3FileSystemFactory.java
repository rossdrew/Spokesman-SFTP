package com.himex.s3;

import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.session.Session;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.HashMap;

public class S3FileSystemFactory implements FileSystemFactory {
    private URI uri = URI.create("localhost");

    public S3FileSystemFactory(URI uri){
        this.uri = uri;
    }

    public FileSystem createFileSystem(Session session) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        FileSystem s3FileSystem = FileSystems.newFileSystem(uri, new HashMap<String,Object>(), classLoader);
        return s3FileSystem;
    }
}
