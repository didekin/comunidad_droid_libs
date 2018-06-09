package com.didekindroid.lib_one.security;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

import com.didekindroid.lib_one.api.ActivityMock;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.didekindroid.lib_one.testutil.InitializerTestUtil.bks_name;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.bks_pswd;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 10/04/2018
 * Time: 14:18
 */
public class SecInitializerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class);

    private AuthTkCacher tkCacher;
    private Activity activity;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
    }

    @After
    public void cleanUp()
    {
        tkCacher.updateIsRegistered(false);
    }

    @Test
    public void test_SecInitializer_1()
    {
        // Exec.
        SecInitializer secInitializer = new SecInitializer(activity, bks_pswd, bks_name);
        // Check.
        assertThat(secInitializer.getJksInClient(), notNullValue());
        assertThat(secInitializer.getAppResources(), notNullValue());
        tkCacher = (AuthTkCacher) secInitializer.getTkCacher();
        assertThat(tkCacher, notNullValue());
        assertThat(tkCacher.isRegisteredCache(), nullValue());
    }
}