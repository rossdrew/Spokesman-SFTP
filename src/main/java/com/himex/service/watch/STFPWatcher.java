package com.himex.service.watch;

import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.DirectoryHandle;
import org.apache.sshd.server.subsystem.sftp.FileHandle;
import org.apache.sshd.server.subsystem.sftp.Handle;
import org.apache.sshd.server.subsystem.sftp.SftpEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

/**
 * Watcher for Sftp Events from
 * {@link org.apache.sshd.server.subsystem.sftp.SftpSubsystem SftpSubsystem}
 * as a
 * {@link org.apache.sshd.server.subsystem.sftp.SftpEventListener SftpEventListener}
 *
 * @Author rossdrew
 */
public class STFPWatcher implements SftpEventListener {
    static final private Logger LOG = LoggerFactory.getLogger(SftpEventListener.class);

    private void printMessage(String msg){
        LOG.info(msg);
    }

    @Override
    public void initialized(ServerSession session, int version) {
        printMessage("initialized");
    }

    @Override
    public void destroying(ServerSession session) {
        printMessage("destroying");
    }

    @Override
    public void open(ServerSession session, String remoteHandle, Handle localHandle) {
        printMessage("open " + localHandle + " / " + remoteHandle);
    }

    @Override
    public void read(ServerSession session, String remoteHandle, DirectoryHandle localHandle, Map<String, Path> entries) {
        printMessage("read");
    }

    @Override
    public void read(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen, int readLen) {
        printMessage("read");
    }

    @Override
    public void write(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen) {
        printMessage("write");
    }

    @Override
    public void blocking(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, long length, int mask) {
        printMessage("blocking");
    }

    @Override
    public void blocked(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, long length, int mask, Throwable thrown) {
        printMessage("blocked");
    }

    @Override
    public void unblocking(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, long length) {
        printMessage("unblocking");
    }

    @Override
    public void unblocked(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, long length, Boolean result, Throwable thrown) {
        printMessage("unblocked");
    }

    @Override
    public void close(ServerSession session, String remoteHandle, Handle localHandle) {
        printMessage("close " + localHandle + " / " + remoteHandle);
    }

    @Override
    public void creating(ServerSession session, Path path, Map<String, ?> attrs) {
        printMessage("creating");
    }

    @Override
    public void created(ServerSession session, Path path, Map<String, ?> attrs, Throwable thrown) {
        printMessage("created");
    }

    @Override
    public void moving(ServerSession session, Path srcPath, Path dstPath, Collection<CopyOption> opts) {
        printMessage("moving");
    }

    @Override
    public void moved(ServerSession session, Path srcPath, Path dstPath, Collection<CopyOption> opts, Throwable thrown) {
        printMessage("moved");
    }

    @Override
    public void removing(ServerSession session, Path path) {
        printMessage("removing");
    }

    @Override
    public void removed(ServerSession session, Path path, Throwable thrown) {
        printMessage("removed");
    }

    @Override
    public void linking(ServerSession session, Path source, Path target, boolean symLink) {
        printMessage("linking");
    }

    @Override
    public void linked(ServerSession session, Path source, Path target, boolean symLink, Throwable thrown) {
        printMessage("linked");
    }

    @Override
    public void modifyingAttributes(ServerSession session, Path path, Map<String, ?> attrs) {
        printMessage("modifyingAttributes");
    }

    @Override
    public void modifiedAttributes(ServerSession session, Path path, Map<String, ?> attrs, Throwable thrown) {
        printMessage("modifyingAttributes");
    }
}
