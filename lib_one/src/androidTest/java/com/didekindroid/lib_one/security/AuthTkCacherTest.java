package com.didekindroid.lib_one.security;

import android.app.Activity;
import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.exception.UiException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.MODE_PRIVATE;
import static com.didekindroid.lib_one.security.AuthTkCacher.AuthTkCacherExceptionMsg.AUTH_HEADER_WRONG;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.app_pref_file_name;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.authToken_key;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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
    private Context activity;

    @Before
    public void getFixture()
    {
        activity = activityRule.getActivity();
        tkCacher = new AuthTkCacher(activity);
    }

    @After
    public void cleanUp(){
        tkCacher.updateAuthToken(null);
    }

    // ===================================== TESTS ==========================================

    @Test
    public void test_new()
    {
        AuthTkCacher cacher = new AuthTkCacher(activity);
        assertThat(cacher, notNullValue());
        assertThat(cacher.getAuthTokenCache(), nullValue());
    }

    @Test
    public void test_updateAuthToken()
    {
        // Exec.
        tkCacher.updateAuthToken("updated_authToken");
        /* Check.*/
        assertThat(tkCacher.getAuthTokenCache(), is("updated_authToken"));
        assertThat(tkCacher.isUserRegistered(), is(true));
        assertThat(tkCacher.getAuthTokenCache().equals(
                activity.getSharedPreferences(
                        app_pref_file_name.toString(), MODE_PRIVATE).getString(authToken_key.toString(), null)),
                is(true));
        // Exec.
        tkCacher.updateAuthToken(null);
        /* Check.*/
        assertThat(tkCacher.getAuthTokenCache(), nullValue());
        assertThat(tkCacher.isUserRegistered(), is(false));
    }

    @Test
    public void test_doAuthHeaderStr() throws UiException
    {
        // Inicializamos todos los datos.
        tkCacher.updateAuthToken("pepe_authToken");
        assertThat(tkCacher.doAuthHeaderStr("pepe_gcmToken").length() > 0, is(true));
    }

    @Test
    public void test_newAuthHeaderDroid()
    {
        // NO inicializamos todos los datos.
        try {
            new AuthTkCacher.AuthHeaderDroid(null, "pepe_authToken");
            fail();
        } catch (UiException ue) {
            assertThat(ue.getErrorBean().getMessage(), is(AUTH_HEADER_WRONG.getHttpMessage()));
        }
    }

    @Test
    public void test_doAuthHeaderMock_1() throws UiException
    {
        // Inicializamos todos los datos.
        tkCacher.updateAuthToken("pepe_authToken");
        assertThat(tkCacher.doAuthHeaderStrMock("mock_gcm").length() > 0, is(true));
    }
}