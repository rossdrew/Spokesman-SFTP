package com.himex.s3;

import com.himex.user.AmazonProperties;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.util.HashMap;
import java.util.Map;

/**
 * A FileSystemFactory for creating Amazon S3 Filesystem
 *
 * @Author Ross W. Drew
 */
@Component
public class S3FileSystemFactory implements FileSystemFactory {
    private URI uri = URI.create("s3:///s3.amazonaws.com");

    //XXX Without caching this I get FileSystemAlreadyExistsException
    private FileSystem s3FileSystem = null;
    private S3FileSystemProviderPlus provider;

    private AmazonProperties amazonProperties;

    @Autowired
    public S3FileSystemFactory(AmazonProperties amazonProperties) {
        this.amazonProperties = amazonProperties;
    }

    public FileSystem createFileSystem(Session session) throws IOException {
        if (provider == null){
            provider = new S3FileSystemProviderPlus();
        }

        if (s3FileSystem == null) {
            HashMap<String, Object> env = new HashMap<>();
            env.put(S3FileSystemProviderPlus.PROP_USERNAME, session.getUsername());

            Map<String, AmazonProperties.UserConfig> users = amazonProperties.getUsers();
            AmazonProperties.UserConfig userConfig = users.get(env.get(S3FileSystemProviderPlus.PROP_USERNAME));
            env.put(S3FileSystemProviderPlus.PROP_USERHOME, userConfig.getHome());

            s3FileSystem = provider.newFileSystem(uri, env);
        }

        return s3FileSystem;
    }
}
