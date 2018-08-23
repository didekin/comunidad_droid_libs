package com.didekindroid.lib_one.usuario;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.usuario.ViewerUserDataIf.UserChangeToMake;
import com.didekindroid.lib_one.usuario.dao.CtrlerUsuario;
import com.didekinlib.http.exception.ErrorBean;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.checkTextsInDialog;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.isResourceIdDisplayed;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.isToastInView;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.isViewDisplayed;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http_Router;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetUser;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.userDataAcRsId;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.lib_one.usuario.ViewerUserDataIf.UserChangeToMake.alias_only;
import static com.didekindroid.lib_one.usuario.ViewerUserDataIf.UserChangeToMake.nothing;
import static com.didekindroid.lib_one.usuario.ViewerUserDataIf.UserChangeToMake.userName;
import static com.didekindroid.lib_one.usuario.testutil.UserEspressoTestUtil.typeUserNameAliasPswd;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.BAD_REQUEST;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 25/03/17
 * Time: 14:16
 */
@RunWith(AndroidJUnit4.class)
public class ViewerUserDataTest {

    private Usuario usuario;

    @Rule
    public ActivityTestRule<UserDataAc> activityRule = new ActivityTestRule<UserDataAc>(UserDataAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                initSec_Http_Router(getTargetContext());
                usuario = regComuUserUserComuGetUser(comu_real_rodrigo);
            } catch (Exception e) {
                fail();
            }
            return new Intent().putExtra(user_name.key, usuario.getUserName());
        }
    };

    private UserDataAc activity;
    private boolean isClean;

    @Before
    public void setUp()
    {
        isClean = false;
        activity = activityRule.getActivity();
        waitAtMost(4, SECONDS).until(() -> activity.viewer != null);
    }

    @After
    public void cleanUp()
    {
        if (isClean) {
            return;
        }
        cleanOneUser(user_crodrigo.getUserName());
    }

    // ============================================================
    //    .................... TESTS ....................
    // ============================================================

    @Test
    public void testDoViewInViewer()
    {
        // test_NewViewerUserData.
        waitAtMost(4, SECONDS).untilAtomic(activity.viewer.getOldUser(), is(usuario));

        assertThat(activity.viewer.getEmailView(), notNullValue());
        assertThat(activity.viewer.getAliasView(), notNullValue());
        assertThat(activity.viewer.getPasswordView(), notNullValue());
        assertThat(activity.viewer.getController(), instanceOf(CtrlerUsuario.class));
        assertThat(activity.viewer.getUsuarioBean(), notNullValue());
        assertThat(activity.viewer.getOldUser(), notNullValue());
        assertThat(activity.viewer.getNewUser(), notNullValue());

        checkUserDataLoaded();

        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(userDataAcRsId));
        waitAtMost(4, SECONDS).until(isViewDisplayed(allOf(withId(R.id.reg_usuario_email_editT), withText(containsString(usuario.getUserName())))));
        waitAtMost(4, SECONDS).until(isViewDisplayed(allOf(withId(R.id.reg_usuario_alias_ediT), withText(containsString(usuario.getAlias())))));
        waitAtMost(4, SECONDS).until(isViewDisplayed(
                allOf(
                        withId(R.id.password_validation_ediT),
                        withText(containsString("")),
                        withHint(R.string.user_data_ac_password_hint)
                )));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(R.id.user_data_modif_button));
    }

    @Test
    public void testProcessBackUserDataLoaded()
    {
        activity.runOnUiThread(() -> {
            activity.viewer.processBackUserDataLoaded(usuario);
            checkUserDataLoaded();
        });
    }

    @Test
    public void testCheckUserData_1() throws InterruptedException
    {
        SECONDS.sleep(2);

        typeUserNameAliasPswd("newuser@user.com", user_crodrigo.getAlias(), user_crodrigo.getPassword());
        runCheckUserData(true);
        Usuario usuario = activity.viewer.getUsuarioBean().get().getUsuario();

        assertThat(usuario.getUserName(), is("newuser@user.com"));
        assertThat(usuario.getAlias(), is(user_crodrigo.getAlias()));
        assertThat(usuario.getPassword(), is(user_crodrigo.getPassword()));
    }

    @Test
    public void testCheckUserData_2() throws InterruptedException
    {
        SECONDS.sleep(2);

        typeUserNameAliasPswd("wrong_newuser.com", user_crodrigo.getAlias(), user_crodrigo.getPassword());
        runCheckUserData(false);
        waitAtMost(6, SECONDS).until(isToastInView(R.string.email_hint, activity));
    }

    @Test
    public void testWhatDataChangeToMake()
    {
        // Caso 1: datos de entrada (usuarioBean) == oldUser.
        activity.viewer.getOldUser().set(new Usuario.UsuarioBuilder().alias(user_crodrigo.getAlias()).userName(user_crodrigo.getUserName()).build());
        typeUserNameAliasPswd(user_crodrigo.getUserName(), user_crodrigo.getAlias(), user_crodrigo.getPassword());
        runWhatDataChange(nothing);

        // Caso 2: datos de entrada userName == oldUser.userName.
        typeUserNameAliasPswd(user_crodrigo.getUserName(), "new_alias", user_crodrigo.getPassword());
        runWhatDataChange(alias_only);

        // Caso 3: datos de entrada userName != oldUser.userName.
        typeUserNameAliasPswd("new@userName.com", user_crodrigo.getAlias(), user_crodrigo.getPassword());
        runWhatDataChange(userName);

        // Caso 4: datos de entrada userName != oldUser.
        typeUserNameAliasPswd("new@userName.com", "new_alias", user_crodrigo.getPassword());
        runWhatDataChange(userName);
    }

    @Test
    public void testModifyUserData_1()
    {
        // No change.
        waitAtMost(6, SECONDS).untilAtomic(activity.viewer.getOldUser(), is(usuario));
        activity.runOnUiThread(() -> activity.viewer.modifyUserData(nothing));
        waitAtMost(3, SECONDS).until(isToastInView(R.string.no_user_data_to_be_modified, activity));
    }

    @Test
    public void testModifyUserData_2()
    {
        // Datos de entrada userName != oldUser.userName.
        waitAtMost(6, SECONDS).untilAtomic(activity.viewer.getOldUser(), is(usuario));
        activity.viewer.getOldUser().set(new Usuario.UsuarioBuilder().copyUsuario(usuario).password(user_crodrigo.getPassword()).build());
        activity.viewer.getNewUser().set(new Usuario.UsuarioBuilder().copyUsuario(activity.viewer.getOldUser().get()).userName(USER_DROID.getUserName()).build());
        activity.runOnUiThread(() -> activity.viewer.modifyUserData(userName));
        checkTextsInDialog(R.string.receive_password_by_mail_dialog, R.string.continuar_button_rot);

        cleanOneUser(USER_DROID.getUserName());
        isClean = true;
    }

    @Test
    public void testProcessControllerError_1()
    {
        activity.runOnUiThread(() -> activity.viewer.onErrorInObserver(new UiException(new ErrorBean(BAD_REQUEST))));
        waitAtMost(3, SECONDS).until(isToastInView(R.string.password_wrong, activity));
        onView(withId(userDataAcRsId)).check(matches(isDisplayed()));
    }

    // ============================================================
    //    .................... Helpers ....................
    // ============================================================

    public void checkUserDataLoaded()
    {
        assertThat(activity.viewer.getOldUser().get(), is(usuario));
        assertThat(activity.viewer.getEmailView().getText().toString(), is(usuario.getUserName()));
        assertThat(activity.viewer.getAliasView().getText().toString(), is(usuario.getAlias()));
        assertThat(activity.viewer.getPasswordView().getHint(), is(activity.getText(R.string.user_data_ac_password_hint)));
    }

    public void runCheckUserData(final boolean isOk)
    {
        final AtomicBoolean isChecked = new AtomicBoolean(!isOk);
        activity.runOnUiThread(() -> {
            boolean isUserDataOk = activity.viewer.checkUserData();
            isChecked.compareAndSet(!isOk, isUserDataOk);
        });
        waitAtMost(6, SECONDS).untilAtomic(isChecked, is(isOk));
    }

    public void runWhatDataChange(final UserChangeToMake changeToMake)
    {
        final AtomicReference<UserChangeToMake> atomicChange = new AtomicReference<>(null);
        activity.runOnUiThread(() -> {
            activity.viewer.checkUserData();
            activity.viewer.whatDataChangeToMake();
            atomicChange.compareAndSet(null, changeToMake);
        });
        waitAtMost(6, SECONDS).untilAtomic(atomicChange, is(changeToMake));
    }
}