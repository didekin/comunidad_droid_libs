package com.didekindroid.lib_one.usuario.router;

import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.router.ContextualRouterIf;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.checkTextsInDialog;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.isResourceIdDisplayed;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.cleanInitialSec;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http_Router;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_RODRIGO;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetUser;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.loginAcResourceId;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.usuario_object;
import static com.didekindroid.lib_one.usuario.router.UserContextAction.login_from_default;
import static com.didekindroid.lib_one.usuario.router.UserContextAction.login_from_user;
import static com.didekindroid.lib_one.usuario.router.UserContextAction.showPswdSentMessage;
import static com.didekindroid.lib_one.usuario.router.UserContextName.default_reg_user;
import static com.didekindroid.lib_one.usuario.router.UserContextName.pswd_just_sent_to_user;
import static com.didekindroid.lib_one.usuario.router.UserContextName.user_name_just_modified;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 19/02/2018
 * Time: 15:08
 */
@RunWith(AndroidJUnit4.class)
public class UserContextActionTest {

    @Rule
    public IntentsTestRule<ActivityMock> intentRule = new IntentsTestRule<>(ActivityMock.class, true, true);
    private ActivityMock activity;
    private ContextualRouterIf router;

    @BeforeClass
    public static void setMore()
    {
        initSec_Http_Router(getTargetContext());
    }

    @Before
    public void setUp()
    {
        activity = intentRule.getActivity();
        router = routerInitializer.get().getContextRouter();
    }

    @AfterClass
    public static void cleanUp()
    {
        cleanInitialSec();
    }

    // ---------------------------------------------------------------------------------------------------

    @Test
    public void testShowPswdSentMessage() throws Exception
    {
        // Preconditions.
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(usuario_object.key, regComuUserUserComuGetUser(comu_real_rodrigo));

        assertThat(router.getActionFromContextNm(user_name_just_modified), is(showPswdSentMessage));
        activity.runOnUiThread(() -> showPswdSentMessage.initActivity(activity, bundle, 0));
        checkTextsInDialog(R.string.receive_password_by_mail_dialog, R.string.continuar_button_rot);

        cleanOptions(CLEAN_RODRIGO);
    }

    @Test
    public void testLogin_from_user()
    {
        assertThat(router.getActionFromContextNm(pswd_just_sent_to_user), is(login_from_user));
        activity.runOnUiThread(() -> login_from_user.initActivity(activity));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(loginAcResourceId));
    }

    @Test
    public void testLogin_from_default()
    {
        assertThat(router.getActionFromContextNm(default_reg_user), is(login_from_default));
        activity.runOnUiThread(() -> login_from_default.initActivity(activity));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(loginAcResourceId));
    }
}