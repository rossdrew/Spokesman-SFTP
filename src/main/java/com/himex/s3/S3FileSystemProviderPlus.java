package com.himex.s3;

import com.google.common.base.Preconditions;
import com.upplication.s3fs.S3FileSystem;
import com.upplication.s3fs.S3FileSystemProvider;
import com.upplication.s3fs.S3Path;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static com.upplication.s3fs.AmazonS3Factory.ACCESS_KEY;
import static com.upplication.s3fs.AmazonS3Factory.SECRET_KEY;


/**
 * Overiding S3FileSystemProvider to
 * - provide a FileChannel option (via newFileChannel())
 * - add permissions attribute required by Apache MINA SSHD
 * - add passing of custom properties
 *
 * @Author Ross W. Drew
 */
public class S3FileSystemProviderPlus extends S3FileSystemProvider {
    public static final String PROP_USERNAME = "sftpUsername";

    /**
     * Just a FileChannel interface that points to a SeekableByteChannel
     */
    @Override
    public FileChannel newFileChannel(Path path,
                                      Set<? extends OpenOption> options,
                                      FileAttribute<?>... attrs)
            throws IOException
    {
        S3Path s3Path = toS3Path(path);
        return new S3FileChannel(s3Path, options);
    }

    /**
     * Overidden to provide 'permissions attribute
     */
    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        Map<String, Object> attributeMap = super.readAttributes(path, attributes, options);

        if (attributeMap != null) {
            //XXX Hack as I have no idea how to get the equivalent of Amazon permissions as
            //    S3ObjectSummary (Amazon AWS SDK) doesn't contain any and Apche MINA SSHD
            //    requires 'permissions' which are normally specific to POSIX
            attributeMap.put("permissions", PosixFilePermissions.fromString("rw-rw----"));
        }

        return attributeMap;
    }

    /**
     * Overidden to provide S3FileSystemPlus rather than S3FileSystem
     */
    protected S3FileSystem createFileSystem(URI uri, Properties props) {
        return new S3FileSystemPlus(this, getFileSystemKey(uri, props), getAmazonS3(uri, props), uri.getHost(), props.getProperty(PROP_USERNAME));
    }

    /**
     * Overidden just to add env to props
     */
    @Override
    public FileSystem newFileSystem(URI uri, Map<String, ?> env) {
        validateUri(uri);
        // get properties for the env or properties or system
        Properties props = getProperties(uri, env);
        validateProperties(props);
        // try to get the filesystem by the key
        String key = getFileSystemKey(uri, props);
        if (getFilesystems().containsKey(key)) {
            throw new FileSystemAlreadyExistsException("File system " + uri.getScheme() + ':' + key + " already exists");
        }

        props.putAll(env);

        // create the filesystem with the final properties, store and return
        S3FileSystem fileSystem = createFileSystem(uri, props);
        getFilesystems().put(fileSystem.getKey(), fileSystem);
        return fileSystem;
    }

    /**
     * XXX Overidden and copied as it's private and newFileChannel() relies on it
     */
    private S3Path toS3Path(Path path) {
        Preconditions.checkArgument(path instanceof S3Path, "path must be an instance of %s", S3Path.class.getName());
        return (S3Path) path;
    }

    /**
     * XXX Overidden and copied as it's private and newFileSystem() relies on it
     */
    private Properties getProperties(URI uri, Map<String, ?> env) {
        Properties props = loadAmazonProperties();
        // but can be overloaded by envs vars
        overloadProperties(props, env);
        // and access key and secret key can be override
        String userInfo = uri.getUserInfo();
        if (userInfo != null) {
            String[] keys = userInfo.split(":");
            props.setProperty(ACCESS_KEY, keys[0]);
            if (keys.length > 1) {
                props.setProperty(SECRET_KEY, keys[1]);
            }
        }
        return props;
    }

    /**
     * XXX Overidden and copied as it's private and newFileSystem() relies on it
     */
    private void validateProperties(Properties props) {
        Preconditions.checkArgument(
                (props.getProperty(ACCESS_KEY) == null && props.getProperty(SECRET_KEY) == null)
                        || (props.getProperty(ACCESS_KEY) != null && props.getProperty(SECRET_KEY) != null), "%s and %s should both be provided or should both be omitted",
                ACCESS_KEY, SECRET_KEY);
    }
}
