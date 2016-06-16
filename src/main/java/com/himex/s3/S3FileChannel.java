package com.himex.s3;

import com.upplication.s3fs.S3Path;
import com.upplication.s3fs.S3SeekableByteChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.*;
import java.nio.file.OpenOption;
import java.util.Set;

/**
 * A
 * {@link java.nio.channels.FileChannel FileChannel}
 * that simply directs calls to
 * {@link com.upplication.s3fs.S3SeekableByteChannel S3SeekablBytechannel}
 * so that Apache MINA can work with s3fs
 *
 * @Author Ross W. Drew
 */
public class S3FileChannel extends FileChannel {
    private SeekableByteChannel byteChannel;

    public S3FileChannel(S3SeekableByteChannel byteChannel){
        this.byteChannel = byteChannel;
    }

    public S3FileChannel(SeekableByteChannel byteChannel){
        this.byteChannel = byteChannel;
    }

    public S3FileChannel(S3Path path, Set<? extends OpenOption> options) throws IOException {
        this.byteChannel = new S3SeekableByteChannel(path, options);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        return byteChannel.read(dst);
    }

    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
        return read(dsts); //XXX
    }

    @Override
    public int read(ByteBuffer dst, long position) throws IOException {
        return read(dst); //XXX
    }

    @Override
    public int write(ByteBuffer src, long position) throws IOException {
        return byteChannel.write(src);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return byteChannel.write(src);
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
        return byteChannel.write(srcs[0]);
    }

    @Override
    public long position() throws IOException {
        return byteChannel.position();
    }

    @Override
    public FileChannel position(long newPosition) throws IOException {
        return null;//XXX
    }

    @Override
    public long size() throws IOException {
        return byteChannel.size();
    }

    @Override
    public FileChannel truncate(long size) throws IOException {
        return new S3FileChannel(byteChannel.truncate(size));
    }

    @Override
    public void force(boolean metaData) throws IOException {
        //XXX
    }

    @Override
    public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
        return 0;//XXX
    }

    @Override
    public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {
        return 0;//XXX
    }

    @Override
    public MappedByteBuffer map(MapMode mode, long position, long size) throws IOException {
        return null;
    }

    @Override
    public FileLock lock(long position, long size, boolean shared) throws IOException {
        return null;
    }

    @Override
    public FileLock tryLock(long position, long size, boolean shared) throws IOException {
        return null;
    }

    @Override
    protected void implCloseChannel() throws IOException {
        byteChannel.close();
    }
}
