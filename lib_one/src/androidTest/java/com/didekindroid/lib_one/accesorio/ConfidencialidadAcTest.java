package com.didekindroid.lib_one.accesorio;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.api.ActivityMock;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static android.app.TaskStackBuilder.create;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.checkUp;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.isResourceIdDisplayed;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.isViewDisplayed;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.cleanInitialSec;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http_Router;
import static com.didekindroid.lib_one.testutil.MockTestConstant.mockAcLayout;
import static com.didekindroid.lib_one.testutil.UiTestUtil.cleanTasks;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 18/09/17
 * Time: 13:06
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class ConfidencialidadAcTest {

    private TaskStackBuilder stackBuilder;
    private Activity activity;

    @BeforeClass
    public static void setMore()
    {
        initSec_Http_Router(getTargetContext());
    }

    @Rule
    public ActivityTestRule<ConfidencialidadAc> activityRule = new ActivityTestRule<ConfidencialidadAc>(ConfidencialidadAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                stackBuilder = create(getTargetContext()).addParentStack(ConfidencialidadAc.class);
                stackBuilder.startActivities();
            }
        }
    };

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
    }

    @After
    public void tearDown()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cleanTasks(activity);
        }
    }

    @AfterClass
    public static void cleanUp()
    {
        cleanInitialSec();
    }

    // ---------------------------------------------------------------------------------------------------

    @Test
    public void testBackStack()
    {
        List<Intent> intents = Arrays.asList(stackBuilder.getIntents());
        assertThat(intents.size(), is(1));
        // El intent con posición inferior es el primero que hemos añadido.
        assertThat(intents.get(0).getComponent().getClassName(), is(ActivityMock.class.getName()));
    }

    @Test
    public void test_OnCreate_UP()
    {
        onView(withId(R.id.proteccion_datos_textview)).check(matches(withText(R.string.proteccion_datos_txt)));
        onView(withId(R.id.confidencialidad_fab)).check(matches(isDisplayed()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            assertThat(routerInitializer.get().getDefaultAc().equals(ActivityMock.class), is(true));
            checkUp(mockAcLayout);
        }
    }

    @Test
    public void test_FabOk()
    {
        waitAtMost(6, SECONDS).until(isViewDisplayed(withId(R.id.confidencialidad_fab)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            onView(withId(R.id.confidencialidad_fab)).perform(click());
            waitAtMost(4, SECONDS).until(isResourceIdDisplayed(mockAcLayout)); // It depends on backStack.
        }
    }
}