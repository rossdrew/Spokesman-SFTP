package com.himex.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.google.common.collect.ImmutableList;
import com.himex.Spokesman;
import com.upplication.s3fs.S3FileStore;
import com.upplication.s3fs.S3FileSystem;
import com.upplication.s3fs.S3FileSystemProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.FileStore;
import java.util.List;

/**
 * @Author rossdrew
 * @Created 09/06/16.
 */
public class S3FileSystemPlus extends S3FileSystem {
    private AmazonS3 client;
    private String username = "unknown";

    public S3FileSystemPlus(S3FileSystemProvider provider, String key, AmazonS3 client, String endpoint, String username) {
        super(provider, key, client, endpoint);
        this.client = client;
        this.username = username;
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        ImmutableList.Builder<FileStore> builder = ImmutableList.builder();

        List<Bucket> availiableBuckets = client.listBuckets();
        boolean d = listContainsBucketName(availiableBuckets, "hubio-ubi-ftp");

        //TODO get a list of users and the buckets they can access

        builder.add(new S3FileStore(this, "hubio-ubi-ftp"));
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
