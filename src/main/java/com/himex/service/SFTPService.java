package com.himex.service;

import com.himex.s3.S3FileSystemFactory;
import org.apache.sshd.common.Factory;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.SecurityUtils;
import org.apache.sshd.common.util.ValidateUtils;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.pubkey.AcceptAllPublickeyAuthenticator;
import org.apache.sshd.server.forward.AcceptAllForwardingFilter;
import org.apache.sshd.server.keyprovider.AbstractGeneratorHostKeyProvider;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.scp.ScpCommandFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.*;

/**
 * Created by rossdrew on 28/04/16.
 */
@Service("sftpService")
public class SFTPService {
    static final private Logger LOG = LoggerFactory.getLogger(SFTPService.class);

    private SshServer sshd = null;

    public static SshServer createSSHServer() throws IOException {
        LOG.info ("Creating SFTP Server...");
        Map<String, String> options = new LinkedHashMap<String, String>();
        String hostKeyType = AbstractGeneratorHostKeyProvider.DEFAULT_ALGORITHM;

        SshServer sshd = SshServer.setUpDefaultServer();
        Map<String, Object> props = sshd.getProperties();
        props.putAll(options);

        sshd.setPort(21000);
        sshd.setKeyPairProvider(buildHostKeyProviderFromFile(hostKeyType));
        sshd.setShellFactory(createShellFactory());
        sshd.setPasswordAuthenticator(createPasswordAuthenticator());
        sshd.setPublickeyAuthenticator(AcceptAllPublickeyAuthenticator.INSTANCE);
        sshd.setTcpipForwardingFilter(AcceptAllForwardingFilter.INSTANCE);
        sshd.setCommandFactory(createCommandFactory());
        sshd.setSubsystemFactories(createSubsystemFactories());
        sshd.setFileSystemFactory(createFileSystemFactory());

        sshd.start();
        return sshd;
    }

    private static FileSystemFactory createFileSystemFactory() {
        //TODO needs to be the custom Amazon S3 FileSystem factory
        URI uri = URI.create("s3:///myuri");
        FileSystemFactory s3FileSystemFactory = new S3FileSystemFactory(uri);

        FileSystemFactory localFileSystemFactory =  new VirtualFileSystemFactory(new File(".").toPath());

        return s3FileSystemFactory;
    }

    private static Factory<Command> createShellFactory() {
        return new ProcessShellFactory(new String[] { "/bin/sh", "-i", "-l" });
        //  return InteractiveProcessShellFactory.INSTANCE;
    }

    private static List<NamedFactory<Command>> createSubsystemFactories() {
        List<NamedFactory<Command>> subsystemFactories = new ArrayList<NamedFactory<Command>>(1);
        subsystemFactories.add(new SftpSubsystemFactory());
        return subsystemFactories;
    }

    private static CommandFactory createCommandFactory() {
        CommandFactory newCommandFactory = new CommandFactory() {
            public Command createCommand(String command) {
                LOG.debug("Received command: " + command);
                return new ProcessShellFactory(GenericUtils.split(command, ' ')).create();
            }
        };

        return new ScpCommandFactory.Builder().withDelegate(newCommandFactory).build();
    }

    /**
     * XXX FOR DEBUG ONLY : Just makes sure the password is the same as the username
     */
    private static PasswordAuthenticator createPasswordAuthenticator() {
        return new PasswordAuthenticator() {
            public boolean authenticate(String username, String password, ServerSession session) {
                return (GenericUtils.length(username) > 0) && username.equals(password);
            }
        };
    }

    private static AbstractGeneratorHostKeyProvider buildHostKeyProviderFromFile(String hostKeyType) throws IOException {
        AbstractGeneratorHostKeyProvider hostKeyProvider;
        Path hostKeyFile;

        if (SecurityUtils.isBouncyCastleRegistered()) {//requires Bouncycastle dependancies
            hostKeyFile = new File("key.pem").toPath();
            hostKeyProvider = SecurityUtils.createGeneratorHostKeyProvider(hostKeyFile);
        } else {
            hostKeyFile = new File("key.ser").toPath();
            hostKeyProvider = new SimpleGeneratorHostKeyProvider(hostKeyFile);
        }
        hostKeyProvider.setAlgorithm(hostKeyType);

        List<KeyPair> keys = ValidateUtils.checkNotNullAndNotEmpty(hostKeyProvider.loadKeys(), "Failed to load keys from %s", hostKeyFile); //sshd 1.2
        KeyPair kp = keys.get(0);
        PublicKey pubKey = kp.getPublic();
        String keyAlgorithm = pubKey.getAlgorithm();
        // force re-generation of host key if not same algorithm
        if (!Objects.equals(keyAlgorithm, hostKeyProvider.getAlgorithm())) {
            Files.deleteIfExists(hostKeyFile);
            hostKeyProvider.clearLoadedKeys(); //sshd 1.2
        }
        return hostKeyProvider;
    }

    public SFTPService(){
        LOG.debug ("** DEBUG ENABLED **");
        LOG.trace ("** TRACE ENABLED **");

        if (sshd != null)
            return;

        try {
            sshd = createSSHServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (sshd != null && sshd.isOpen()) {//HACKY MAKE BETTER
            try {
                Thread.sleep(1000);
                /*DEBUG*/System.out.print(".");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
