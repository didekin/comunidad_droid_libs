package com.didekindroid.lib_one.usuario;

import android.app.Activity;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.api.exception.UiException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.checkBack;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.isResourceIdDisplayed;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.isToastInView;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http_Router;
import static com.didekindroid.lib_one.testutil.UiTestUtil.focusOnView;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_RODRIGO;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regGetUserComu;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.loginAcResourceId;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.userDataAcRsId;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.lib_one.usuario.UsuarioMockDao.usuarioMockDao;
import static com.didekindroid.lib_one.usuario.testutil.UserEspressoTestUtil.typeUserNameAliasPswd;
import static com.didekindroid.lib_one.usuario.testutil.UserEspressoTestUtil.typeUserNamePswd;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 09/04/2018
 * Time: 12:06
 */
public class UserDataAcTest {

    private UserDataAc activity;

    @Rule
    public IntentsTestRule<? extends Activity> mActivityRule = new IntentsTestRule<UserDataAc>(UserDataAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {

            initSec_Http_Router(getTargetContext());
            try {
                regGetUserComu(comu_real_rodrigo);
            } catch (Exception e) {
                fail();
            }
        }
    };

    @Before
    public void setUp()
    {
        activity = (UserDataAc) mActivityRule.getActivity();
    }

    @After
    public void tearDown()
    {

    }

    // ============================================================
    //    ................ TESTS ..............
    // ============================================================

    @Test  // Wrong password.
    public void testModifyUserDataWrongPswd() throws InterruptedException, UiException
    {
        SECONDS.sleep(2);
        typeUserNameAliasPswd("new_crodrigo@didekin.es", user_crodrigo.getAlias(), "wrong_password");
        onView(withId(R.id.user_data_modif_button)).perform(scrollTo()).check(matches(isDisplayed())).perform(click());
        waitAtMost(6, SECONDS).until(isToastInView(R.string.password_wrong, activity));

        cleanOptions(CLEAN_RODRIGO);
    }

    @Test  // Modify userName and alias OK.
    public void testModifyUserData() throws InterruptedException, IOException
    {
        SECONDS.sleep(2);
        typeClickWait();

        assertThat(usuarioMockDao.deleteUser(USER_DROID.getUserName()).execute().body(), is(true));
    }

    @Test  // Modify userName OK.
    public void testModifyUserData_Back() throws InterruptedException, IOException
    {
        SECONDS.sleep(2);
        typeUserNamePswd(USER_DROID.getUserName(), user_crodrigo.getPassword());
        focusOnView(activity, R.id.user_data_modif_button);
        onView(withId(R.id.user_data_modif_button)).perform(scrollTo(), click());
        // Check passwordSent dialog and back.
        checkBack(onView(withText(R.string.receive_password_by_mail_dialog)).inRoot(isDialog()).check(matches(isDisplayed())), userDataAcRsId);

        assertThat(usuarioMockDao.deleteUser(USER_DROID.getUserName()).execute().body(), is(true));
    }

    @Test
    public final void testOnStop() throws UiException
    {
        activity.runOnUiThread(() -> {
            getInstrumentation().callActivityOnStop(activity);
            // Check.
            assertThat(requireNonNull(activity.viewer.getController()).getSubscriptions().size(), is(0));
        });

        cleanOptions(CLEAN_RODRIGO);
    }



    /*    =================================  HELPERS ==================================*/

    private void typeClickWait()
    {
        typeUserNameAliasPswd(USER_DROID.getUserName(), "new_alias", user_crodrigo.getPassword());
        focusOnView(activity, R.id.user_data_modif_button);
        onView(withId(R.id.user_data_modif_button)).perform(scrollTo(), click());
        // Exec.
        onView(withText(R.string.continuar_button_rot)).inRoot(isDialog()).perform(click());
        // Check.
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(loginAcResourceId));
        intended(hasExtra(user_name.key, USER_DROID.getUserName()));
    }
}
