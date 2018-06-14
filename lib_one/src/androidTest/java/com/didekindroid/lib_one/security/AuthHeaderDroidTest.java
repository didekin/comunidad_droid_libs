package com.didekindroid.lib_one.security;

import android.support.test.runner.AndroidJUnit4;
import android.util.Base64;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.usuario.AuthHeaderIf;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class AuthHeaderDroidTest {

    private static final String tokenInLocal = "eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2In0" +
            "." +
            "._L86WbOFHY-3g0E2EXejJg" +
            ".UB1tHZZq0TYFTZKPVZXY83GRxHz770Aq7BuMCEbNnaSC5cVNOLEOgBQrOQVJmVL-9Ke9KRSwuq7MmVcA2EB_0xRBr_YbzmMWbpUcTQUFtE5OZOFiCsxL5Yn0gA_DDLZboivpoSqndQRP-44mWVkM1A" +
            ".RIvTWRrsyoJ1mpl8vUhQDQ";

    private static final String userName = "pedro@didekin.com";
    private static final String appId = "cVNOLEOgBQrOQVJmVL-9Ke9KRSw..uq:7MmVcA2EB_0xRBr";

    private AuthHeaderIf header;

    @Before
    public void setUp() throws UiException
    {
        header = new AuthTkCacher.AuthHeaderDroid(userName, appId, tokenInLocal);
    }

    @Test
    public void test_toString()
    {
        assertThat(header.toString(), allOf(
                containsString("\"userName\"" + ":" + "\"" + userName + "\""),
                containsString("\"appID\"" + ":" + "\"" + appId + "\""),
                containsString("\"token\"" + ":" + "\"" + tokenInLocal + "\""),
                containsString("{"),
                containsString("}")
        ));
    }

    @Test
    public void test_getBase64Str()
    {
        String headerBase64 = header.getBase64Str();
        System.out.printf("%s%n", headerBase64);
        assertThat(new String(Base64.decode(headerBase64, Base64.URL_SAFE)), is(header.toString()));
    }

    @Test
    public void test_newAuthHeaderDroid()
    {
        AuthHeaderIf headerPojo = new AuthTkCacher.AuthHeaderDroid(header.getBase64Str());
        assertThat(headerPojo.getUserName(), allOf(
                is(header.getUserName()),
                is(userName)
        ));
        assertThat(headerPojo.getAppID(), allOf(
                is(header.getAppID()),
                is(appId)
        ));
        assertThat(headerPojo.getToken(), allOf(
                is(header.getToken()),
                is(tokenInLocal)
        ));
    }
}
