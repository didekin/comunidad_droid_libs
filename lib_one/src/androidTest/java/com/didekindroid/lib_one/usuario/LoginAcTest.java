package com.didekindroid.lib_one.usuario;

import android.app.Activity;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.checkTextsInDialog;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.isToastInView;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http_Router;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_RODRIGO;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regUserComuWithTkCache;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.loginAcResourceId;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.lib_one.usuario.testutil.UserEspressoTestUtil.typeLoginData;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 26/10/15
 * Time: 13:55
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class LoginAcTest {

    private LoginAc activity;

    @Rule
    public ActivityTestRule<? extends Activity> mActivityRule = new ActivityTestRule<LoginAc>(LoginAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            initSec_Http_Router(getTargetContext());
            try {
                regUserComuWithTkCache(comu_real_rodrigo);
            } catch (Exception e) {
                fail();
            }
        }

        @Override
        protected Intent getActivityIntent()
        {
            Intent intent = new Intent();
            intent.putExtra(user_name.key, user_crodrigo.getUserName());
            return intent;
        }
    };

    @Before
    public void setUp()
    {
        activity = (LoginAc) mActivityRule.getActivity();
        assertThat(activity.getIntent().hasExtra(user_name.key), is(true));
    }

    @After
    public void cleanUp()
    {
        cleanOptions(CLEAN_RODRIGO);
    }

    //    ==================================  TESTS INTEGRATIOIN  ==================================

    @Test
    public final void testOnCreate()
    {
        onView(allOf(
                withId(R.id.reg_usuario_email_editT),
                withText(user_crodrigo.getUserName())
        )).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usuario_password_ediT)).check(matches(isDisplayed()));
        onView(withId(R.id.login_ac_button)).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withContentDescription(R.string.navigate_up_txt)).check(matches(isDisplayed()));
    }

    @Test
    public final void testOnStop()
    {
        activity.runOnUiThread(() -> getInstrumentation().callActivityOnStop(activity));
        // Check.
        assertThat(activity.getViewerLogin().getController().getSubscriptions().size(), is(0));
    }

    @Test   // Login NOT OK, counterWrong > 3.
    public void testValidateLoginRemote_1()
    {
        activity.getViewerLogin().getCounterWrong().set(3);
        typeLoginData(user_crodrigo.getUserName(), "password_wrong");
        onView(withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());

        waitAtMost(6, SECONDS).untilAtomic(activity.getViewerLogin().getCounterWrong(), equalTo(4));
        checkTextsInDialog(R.string.send_password_by_mail_dialog, R.string.send_password_by_mail_YES);
    }

    @Test   // Login NOT OK, counterWrong <= 3.
    public void testValidateLoginRemote_2() throws InterruptedException
    {
        SECONDS.sleep(2);

        activity.getViewerLogin().getCounterWrong().set(2);
        typeLoginData(user_crodrigo.getUserName(), "password_wrong");
        onView(withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click(), closeSoftKeyboard());

        onView(withId(loginAcResourceId)).check(matches(isDisplayed()));
        waitAtMost(3, SECONDS).until(isToastInView(R.string.password_wrong, activity));
    }
}
