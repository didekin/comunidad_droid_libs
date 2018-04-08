package com.didekindroid.lib_one.usuario.router;

import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.router.ContextualRouterIf;
import com.didekindroid.lib_one.api.router.RouterActionIf;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.checkTextsInDialog;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http_Router;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_RODRIGO;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regGetUserComu;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.usuario_object;
import static com.didekindroid.lib_one.usuario.router.UserContextAction.showPswdSentMessage;
import static com.didekindroid.lib_one.usuario.router.UserContextName.user_name_just_modified;
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

    @Before
    public void setUp()
    {
        activity = intentRule.getActivity();
        initSec_Http_Router(activity);
        router = routerInitializer.get().getContextRouter();
    }

    @Test
    public void test_user_name_just_modified() throws Exception
    {
        RouterActionIf action = router.getActionFromContextNm(user_name_just_modified);
        assertThat(action, is(showPswdSentMessage));

        Bundle bundle = new Bundle(1);
        bundle.putSerializable(usuario_object.key, regGetUserComu(comu_real_rodrigo));
        activity.runOnUiThread(() -> action.initActivity(activity, bundle, 0));
        checkTextsInDialog(R.string.receive_password_by_mail_dialog, R.string.continuar_button_rot);

        cleanOptions(CLEAN_RODRIGO);
    }
}