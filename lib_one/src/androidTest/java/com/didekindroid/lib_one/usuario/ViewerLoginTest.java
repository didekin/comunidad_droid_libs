package com.didekindroid.lib_one.usuario;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.usuario.ViewerLogin.PasswordMailDialog;
import com.didekindroid.lib_one.usuario.dao.CtrlerUsuario;
import com.didekinlib.http.exception.ErrorBean;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_EXEC_A;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.checkTextsInDialog;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.isToastInView;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.isViewDisplayedAndPerform;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http_Router;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_DROID;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.loginAcResourceId;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.login_counter_atomic_int;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.usuario_object;
import static com.didekindroid.lib_one.usuario.ViewerLogin.PasswordMailDialog.newInstance;
import static com.didekindroid.lib_one.usuario.testutil.UserEspressoTestUtil.typeLoginData;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/03/17
 * Time: 19:15
 */
@RunWith(AndroidJUnit4.class)
public class ViewerLoginTest {

    private static final AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public IntentsTestRule<? extends Activity> activityRule = new IntentsTestRule<LoginAc>(LoginAc.class, true, true) {
        @Override
        protected void beforeActivityLaunched()
        {
            initSec_Http_Router(getTargetContext());
        }
    };

    private LoginAc activity;

    @Before
    public void setUp()
    {
        activity = (LoginAc) activityRule.getActivity();
    }

    @Test
    public void testNewViewerLogin()
    {
        assertThat(activity.viewerLogin, notNullValue());
        assertThat(activity.viewerLogin.getController(), instanceOf(CtrlerUsuario.class));
    }

    @Test
    public void testDoViewInViewer_1()
    {
        Bundle bundle = new Bundle(1);
        bundle.putInt(login_counter_atomic_int.key, 2);
        // Precondition.
        assertThat(activity.viewerLogin.getCounterWrong().get(), is(0));
        // Execute.
        activity.viewerLogin.doViewInViewer(bundle, null);
        // Check.
        assertThat(activity.viewerLogin.getCounterWrong().get(), is(2));
    }

    @Test
    public void testDoViewInViewer_2()
    {
        // Execute.
        activity.runOnUiThread(() -> {
            activity.viewerLogin.doViewInViewer(new Bundle(0), USER_DROID.getUserName());
            // Check.
            assertThat(((EditText) activity.viewerLogin.getViewInViewer().findViewById(R.id.reg_usuario_email_editT))
                    .getText().toString(), is(USER_DROID.getUserName()));
        });
    }

    @Test
    public void test_CheckEmailData()
    {
        typeLoginData(USER_DROID.getUserName(), null);
        assertThat(activity.viewerLogin.checkEmailData(), is(true));
    }

    @Test  // Validation: error message.
    public void testCheckLoginData_1()
    {
        typeLoginData("user_wrong", "psw");
        activity.runOnUiThread(() -> activity.viewerLogin.checkLoginData());
        waitAtMost(4, SECONDS).until(isToastInView(R.string.error_validation_msg, activity,
                R.string.email_hint, R.string.password));
    }

    @Test   // Validation OK
    public void testCheckLoginData_2()
    {
        typeLoginData(USER_DROID.getUserName(), USER_DROID.getPassword());
        assertThat(activity.viewerLogin.checkLoginData(), is(true));
    }

    @Test
    public void testGetLoginDataFromView()
    {
        typeLoginData(USER_DROID.getUserName(), USER_DROID.getPassword());
        assertThat(activity.viewerLogin.getLoginDataFromView()[0], is(USER_DROID.getUserName()));
        assertThat(activity.viewerLogin.getLoginDataFromView()[1], is(USER_DROID.getPassword()));
    }

    @Test
    public void test_ShowDialogAfterErrors()
    {
        userBeanPreconditions();
        // Exec.
        activity.viewerLogin.showDialogAfterErrors();
        // Check.
        checkTextsInDialog(R.string.send_password_by_mail_dialog, R.string.send_password_by_mail_YES);
    }

    @Test   // Login NO ok, counterWrong > 3.
    public void testProcessLoginBackInView_1()
    {
        // Precondition.
        activity.viewerLogin.getCounterWrong().set(3);
        userBeanPreconditions();
        // Exec.
        activity.runOnUiThread(() -> activity.viewerLogin.processLoginBackInView(false));
        // Check.
        waitAtMost(3, SECONDS).untilAtomic(activity.viewerLogin.getCounterWrong(), equalTo(4));
        checkTextsInDialog(R.string.send_password_by_mail_dialog, R.string.send_password_by_mail_YES);
    }

    @Test   // Login NO ok, counterWrong <= 3.
    public void testProcessLoginBackInView_2()
    {
        // Precondition.
        activity.viewerLogin.getCounterWrong().set(2);
        // Exec.
        activity.runOnUiThread(() -> activity.viewerLogin.processLoginBackInView(false));
        // Check.
        waitAtMost(5, SECONDS).untilAtomic(activity.viewerLogin.getCounterWrong(), equalTo(3));
        waitAtMost(5, SECONDS).until(isToastInView(R.string.password_wrong, activity));
        onView(withId(loginAcResourceId)).check(matches(isDisplayed()));
    }

    @Test
    public void testDoDialogPositiveClick_1()
    {
        activity.viewerLogin.setController(new CtrlerUsuario() {
            @Override
            public boolean sendNewPassword(@NonNull DisposableSingleObserver<Boolean> observer, @NonNull Usuario usuario)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return false;
            }
        });
        activity.viewerLogin.doDialogPositiveClick(USER_DROID);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void testDoDialogPositiveClick_2()
    {
        activity.runOnUiThread(() -> activity.viewerLogin.doDialogPositiveClick(null));
        waitAtMost(4, SECONDS).until(isToastInView(R.string.username_wrong_in_login, activity));
    }

    @Test
    public void testProcessBackSendPswdInView()
    {
        activity.runOnUiThread(() -> activity.viewerLogin.processBackSendPswdInView(true));
        waitAtMost(4, SECONDS).until(isToastInView(R.string.password_new_in_login, activity));
    }

    // =========================  LyfeCicle  =========================

    @Test
    public void testSaveState()
    {
        // Previous state.
        Bundle bundle = new Bundle(1);
        bundle.putInt(login_counter_atomic_int.key, 2);
        activity.viewerLogin.doViewInViewer(bundle, null);
        assertThat(activity.viewerLogin.getCounterWrong().get(), is(2));
        // Execute and checkAppBarMenu.
        activity.viewerLogin.saveState(bundle);
        assertThat(bundle.getInt(login_counter_atomic_int.key), is(2));
    }

    // =========================  Help button  =========================

    @Test
    public void test_HelpButton_1()
    {
        // Precondition.
        typeLoginData(USER_DROID.getUserName(), null);
        waitAtMost(4, SECONDS).until(isViewDisplayedAndPerform(withId(R.id.login_help_fab), click()));
        // Check.
        checkTextsInDialog(R.string.send_password_by_mail_dialog, R.string.send_password_by_mail_YES);
    }

    @Test
    public void test_HelpButton_2()
    {
        // Precondition.
        typeLoginData(USER_DROID.getUserName(), USER_DROID.getPassword());
        waitAtMost(4, SECONDS).until(isViewDisplayedAndPerform(withId(R.id.login_help_fab), click()));
        // Check.
        checkTextsInDialog(R.string.send_password_by_mail_dialog, R.string.send_password_by_mail_YES);
    }

    // ============================================================
    // ....................... SUBSCRIBERS ...................
    // ============================================================

    @Test
    public void testOnErrorInObserver_1()
    {
        activity.runOnUiThread(() -> activity.viewerLogin.onErrorInObserver(new UiException(new ErrorBean(USER_NAME_NOT_FOUND))));
        waitAtMost(3, SECONDS).until(isToastInView(R.string.username_wrong_in_login, activity));
        onView(withId(loginAcResourceId)).check(matches(isDisplayed()));
    }

    // ============================================================
    // ....................... Inner classes ...................
    // ============================================================

    @Test
    public void test_NewInstance()
    {
        UsuarioBean usuarioBean = new UsuarioBean("email@mail.es", "alias", "password", "password");
        usuarioBean.validateLoginData(activity.getResources(), new StringBuilder(0));
        PasswordMailDialog dialog = newInstance(usuarioBean, activity.viewerLogin);
        assertThat(dialog.getArguments().getSerializable(usuario_object.key), notNullValue());
    }

    // =========================  HELPERS  =========================

    private void userBeanPreconditions()
    {
        activity.viewerLogin.usuarioBean.compareAndSet(null, new UsuarioBean(USER_DROID.getUserName(), null, USER_DROID.getPassword(), null));
        activity.viewerLogin.usuarioBean.get().validateLoginData(getTargetContext().getResources(), new StringBuilder(0));
    }
}
