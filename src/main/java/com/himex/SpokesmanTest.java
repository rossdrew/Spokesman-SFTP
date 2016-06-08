package com.himex;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.himex.s3.S3FileSystemFactory;
import com.upplication.s3fs.S3Iterator;
import com.upplication.s3fs.S3Path;
import org.apache.sshd.common.file.FileSystemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Arrays;

/**
 * @Author rossdrew
 * @Created 08/06/16.
 */
public class SpokesmanTest {
    static final private Logger LOG = LoggerFactory.getLogger(SpokesmanTest.class);

    public static void testS3fs(){
        URI uri = URI.create("s3:///s3.amazonaws.com");

        try {
            FileSystemFactory s3FileSystemFactory = new S3FileSystemFactory(uri);
            FileSystem s3FileSystem = s3FileSystemFactory.createFileSystem(null);

            printFileStores(s3FileSystem);
            printPathContents(s3FileSystem, "hubio-ubi-ftp");
            printPathContents(s3FileSystem, "ImageTransfer");
        } catch (AmazonS3Exception e2) {
            LOG.error("Cannot access Amazone object: " + e2.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printFileStores(FileSystem s3FileSystem) throws IOException {
        String fileStores = "";
        for (FileStore f : s3FileSystem.getFileStores()){
            fileStores += ("\n - " + f.name() + " (" + f.type() + ")\t\t" + f.getTotalSpace());
        }
            /*DEBUG*/
        System.out.println("## File Stores [" + Arrays.toString(s3FileSystem.supportedFileAttributeViews().toArray()) + "]");
            /*DEBUG*/
        System.out.println(fileStores);
            /*DEBUG*/
        System.out.println("File Stores ##");
    }

    private static void printPathContents(FileSystem s3FileSystem, String bucket) {
        Path p = s3FileSystem.getPath("/" + bucket);

        LOG.debug("Retrieving contents of " + p.toString() + "...");

        String contents = "";
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(p, "*")) {
            for (Path file : stream) {
                contents += "\n \t - " + file.getFileName();
            }
        } catch (IOException | DirectoryIteratorException x) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            System.err.println(x);
        }
            /*DEBUG*/
        System.out.println("Listing contents of " + p.getFileName().toString() + " newDirectoryStream() ############################## " + contents + "\n##");

        //Test S3Iterator directly
//        S3Path pathAsS3Path = (S3Path)p;
//        S3Iterator s3Iterator = new S3Iterator(pathAsS3Path);
//
//        /*DEBUG*/System.out.println("Listing contents with S3Iterator ##############################");
//        while (s3Iterator.hasNext()){
//                /*DEBUG*/System.out.println(s3Iterator.next());
//            //This works, so why cant SFTP clients see contents?
//        }
        /********************/
    }
}
