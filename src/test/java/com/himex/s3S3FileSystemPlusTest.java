package com.himex;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.himex.s3.S3FileSystemPlus;
import com.upplication.s3fs.S3FileSystemProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.nio.file.FileStore;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @Author rossdrew
 */
@RunWith(MockitoJUnitRunner.class)
public class s3S3FileSystemPlusTest {
    private S3FileSystemPlus fileSystem;

    @Mock
    private S3FileSystemProvider provider;
    @Mock
    private AmazonS3 client;

    @Before
    public void setUp(){
        fileSystem = new S3FileSystemPlus(provider, "testKey", client, "testEndpoint", "testUserHome");
    }

    @Test
    public void testGetFileStoresWithValidHome(){
        List<Bucket> availBucketList = new ArrayList<>();
        availBucketList.add(new Bucket("invalidHome3"));
        availBucketList.add(new Bucket("invalidHome2"));
        availBucketList.add(new Bucket("testUserHome"));
        availBucketList.add(new Bucket("invalidHome1"));
        when(client.listBuckets()).thenReturn(availBucketList);

        Iterable<FileStore> bucketList = fileSystem.getFileStores();
        List<FileStore> fileStores = new ArrayList<>();

        for (FileStore fs : bucketList){
            fileStores.add(fs);
        }

        assertEquals(1, fileStores.size());
        assertEquals("testUserHome", fileStores.get(0).name());
        assertEquals("S3Bucket", fileStores.get(0).type());
    }

    @Test
    public void testGetFileStoresWithInvalidHome(){
        List<Bucket> availBucketList = new ArrayList<>();
        availBucketList.add(new Bucket("invalidHome3"));
        availBucketList.add(new Bucket("invalidHome2"));
        availBucketList.add(new Bucket("invalidHome1"));
        when(client.listBuckets()).thenReturn(availBucketList);

        Iterable<FileStore> bucketList = fileSystem.getFileStores();
        List<FileStore> fileStores = new ArrayList<>();

        for (FileStore fs : bucketList){
            fileStores.add(fs);
        }

        assertEquals(0, fileStores.size());
    }
}
