package com.himex.s3;

import com.himex.SpokesmanProperties;
import org.apache.sshd.common.session.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @Author rossdrew
 */
@RunWith(MockitoJUnitRunner.class)
public class TestS3FileSystemFactory {
    private S3FileSystemFactory factory;

    @Mock
    private SpokesmanProperties mockProperties;
    @Mock
    private S3FileSystemProviderPlus mockProvider;
    @Mock
    private Session mockSession;

    private Map<String, SpokesmanProperties.UserConfig> users = new HashMap<>();

    @Before
    public void setUp(){
        factory = new S3FileSystemFactory(mockProperties, mockProvider);
        SpokesmanProperties.UserConfig testUserConfig = new SpokesmanProperties.UserConfig();
        testUserConfig.setHome("testHome");
        users.put("testUsername", testUserConfig);
    }

    @Test
    public void testCreateFileSystem() throws IOException {
        when(mockSession.getUsername()).thenReturn("testUsername");
        when(mockProperties.getUsers()).thenReturn(users);

        verify(mockProvider).newFileSystem(any(URI.class),
                (Map<String, ?>)argThat(hasEntry(S3FileSystemProviderPlus.PROP_USERNAME, "testUsername")));
        verify(mockProvider).newFileSystem(any(URI.class),
                (Map<String, ?>)argThat(hasEntry(S3FileSystemProviderPlus.PROP_USERHOME, "testHome")));
    }

    @Test
    public void testCreateFileSystemWithUnknownUser() throws IOException {
        when(mockSession.getUsername()).thenReturn("invalidUsername");
        when(mockProperties.getUsers()).thenReturn(users);

        verify(mockProvider).newFileSystem(any(URI.class),
                (Map<String, ?>)argThat(hasEntry(S3FileSystemProviderPlus.PROP_USERNAME, "invalidUsername")));
        verify(mockProvider).newFileSystem(any(URI.class),
                (Map<String, ?>)argThat(not(hasEntry(S3FileSystemProviderPlus.PROP_USERHOME, ""))));
    }
}
