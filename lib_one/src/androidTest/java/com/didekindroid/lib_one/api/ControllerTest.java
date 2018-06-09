package com.didekindroid.lib_one.api;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.security.AuthTkCacher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.disposables.Disposable;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 17:42
 */
@RunWith(AndroidJUnit4.class)
public class ControllerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    private Controller controller;

    @Before
    public void setUp()
    {
        controller = new Controller(new AuthTkCacher(activityRule.getActivity()));
    }

    @Test
    public void testGetSubscriptions()
    {
        assertThat(controller.getSubscriptions(), allOf(
                is(controller.getSubscriptions()),
                notNullValue()
        ));
        assertThat(controller.getSubscriptions().size(), is(0));
    }

    @Test
    public void testClearSubscriptions()
    {
        assertThat(controller.getSubscriptions().size(), is(0));
        controller.getSubscriptions().add(new Disposable() {
            @Override
            public void dispose()
            {
            }

            @Override
            public boolean isDisposed()
            {
                return false;
            }
        });
        assertThat(controller.getSubscriptions().size(), is(1));
        controller.clearSubscriptions();
        assertThat(controller.getSubscriptions().size(), is(0));
    }

    @Test
    public void testIsRegisteredUser()
    {
        controller.updateIsRegistered(true);
        assertThat(controller.isRegisteredUser(), is(true));
    }

    @Test
    public void testUpdateIsRegistered()
    {
        controller.updateIsRegistered(false);
        assertThat(controller.isRegisteredUser(), is(false));
    }

    @Test
    public void testGetIdentityCacher()
    {
        assertThat(controller.getTkCacher(), notNullValue());
    }
}