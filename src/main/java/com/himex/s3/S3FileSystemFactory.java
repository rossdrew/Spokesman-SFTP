package com.himex.s3;

import com.himex.SpokesmanProperties;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
    private S3FileSystemProviderPlus provider;
    private Map<String, FileSystem> userFileSystems = new HashMap<>();
    
    private SpokesmanProperties spokesmanProperties;

    @Autowired
    public S3FileSystemFactory(SpokesmanProperties spokesmanProperties) {
        this.spokesmanProperties = spokesmanProperties;
    }

    public FileSystem createFileSystem(Session session) throws IOException {
        if (provider == null){
            provider = new S3FileSystemProviderPlus();
        }

        String username = session.getUsername();
        if (!userFileSystems.containsKey(username)){
            HashMap<String, Object> env = new HashMap<>();
            env.put(S3FileSystemProviderPlus.PROP_USERNAME, username);

            Map<String, SpokesmanProperties.UserConfig> users = spokesmanProperties.getUsers();
            SpokesmanProperties.UserConfig userConfig = users.get(env.get(S3FileSystemProviderPlus.PROP_USERNAME));
            env.put(S3FileSystemProviderPlus.PROP_USERHOME, userConfig.getHome());

            userFileSystems.put(username, provider.newFileSystem(spokesmanProperties.getAmazonURI(), env));
        }

        return userFileSystems.get(username);
    }
}
