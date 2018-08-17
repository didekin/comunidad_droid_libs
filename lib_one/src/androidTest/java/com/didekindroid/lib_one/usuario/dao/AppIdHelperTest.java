package com.didekindroid.lib_one.usuario.dao;

import org.junit.Before;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSecurity;
import static com.didekindroid.lib_one.usuario.dao.AppIdHelper.appIdSingle;
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
}