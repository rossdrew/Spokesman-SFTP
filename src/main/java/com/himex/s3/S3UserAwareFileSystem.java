package com.himex.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.google.common.collect.ImmutableList;
import com.upplication.s3fs.S3FileStore;
import com.upplication.s3fs.S3FileSystem;
import com.upplication.s3fs.S3FileSystemProvider;

import java.nio.file.FileStore;

/**
 * @Author rossdrew
 * @Created 09/06/16.
 */
public class S3UserAwareFileSystem extends S3FileSystem {
    private AmazonS3 client;

    public S3UserAwareFileSystem(S3FileSystemProvider provider, String key, AmazonS3 client, String endpoint) {
        super(provider, key, client, endpoint);
        this.client = client;
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        ImmutableList.Builder<FileStore> builder = ImmutableList.builder();
//        for (Bucket bucket : client.listBuckets()) {
//            builder.add(new S3FileStore(this, bucket.getName()));
//        }
        builder.add(new S3FileStore(this, "hubio-ubi-ftp"));
        return builder.build();
    }
}
