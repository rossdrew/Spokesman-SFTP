package com.himex.service;

import com.himex.Spokesman;
import com.himex.auth.SFTPPublicKeyAuthenticator;
import com.himex.s3.S3FileSystemFactory;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.SecurityUtils;
import org.apache.sshd.common.util.ValidateUtils;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.keyprovider.AbstractGeneratorHostKeyProvider;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * An SFTP service which provides a {@link java.nio.file.FileSystem FileSystem}
 *
 * @Author Ross W. Drew
 */
@Service("sftpService")
public class SFTPService implements SpokesmanService {
    static final private Logger LOG = LoggerFactory.getLogger(SFTPService.class);

    private Spokesman spokesman;
    private SshServer sshd = null;

    public static SshServer createSSHServer(Spokesman spokesman) throws IOException {
        Map<String, String> options = new LinkedHashMap<String, String>();
        String hostKeyType = AbstractGeneratorHostKeyProvider.DEFAULT_ALGORITHM;

        SshServer sshd = SshServer.setUpDefaultServer();
        Map<String, Object> props = sshd.getProperties();
        props.putAll(options);

        Integer port = spokesman.getSftpPort();
        sshd.setPort(port);
        sshd.setKeyPairProvider(buildHostKeyProviderFromFile(hostKeyType, spokesman));
        sshd.setPasswordAuthenticator(createPasswordAuthenticator());//Why does it need one of these is if has public key auth?
        sshd.setPublickeyAuthenticator(new SFTPPublicKeyAuthenticator());

        sshd.setSubsystemFactories(createSubsystemFactories());
        sshd.setFileSystemFactory(createFileSystemFactory());

        sshd.start();
        LOG.info ("Started SFTP Server!");
        return sshd;
    }

    private static FileSystemFactory createFileSystemFactory() {
        //XXX speed up by putting in the correct region
        //XXX always defaults to ImageTransfer bucket even with url = hubio-ubi-ftp.s3.amazonaws.com
        URI uri = URI.create("s3:///s3.amazonaws.com");

        FileSystemFactory s3FileSystemFactory = new S3FileSystemFactory(uri);

        //FileSystemFactory localFileSystemFactory =  new VirtualFileSystemFactory(new File(".").toPath());

        return s3FileSystemFactory;
    }

    private static List<NamedFactory<Command>> createSubsystemFactories() {
        List<NamedFactory<Command>> subsystemFactories = new ArrayList<NamedFactory<Command>>(1);
        SftpSubsystemFactory factory = new SftpSubsystemFactory();
        //factory.addSftpEventListener(new STFPWatcher());

        subsystemFactories.add(factory);
        return subsystemFactories;
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

    private static AbstractGeneratorHostKeyProvider buildHostKeyProviderFromFile(String hostKeyType, Spokesman properties) throws IOException {
        AbstractGeneratorHostKeyProvider hostKeyProvider;
        Path hostKeyFile;

        if (SecurityUtils.isBouncyCastleRegistered()) {//requires Bouncycastle dependancies
            String publicKeyFile = properties.getSftpPublicKeyFile();
            hostKeyFile = new File(publicKeyFile).toPath();
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

    @Autowired
    public SFTPService(Spokesman spokesman){
        this.spokesman = spokesman;

        if (sshd != null)
            return;

        try {
            sshd = createSSHServer(this.spokesman);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (sshd != null && sshd.isOpen()) {//XXX HACKY MAKE BETTER
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getStatus() {
        boolean isRunning = sshd != null && sshd.isOpen();
        return "SFTP Server :" + (isRunning ? "" : " Not") + " Running";
    }
}
