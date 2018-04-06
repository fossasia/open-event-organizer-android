package org.fossasia.openevent.app.data.auth;

import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.core.main.MainActivity;
import org.fossasia.openevent.app.data.Preferences;
import org.fossasia.openevent.app.data.event.Event;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthHolderTest {

    private static final String EXPIRED_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
        ".eyJuYmYiOjE0OTU3NDU0MDAsImlhdCI6MTQ5NTc0NTQwMCwiZXhwIjoxNDk1NzQ1ODAwLCJpZGVudGl0eSI6MzQ0fQ" +
        ".NlZ9mrmEPyGpzQ-aIqauhwliYLh9GMiz11sG-EUaQ6I";
    private static final String UNEXPIRABLE_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
        ".eyJuYmYiOjE0OTU3NDU0MDAsImlhdCI6MTQ5NTc0NTQwMCwiZXhwIjoyNDk1ODMxODAwLCJpZGVudGl0eSI6MzQ0fQ" +
        ".A_aC4hwK8sixZk4k9gzmzidO1wj2hjy_EH573uorK-E";

    private AuthHolder authHolder;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock private Preferences preferenceModel;

    @Before
    public void setUp() {
        authHolder = new AuthHolder(preferenceModel);
    }

    @Test
    public void shouldReturnNull() {
        assertNull(authHolder.getToken());
    }

    @Test
    public void shouldBeLoggedOut() {
        assertFalse(authHolder.isLoggedIn());
    }

    @Test
    public void shouldReturnWrongIdentity() {
        assertEquals(-1, authHolder.getIdentity());
    }

    @Test
    public void shouldReturnCorrectIdentity() {
        authHolder.login(UNEXPIRABLE_TOKEN);

        assertEquals(344, authHolder.getIdentity());
    }

    @Test
    public void shouldReturnStoredToken() {
        when(preferenceModel.getString(any(), any())).thenReturn("token");

        assertEquals("token", authHolder.getToken());
    }

    @Test
    public void shouldSayLoggedInOnUnexpired() {
        authHolder.login(UNEXPIRABLE_TOKEN);

        assertTrue(authHolder.isLoggedIn());
    }

    @Test
    public void shouldSayLoggedOutOnExpired() {
        authHolder.login(EXPIRED_TOKEN);

        assertFalse(authHolder.isLoggedIn());
    }

    @Test
    public void shouldSayLoggedInOnUnexpiredSaved() {
        when(preferenceModel.getString(any(), any())).thenReturn(UNEXPIRABLE_TOKEN);

        assertTrue(authHolder.isLoggedIn());
    }

    @Test
    public void shouldSayLoggedOutOnLogout() {
        authHolder.login(UNEXPIRABLE_TOKEN);

        assertTrue(authHolder.isLoggedIn());

        authHolder.logout();

        assertFalse(authHolder.isLoggedIn());
    }

    @Test
    public void shouldClearSelectedEventOnLogout() {
        ContextManager.setSelectedEvent(new Event());

        authHolder.logout();

        verify(preferenceModel).setLong(MainActivity.EVENT_KEY, -1);
        assertNull(ContextManager.getSelectedEvent());
    }

}
