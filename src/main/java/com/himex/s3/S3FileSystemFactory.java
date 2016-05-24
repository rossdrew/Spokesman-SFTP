package com.himex.s3;

import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.session.Session;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.HashMap;

/**
 * A FileSystemFactory for creating Amazon S3 Filesystem
 *
 * @Author Ross W. Drew
 */
public class S3FileSystemFactory implements FileSystemFactory {
    private URI uri = URI.create("localhost");

    private FileSystem s3FileSystem = null;

    public S3FileSystemFactory(URI uri){
        this.uri = uri;
    }

    public FileSystem createFileSystem(Session session) throws IOException {
        if (s3FileSystem == null) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            s3FileSystem = FileSystems.newFileSystem(uri, new HashMap<String, Object>(), classLoader);
        }

        return s3FileSystem;
    }
}
