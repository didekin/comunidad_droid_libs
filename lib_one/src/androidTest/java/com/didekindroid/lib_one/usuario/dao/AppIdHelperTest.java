package com.didekindroid.lib_one.usuario.dao;

import com.didekindroid.lib_one.api.exception.UiException;

import org.junit.Before;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSecurity;
import static com.didekindroid.lib_one.usuario.dao.AppIdHelper.appIdSingle;
import static com.didekindroid.lib_one.util.ConnectionUtils.isInternetConnected;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.FIREBASE_SERVICE_NOT_AVAILABLE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AppIdHelperTest {

    @Before
    public void setUp()
    {
        initSecurity(getTargetContext());
    }

    @Test
    public void test_GetTokenSingle()
    {
        String token = appIdSingle.getTokenSingle().blockingGet();
        assertThat(token.length() > 1, is(true));
    }

    @Test
    public void test_GetToken_1()
    {
        if (!isInternetConnected(getTargetContext())) {
            appIdSingle.getTokenSingle().test().assertError(
                    uiException -> UiException.class.cast(uiException).getErrorHtppMsg().equals(FIREBASE_SERVICE_NOT_AVAILABLE.getHttpMessage())
            );
        }
    }
}