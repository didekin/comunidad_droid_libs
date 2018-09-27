package com.didekindroid.lib_one.security;

import android.app.Activity;
import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Base64;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.model.usuario.http.AuthHeaderIf;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.MODE_PRIVATE;
import static com.didekindroid.lib_one.security.AuthTkCacher.AuthTkCacherExceptionMsg.AUTH_HEADER_WRONG;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.app_pref_file_name;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.authToken_key;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
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

    private static final String authTokenInLocal = "eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2In0" +
            "." +
            "._L86WbOFHY-3g0E2EXejJg" +
            ".UB1tHZZq0TYFTZKPVZXY83GRxHz770Aq7BuMCEbNnaSC5cVNOLEOgBQrOQVJmVL-9Ke9KRSwuq7MmVcA2EB_0xRBr_YbzmMWbpUcTQUFtE5OZOFiCsxL5Yn0gA_DDLZboivpoSqndQRP-44mWVkM1A" +
            ".RIvTWRrsyoJ1mpl8vUhQDQ";

    private static final String appId = "cVNOLEOgBQrOQVJmVL-9Ke9KRSw..uq:7MmVcA2EB_0xRBr";

    @Rule
    public ActivityTestRule<? extends Activity> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    private AuthTkCacher tkCacher;
    private AuthHeaderIf header;
    private Context activity;

    @Before
    public void getFixture()
    {
        activity = activityRule.getActivity();
    }

    @After
    public void cleanUp(){
        if (tkCacher != null){
            tkCacher.updateAuthToken(null);
        }
    }

    // ===================================== TESTS ==========================================

    @Test
    public void test_new()
    {
        tkCacher = new AuthTkCacher(activity);
        waitAtMost(2, SECONDS).until(() -> tkCacher != null);
        assertThat(tkCacher.getAuthTokenCache(), nullValue());
    }

    @Test
    public void test_updateAuthToken()
    {
        // Exec.
        tkCacher = new AuthTkCacher(activity);
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
        tkCacher = new AuthTkCacher(activity);
        tkCacher.updateAuthToken("pepe_authToken");
        assertThat(tkCacher.doAuthHeaderStr("pepe_gcmToken").length() > 0, is(true));
    }

    @Test
    public void test_doAuthHeaderMock_1() throws UiException
    {
        // Inicializamos todos los datos.
        tkCacher = new AuthTkCacher(activity);
        tkCacher.updateAuthToken("pepe_authToken");
        assertThat(tkCacher.doAuthHeaderStrMock("mock_gcm").length() > 0, is(true));
    }

    // ============================ AuthHeaderDroid tests ===============================

    @Test
    public void test_newAuRthHeaderDroid_1()
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
    public void test_newAuthHeaderDroid_2() throws UiException
    {
        header = new AuthTkCacher.AuthHeaderDroid(appId, authTokenInLocal);
        AuthHeaderIf headerPojo = new AuthTkCacher.AuthHeaderDroid(header.getBase64Str());
        assertThat(headerPojo.getAppID(), allOf(
                is(header.getAppID()),
                is(appId)
        ));
        assertThat(headerPojo.getToken(), allOf(
                is(header.getToken()),
                is(authTokenInLocal)
        ));
    }

    @Test
    public void test_toString() throws UiException
    {
        header = new AuthTkCacher.AuthHeaderDroid(appId, authTokenInLocal);
        assertThat(header.toString(), allOf(
                containsString("\"appID\"" + ":" + "\"" + appId + "\""),
                containsString("\"token\"" + ":" + "\"" + authTokenInLocal + "\""),
                containsString("{"),
                containsString("}")
        ));
    }

    @Test
    public void test_getBase64Str() throws UiException
    {
        header = new AuthTkCacher.AuthHeaderDroid(appId, authTokenInLocal);
        String headerBase64 = header.getBase64Str();
        System.out.printf("%s%n", headerBase64);
        assertThat(new String(Base64.decode(headerBase64, Base64.URL_SAFE)), is(header.toString()));
    }
}