package com.didekindroid.lib_one.security;

import android.app.Activity;
import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.usuario.UserTestData;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.MODE_PRIVATE;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.app_pref_file_name;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.authToken_key;
import static com.didekindroid.lib_one.usuario.UserTestData.authTokenExample;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
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
    private Context activity;

    @Before
    public void getFixture()
    {
        activity = activityRule.getActivity();
        tkCacher = new AuthTkCacher(activity);
        waitAtMost(2, SECONDS).until(() -> tkCacher != null);
    }

    @After
    public void cleanUp() throws UiException
    {
        if (tkCacher != null) {
            tkCacher.updateAuthToken(null);
        }
    }

    // ===================================== TESTS ==========================================

    @Test
    public void test_new()
    {
        assertThat(tkCacher.getAuthTokenCache(), nullValue());
    }

    @Test
    public void test_updateAuthToken() throws UiException
    {
        // Exec.
        tkCacher.updateAuthToken(authTokenExample);
        /* Check.*/
        assertThat(tkCacher.getAuthTokenCache(), is(authTokenExample));
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
}