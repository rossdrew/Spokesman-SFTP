package com.himex.auth;

import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import java.security.PublicKey;

/**
 * @Author rossdrew
 * @Created 07/06/16.
 */
public class SFTPPublicKeyAuthenticator implements PublickeyAuthenticator {
    @Override
    public boolean authenticate(String username, PublicKey key, ServerSession session) {
        //TODO public key validation
        return true;
    }
}
