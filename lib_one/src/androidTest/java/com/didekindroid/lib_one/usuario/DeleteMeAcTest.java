package com.didekindroid.lib_one.usuario;

import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.v7.app.AppCompatActivity;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.api.ActivityNextMock;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.api.router.ContextualRouterIf;
import com.didekindroid.lib_one.api.router.RouterActionIf;
import com.didekindroid.lib_one.api.router.RouterInitializerMock;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.app.TaskStackBuilder.create;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasFlag;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.checkUp;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http_Router;
import static com.didekindroid.lib_one.testutil.MockTestConstant.nextMockAcLayout;
import static com.didekindroid.lib_one.testutil.UiTestUtil.cleanTasks;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_RODRIGO;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regGetUserComu;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.userDataAcRsId;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 25/09/15
 * Time: 16:24
 */
public class DeleteMeAcTest {

    protected DeleteMeAc activity;

    @Rule
    public IntentsTestRule<? extends AppCompatActivity> mActivityRule = new IntentsTestRule<DeleteMeAc>(DeleteMeAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            try {
                initSec_Http_Router(getTargetContext());
                regGetUserComu(comu_real_rodrigo);
            } catch (Exception e) {
                fail();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                create(getTargetContext()).addParentStack(DeleteMeAc.class).startActivities();
            }
        }
    };
    private boolean mustClean;

    @Before
    public void setUp()
    {
        activity = (DeleteMeAc) mActivityRule.getActivity();
        mustClean = true;
    }

    @After
    public void clean() throws UiException
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cleanTasks(activity);
        }

        if (mustClean) {
            cleanOptions(CLEAN_RODRIGO);
        }
    }

    //    =====================================  TESTS  ==========================================

    @Test
    public void testOnCreate()
    {
        assertThat(activity.controller.getTkCacher().isRegisteredCache(), is(true));
        assertThat(activity, notNullValue());

        onView(withId(R.id.delete_me_ac_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.delete_me_ac_unreg_button)).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(allOf(withContentDescription(R.string.navigate_up_txt), isClickable()))
                .check(matches(isDisplayed()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(userDataAcRsId);
        }
    }

    @Test
    public final void testOnStop()
    {
        activity.runOnUiThread(() -> getInstrumentation().callActivityOnStop(activity));
        // Check.
        assertThat(activity.controller.getSubscriptions().size(), CoreMatchers.is(0));
    }

    @Test
    public final void testOnStart()
    {
        activity.runOnUiThread(() -> {
            getInstrumentation().callActivityOnStart(activity);
            // Check.
            assertThat(activity.controller, notNullValue());
        });
    }

    @Test
    public void testUnregisterUser()
    {
        routerInitializer.set(new RouterInitializerMock() {
            @Override
            public ContextualRouterIf getContextRouter()
            {
                return contextualName -> (RouterActionIf) () -> ActivityNextMock.class;
            }
        });

        onView(withId(R.id.delete_me_ac_unreg_button)).check(matches(isDisplayed())).perform(click());
        waitAtMost(4, SECONDS).until(() -> activity.controller.isRegisteredUser(), is(false));
        onView(withId(nextMockAcLayout)).check(matches(isDisplayed()));
        intended(hasFlag(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK));

        mustClean = false;
    }
}