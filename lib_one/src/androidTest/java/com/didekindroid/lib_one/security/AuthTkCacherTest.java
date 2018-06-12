package com.didekindroid.lib_one.security;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekinlib.http.usuario.AuthHeaderIf;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.lib_one.usuario.UserTestData.USER_PEPE;
import static com.google.firebase.iid.FirebaseInstanceId.getInstance;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 29/06/15
 * Time: 08:11
 */
@RunWith(AndroidJUnit4.class)
public class AuthTkCacherTest {

    @Rule
    public ActivityTestRule<? extends Activity> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    private AuthTkCacher tkCacher;

    @Before
    public void getFixture()
    {
        tkCacher = new AuthTkCacher(activityRule.getActivity());
        tkCacher.updateIsRegistered(false);
    }

    // ===================================== TESTS ==========================================

    @Test
    public void test_UpdateIsRegistered()
    {
        tkCacher.updateIsRegistered(true);
        assertThat(tkCacher.isRegisteredUser(), is(true));
        assertThat(tkCacher.isRegisteredCache(), is(true));

        // Inicializamos todos los datos.
        tkCacher.updateUserName(USER_PEPE.getUserName()).updateAuthToken("pepe_authToken").updateIsGcmTokenSentServer(true);
        // Exec.
        tkCacher.updateIsRegistered(false);
        assertThat(tkCacher.isRegisteredUser(), is(false));
        assertThat(tkCacher.isRegisteredCache(), is(false));
        assertThat(tkCacher.isGcmTokenSentServer(), is(false));
        assertThat(tkCacher.getAuthToken(), nullValue());
        assertThat(tkCacher.getUserName(), nullValue());
    }

    @Test
    public void test_updateUserName()
    {
        tkCacher.updateUserName(USER_PEPE.getUserName());
        assertThat(tkCacher.getUserName(), is(USER_PEPE.getUserName()));
        assertThat(tkCacher.getUserNameCache(), is(USER_PEPE.getUserName()));
        assertThat(tkCacher.isRegisteredCache(), is(true));
    }

    @Test
    public void test_updateIsGcmTokenSentServer()
    {
        tkCacher.updateIsRegistered(false);
        // Exec.
        tkCacher.updateIsGcmTokenSentServer(true);
        // Check.
        assertThat(tkCacher.isGcmTokenSentServer(), is(true));
        assertThat(tkCacher.isRegisteredCache(), is(true)); // Change value in cache.
        // Exec.
        tkCacher.updateIsGcmTokenSentServer(false);
        // Check.
        assertThat(tkCacher.isGcmTokenSentServer(), is(false));
        assertThat(tkCacher.isRegisteredCache(), is(true)); // Don't change value in cache.
    }

    @Test
    public void test_updateAuthToken()
    {
        // Exec.
        tkCacher.updateAuthToken("updated_authToken");
        /* Check.*/
        assertThat(tkCacher.getAuthToken(), notNullValue());
        assertThat(tkCacher.getAuthTokenCache(), notNullValue());
        assertThat(tkCacher.isRegisteredCache(), is(true));
        // Exec.
        tkCacher.updateAuthToken(null);
        assertThat(tkCacher.getAuthToken(), nullValue());
        assertThat(tkCacher.getAuthTokenCache(), nullValue());
        assertThat(tkCacher.isRegisteredCache(), is(true));
    }

    @Test
    public void test_doAuthHeader()
    {
        // Inicializamos todos los datos.
        tkCacher.updateUserName(USER_PEPE.getUserName()).updateUserName("pepe_authToken");
        AuthHeaderIf authHeader = tkCacher.doAuthHeader();
        assertThat(authHeader.getAppID(), is(getInstance().getToken()));
        assertThat(authHeader.getToken(), is(tkCacher.getAuthTokenCache()));
        assertThat(authHeader.getUserName(), is(tkCacher.getUserNameCache()));

        assertThat(tkCacher.doAuthHeaderStr().length() > 0, is(true));
    }
}