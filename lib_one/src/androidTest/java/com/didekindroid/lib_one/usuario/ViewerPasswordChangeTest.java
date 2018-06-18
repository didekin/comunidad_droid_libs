package com.didekindroid.lib_one.usuario;

import android.app.Activity;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.usuario.dao.CtrlerUsuario;
import com.didekinlib.http.exception.ErrorBean;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.isResourceIdDisplayed;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.isToastInView;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http_Router;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regGetUserComu;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.loginAcResourceId;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.pswdChangeAcRsId;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.userDataAcRsId;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.lib_one.usuario.testutil.UserEspressoTestUtil.typePswdConfirmPswd;
import static com.didekindroid.lib_one.usuario.testutil.UserEspressoTestUtil.typePswdWithPswdValidation;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.BAD_REQUEST;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.PASSWORD_NOT_SENT;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_NOT_FOUND;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 25/03/17
 * Time: 13:09
 */
public class ViewerPasswordChangeTest {

    private Usuario usuario;
    @Rule
    public ActivityTestRule<? extends Activity> mActivityRule = new ActivityTestRule<PasswordChangeAc>(PasswordChangeAc.class) {

        @Override
        protected Intent getActivityIntent()
        {
            usuario = null;
            try {
                initSec_Http_Router(getTargetContext());
                usuario = regGetUserComu(comu_real_rodrigo);
            } catch (Exception e) {
                fail();
            }
            return new Intent().putExtra(user_name.key, usuario.getUserName());
        }
    };
    private PasswordChangeAc activity;

    @Before
    public void setUp()
    {
        activity = (PasswordChangeAc) mActivityRule.getActivity();
    }

    @After
    public void clearUp()
    {
        cleanOneUser(user_crodrigo.getUserName());
    }

    //    ============================  TESTS  ===================================

    @Test
    public void testNewViewerPswdChange_1()
    {
        assertThat(activity.viewer.getController(), instanceOf(CtrlerUsuario.class));
        assertThat(activity.viewer.userName.get(), is(activity.getIntent().getStringExtra(user_name.key)));
        assertThat(activity.viewer.usuarioBean, notNullValue());
    }

    @Test
    public void testDoViewInViewer()
    {
        // Esperamos a tener inicializado activity.viewer.userName, y lo ponemos a null.
        waitAtMost(2, SECONDS).until(() -> activity.viewer.userName.getAndSet(null).equals(usuario.getUserName()));
        // Exec.
        activity.viewer.doViewInViewer(null, null);
        // Check that the field is update correctly.
        waitAtMost(4, SECONDS).until(() ->
                activity.viewer.userName.get() != null && activity.viewer.userName.get().equals(usuario.getUserName()));
    }

    @Test
    public void testProcessBackUserDataLoaded()
    {
        // Precondition.
        assertThat(activity.viewer.userName.getAndSet(null), is(usuario.getUserName()));
        // Run
        activity.runOnUiThread(() -> {
            activity.viewer.processBackUserDataLoaded(USER_DROID);
            // Check.
            assertThat(activity.viewer.userName.get(), is(USER_DROID.getUserName()));
        });
    }

    @Test
    public void testCheckLoginData_1()
    {
        // Caso WRONG: We test the change to false.
        typePswdConfirmPswd("password1", "password2");
        // Run.
        activity.runOnUiThread(() -> activity.viewer.checkLoginData());
        // Check.
        waitAtMost(4L, SECONDS).until(isToastInView(R.string.error_validation_msg, activity, R.string.password_different));
    }

    @Test
    public void testCheckLoginData_2()
    {
        // Caso WRONG: wrong format for current passwordSend.
        typePswdWithPswdValidation("password1", "password1", "wrong+passwordSend");

        activity.runOnUiThread(() -> activity.viewer.checkLoginData());
        waitAtMost(4L, SECONDS).until(isToastInView(R.string.password_wrong, activity));
    }

    @Test
    public void testCheckLoginData_3()
    {
        // Caso OK: We test the change to true.
        final AtomicBoolean isPswdDataOk = new AtomicBoolean(false);
        typePswdWithPswdValidation("password1", "password1", user_crodrigo.getPassword());

        activity.runOnUiThread(() -> assertThat(isPswdDataOk.getAndSet(activity.viewer.checkLoginData()), is(false)));
        waitAtMost(2, SECONDS).untilTrue(isPswdDataOk);
    }

    @Test
    public void testGetPswdDataFromView()
    {
        typePswdWithPswdValidation("new_password", "confirmation", "currentPassword");
        assertThat(activity.viewer.getPswdDataFromView()[0], is("new_password"));
        assertThat(activity.viewer.getPswdDataFromView()[1], is("confirmation"));
        assertThat(activity.viewer.getPswdDataFromView()[2], is("currentPassword"));
    }

    @Test
    public void testOnErrorInObserver_1()
    {
        activity.runOnUiThread(() -> activity.viewer.onErrorInObserver(new UiException(new ErrorBean(USER_NOT_FOUND))));
        waitAtMost(3, SECONDS).until(isToastInView(R.string.user_email_wrong, activity));
        onView(withId(userDataAcRsId)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnErrorInObserver_2()
    {
        activity.runOnUiThread(() -> activity.viewer.onErrorInObserver(new UiException(new ErrorBean(PASSWORD_NOT_SENT))));
        waitAtMost(3, SECONDS).until(isToastInView(R.string.user_email_wrong, activity));
        onView(withId(userDataAcRsId)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnErrorInObserver_3()
    {
        activity.runOnUiThread(() -> activity.viewer.onErrorInObserver(new UiException(new ErrorBean(BAD_REQUEST))));
        waitAtMost(4, SECONDS).until(isToastInView(R.string.password_wrong, activity));
        onView(withId(pswdChangeAcRsId)).check(matches(isDisplayed()));
    }

    //    ============================  TESTS OBSERVERS  ===================================

    @Test
    public void test_PswdSendSingleObserver_Succcess()
    {
        onView(withId(R.id.password_send_ac_button)).perform(click());
        waitAtMost(6, SECONDS).until(isToastInView(R.string.password_new_in_login, activity));
        waitAtMost(6, SECONDS).until(isResourceIdDisplayed(loginAcResourceId));
    }
}