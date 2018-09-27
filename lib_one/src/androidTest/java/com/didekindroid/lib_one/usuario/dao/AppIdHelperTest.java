package com.didekindroid.lib_one.usuario.dao;

import com.didekindroid.lib_one.api.exception.UiException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.cleanInitialSec;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSecurity;
import static com.didekindroid.lib_one.usuario.dao.AppIdHelper.appIdSingle;
import static com.didekindroid.lib_one.util.ConnectionUtils.isInternetConnected;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.FIREBASE_SERVICE_NOT_AVAILABLE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AppIdHelperTest {

    @Before
    public void setUp()
    {
        initSecurity(getTargetContext());
        waitAtMost(4, SECONDS).until(() -> secInitializer.get().getJksInClient() != null);
    }

    @AfterClass
    public static void cleanUp()
    {
        cleanInitialSec();
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