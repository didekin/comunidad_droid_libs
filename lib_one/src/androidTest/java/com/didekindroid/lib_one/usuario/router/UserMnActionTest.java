package com.didekindroid.lib_one.usuario.router;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.accesorio.ConfidencialidadAc;
import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.ActivityNextMock;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.api.router.MnRouterActionIf;
import com.didekindroid.lib_one.usuario.DeleteMeAc;
import com.didekindroid.lib_one.usuario.LoginAc;
import com.didekindroid.lib_one.usuario.PasswordChangeAc;
import com.didekindroid.lib_one.usuario.UserDataAc;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collection;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.checkUp;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.getActivitesInTaskByStage;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.cleanInitialSec;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http_Router;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSecurity;
import static com.didekindroid.lib_one.testutil.MockTestConstant.mockAcLayout;
import static com.didekindroid.lib_one.testutil.MockTestConstant.nextMockAcLayout;
import static com.didekindroid.lib_one.usuario.UserTestData.authTokenExample;
import static com.didekindroid.lib_one.usuario.router.UserMnAction.confidencialidad_mn;
import static com.didekindroid.lib_one.usuario.router.UserMnAction.delete_me_mn;
import static com.didekindroid.lib_one.usuario.router.UserMnAction.login_mn;
import static com.didekindroid.lib_one.usuario.router.UserMnAction.navigateUp;
import static com.didekindroid.lib_one.usuario.router.UserMnAction.password_change_mn;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 09/04/2018
 * Time: 19:08
 */
public class UserMnActionTest {

    @Rule
    public IntentsTestRule<ActivityMock> activityRule = new IntentsTestRule<ActivityMock>(ActivityMock.class) {
        @Override
        protected Intent getActivityIntent()
        {
            Intent intent = new Intent();
            intent.putExtra("keyTest_2", "Value_keyTest_2");
            return intent;
        }
    };

    private ActivityMock activity;

    @BeforeClass
    public static void setMore()
    {
        initSecurity(getTargetContext());
    }

    @Before
    public void setUp() throws UiException
    {
        activity = activityRule.getActivity();
        secInitializer.get().getTkCacher().updateAuthToken(null);
    }

    @AfterClass
    public static void cleanUp()
    {
        cleanInitialSec();
    }

    // ============================================================
    //    ................ TESTS ..............
    // ============================================================

    @Test
    public void test_navigateUp_1() throws Exception
    {
        ActivityManager manager = (ActivityManager) activity.getSystemService(ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            requireNonNull(manager).getAppTasks().get(0)
                    .startActivity(activity, new Intent(activity, ActivityNextMock.class), new Bundle(0));
            // Calling indirectly the method to test and check new activity layout.
            checkUp(mockAcLayout);
            // Check that the up activity is resumed and has the original intent.
            Collection<Activity> activities = getActivitesInTaskByStage(RESUMED);
            assertThat(activities.size(), is(1));
            for (Activity next : activities) {
                assertThat(next.getComponentName().getClassName(), is(ActivityMock.class.getCanonicalName()));
                assertThat(next.getIntent().getStringExtra("keyTest_2"), is("Value_keyTest_2"));
            }
        }
    }

    @Test
    public void test_navigateUp_2()
    {
        // From ActivityMock we initiate ActivityNextMock.
        Intent intent = new Intent(getTargetContext(), ActivityNextMock.class).setFlags(FLAG_ACTIVITY_NEW_TASK);
        ActivityNextMock nextAc = (ActivityNextMock) getInstrumentation().startActivitySync(intent);
        onView(withId(nextMockAcLayout)).check(matches(isDisplayed()));
        // Navigate up to ActivityMock from ActivityNextMock.
        navigateUp.initActivity(nextAc);
        onView(withId(R.id.mock_ac_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void test_confidencialidad_mn()
    {
        confidencialidad_mn.initActivity(activity);
        intended(hasComponent(ConfidencialidadAc.class.getName()));
    }

    @Test
    public void test_delete_me_mn() throws UiException
    {
        initSec_Http(activity);
        secInitializer.get().getTkCacher().updateAuthToken(authTokenExample);
        waitAtMost(4, SECONDS).until(secInitializer.get().getTkCacher()::isUserRegistered);

        delete_me_mn.initActivity(activity);
        intended(hasComponent(DeleteMeAc.class.getName()));
    }

    @Test
    public void test_login_mn()
    {
        initSec_Http_Router(activity);
        login_mn.initActivity(activity);
        intended(hasComponent(LoginAc.class.getName()));
    }

    @Test
    public void test_password_change_mn() throws UiException
    {
        initSec_Http_Router(activity);
        waitAtMost(4, SECONDS).until(() -> secInitializer.get() != null);
        secInitializer.get().getTkCacher().updateAuthToken(authTokenExample);

        password_change_mn.initActivity(activity);
        intended(hasComponent(PasswordChangeAc.class.getName()));
    }

    @Test
    public void test_user_data_mn() throws UiException
    {
        initSec_Http_Router(activity);
        waitAtMost(4, SECONDS).until(() -> secInitializer.get() != null && secInitializer.get().getTkCacher() != null);
        secInitializer.get().getTkCacher().updateAuthToken(authTokenExample);

        MnRouterActionIf userMenuData = new MnRouterActionIf() {
            @Override
            public int getMnItemRsId()
            {
                return R.id.user_data_ac_mn;
            }

            @Override
            public Class<? extends Activity> getAcToGo()
            {
                return UserDataAc.class;
            }
        };
        userMenuData.initActivity(activity);
        intended(hasComponent(UserDataAc.class.getName()));
    }
}