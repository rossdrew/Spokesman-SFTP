package com.himex.service;

import com.himex.SpokesmanProperties;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.util.SecurityUtils;
import org.apache.sshd.common.util.ValidateUtils;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.UserAuth;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.UserAuthPasswordFactory;
import org.apache.sshd.server.auth.pubkey.UserAuthPublicKey;
import org.apache.sshd.server.auth.pubkey.UserAuthPublicKeyFactory;
import org.apache.sshd.server.config.keys.AuthorizedKeysAuthenticator;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.*;

/**
 * An SFTP service using Apache MINA SSHD which provides a {@link java.nio.file.FileSystem FileSystem}
 *
 * @Author Ross W. Drew
 */
@Service
public class SFTPService implements SpokesmanService {
    static final private Logger LOG = LoggerFactory.getLogger(SFTPService.class);

    private SpokesmanProperties spokesmanProperties;
    private FileSystemFactory s3FileSystemFactory;
    private SshServer sshd = null;

    public SshServer createSSHServer(SpokesmanProperties spokesmanProperties) throws IOException {
        Map<String, String> options = new LinkedHashMap<String, String>();
        String hostKeyType = AbstractGeneratorHostKeyProvider.DEFAULT_ALGORITHM;

        SshServer sshd = SshServer.setUpDefaultServer();
        Map<String, Object> props = sshd.getProperties();
        props.putAll(options);

        Integer port = spokesmanProperties.getSftpConfig().getPort();
        sshd.setPort(port);
        sshd.setKeyPairProvider(buildHostKeyProviderFromFile(hostKeyType, spokesmanProperties));//XXX Why does it need this if I have a publicKeyAuthenticator
        sshd.setPasswordAuthenticator(createPasswordAuthenticator());//XXX Not used if PublickeyAuthenticator is setup

        sshd.setPublickeyAuthenticator(new AuthorizedKeysAuthenticator(spokesmanProperties.getAuthorizedKeysFile()));
        List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<>();
        userAuthFactories.add(UserAuthPublicKeyFactory.INSTANCE); // <<<--- denies all keys?!
        sshd.setUserAuthFactories(userAuthFactories);

        sshd.setSubsystemFactories(createSubsystemFactories());
        sshd.setFileSystemFactory(createFileSystemFactory());

        sshd.start();
        LOG.info ("Started SFTP service!");
        return sshd;
    }

    private FileSystemFactory createFileSystemFactory() {
        return s3FileSystemFactory;
    }

    private List<NamedFactory<Command>> createSubsystemFactories() {
        List<NamedFactory<Command>> subsystemFactories = new ArrayList<NamedFactory<Command>>(1);
        SftpSubsystemFactory factory = new SftpSubsystemFactory();
        //factory.addSftpEventListener(new STFPWatcher());

        subsystemFactories.add(factory);
        return subsystemFactories;
    }

    /**
     * XXX FOR DEBUG ONLY : Just makes sure the password is the same as the username
     */
    private PasswordAuthenticator createPasswordAuthenticator() {
        return new PasswordAuthenticator() {
            public boolean authenticate(String username, String password, ServerSession session) {
                boolean validLogin = true;

                LOG.info((!validLogin ? "Failed l" : "L") + "ogin from " + username + "@" + session.getClientAddress());

                return validLogin;//(GenericUtils.length(username) > 0) && username.equals(password);
            }
        };
    }

    private AbstractGeneratorHostKeyProvider buildHostKeyProviderFromFile(String hostKeyType, SpokesmanProperties properties) throws IOException {
        Path hostKeyFile;
        AbstractGeneratorHostKeyProvider hostKeyProvider;

        if (SecurityUtils.isBouncyCastleRegistered()) {//requires Bouncycastle dependancies
            String publicKeyFile = properties.getSftpConfig().getPublicKeyFile();
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
    public SFTPService(FileSystemFactory s3FileSystemFactory, SpokesmanProperties props){
        this.spokesmanProperties = props;
        this.s3FileSystemFactory = s3FileSystemFactory;

        if (sshd != null)
            return;

        try {
            sshd = createSSHServer(this.spokesmanProperties);
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
