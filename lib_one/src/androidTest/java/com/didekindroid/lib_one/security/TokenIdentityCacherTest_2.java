package com.didekindroid.lib_one.security;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.ActivityMock;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.lib_one.testutil.EspressoTestUtil.writeFile;
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
public class TokenIdentityCacherTest_2 {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class);

    private TokenIdentityCacher tkCacher;
    private Activity activity;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
    }

    @After
    public void cleanUp()
    {
        tkCacher.cleanIdentityCache();
        tkCacher.updateIsRegistered(false);
    }

    // ===================================== TESTS ==========================================

    @Test
    public void test_InitClass_1()
    {
        // Preconditions: escribimos fichero.
        writeFile("test_refreshToken");
        // Inicializamos caché.
        tkCacher = new TokenIdentityCacher(activity);
        // Check.
        assertThat(tkCacher.getTokenCache().get(), notNullValue());
        assertThat(tkCacher.getTokenCache().get().getRefreshToken().getValue(), is("test_refreshToken"));
    }

    @Test
    public void test_InitClass_2()
    {
        // Empty file.
        writeFile("");
        // Inicializamos caché.
        tkCacher = new TokenIdentityCacher(activity);
        // Check.
        assertThat(tkCacher.getTokenCache().get(), nullValue());
    }

    @Test
    public void test_InitClass_3()
    {
        // Preconditions: no file.
        // Inicializamos caché.
        tkCacher = new TokenIdentityCacher(activity);
        // Check.
        assertThat(tkCacher.getTokenCache().get(), nullValue());
    }
}