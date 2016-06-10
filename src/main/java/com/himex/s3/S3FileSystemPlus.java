package com.himex.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.google.common.collect.ImmutableList;
import com.upplication.s3fs.S3FileStore;
import com.upplication.s3fs.S3FileSystem;
import com.upplication.s3fs.S3FileSystemProvider;

import java.nio.file.FileStore;
import java.util.List;

/**
 * @Author rossdrew
 * @Created 09/06/16.
 */
public class S3FileSystemPlus extends S3FileSystem {
    private AmazonS3 client;
    private String homeDirectory;

    public S3FileSystemPlus(S3FileSystemProvider provider, String key, AmazonS3 client, String endpoint, String userhome) {
        super(provider, key, client, endpoint);
        this.client = client;
        this.homeDirectory = userhome;
    }

    /**
     * Overidden so that user can only view certain directories, this would otherwise return a full list of buckets.
     * MINA would then select the first of these, which there is likely no permission for, causing an error
     */
    @Override
    public Iterable<FileStore> getFileStores() {
        ImmutableList.Builder<FileStore> builder = ImmutableList.builder();

        List<Bucket> availiableBuckets = client.listBuckets();
        if (listContainsBucketName(availiableBuckets, homeDirectory)){
            builder.add(new S3FileStore(this, homeDirectory));
        }

        return builder.build();
    }

    private boolean listContainsBucketName(List<Bucket> bucketList, String name){
        for (Bucket bucket : bucketList){
            if (bucket.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }
}
