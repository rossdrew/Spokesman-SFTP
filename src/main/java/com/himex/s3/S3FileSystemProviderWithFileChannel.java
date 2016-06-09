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
 * Overriding S3FileSystemProvider to provide a FileChannel option (via newFileChannel())
 * and adding permissions attribute required by Apache MINA SSHD
 *
 * @Author Ross W. Drew
 */
public class S3FileSystemProviderWithFileChannel extends S3FileSystemProvider {
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
     * Private in original library so needs to be duplicated
     */
    private S3Path toS3Path(Path path) {
        Preconditions.checkArgument(path instanceof S3Path, "path must be an instance of %s", S3Path.class.getName());
        return (S3Path) path;
    }

    /**
     * Overriden to provide 'permissions attribute
     */
    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        Map<String, Object> attributeMap = super.readAttributes(path, attributes, options);

        if (attributeMap != null) {
            //XXX Hack as I have no idea how to get Amazon permissions as S3ObjectSummary (Amazon AWS SDK) doesn't contain any
            attributeMap.put("permissions", PosixFilePermissions.fromString("rw-rw----"));
        }

        return attributeMap;
    }

    /**
     * Overriden to provide S3UserAwareFileSystem rather than S3FileSystem
     */
    protected S3FileSystem createFileSystem(URI uri, Properties props) {
        return new S3UserAwareFileSystem(this, getFileSystemKey(uri, props), getAmazonS3(uri, props), uri.getHost());
    }
}
