package com.himex.s3;

import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.session.Session;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.util.HashMap;

/**
 * A FileSystemFactory for creating Amazon S3 Filesystem
 *
 * @Author Ross W. Drew
 */
public class S3FileSystemFactory implements FileSystemFactory {
    private URI uri = URI.create("localhost");

    //XXX Without caching this I get FileSystemAlreadyExistsException
    private FileSystem s3FileSystem = null;
    private S3FileSystemProviderPlus provider;

    public S3FileSystemFactory(URI uri){
        this.uri = uri;
    }

    public FileSystem createFileSystem(Session session) throws IOException {
        if (provider == null){
            provider = new S3FileSystemProviderPlus();
        }

        if (s3FileSystem == null) {
            s3FileSystem = provider.newFileSystem(uri, new HashMap<>());
        }

        return s3FileSystem;
    }
}
