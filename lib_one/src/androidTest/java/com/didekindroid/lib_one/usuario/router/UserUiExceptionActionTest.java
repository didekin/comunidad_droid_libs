package com.didekindroid.lib_one.usuario.router;

import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.api.router.UiExceptionRouterIf;
import com.didekinlib.http.exception.ErrorBean;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasFlag;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.security.AuthTkCacher.AuthTkCacherExceptionMsg.AUTH_HEADER_WRONG;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.isResourceIdDisplayed;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.isToastInView;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http_Router;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_RODRIGO;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetAuthTk;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.loginAcResourceId;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.userDataAcRsId;
import static com.didekindroid.lib_one.usuario.router.UserUiExceptionAction.show_login_noUser;
import static com.didekindroid.lib_one.usuario.router.UserUiExceptionAction.show_login_no_authHeader;
import static com.didekindroid.lib_one.usuario.router.UserUiExceptionAction.show_login_unauthorized;
import static com.didekindroid.lib_one.usuario.router.UserUiExceptionAction.show_userData_wrongMail;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.PASSWORD_NOT_SENT;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.TOKEN_ENCRYP_DECRYP_ERROR;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USERCOMU_WRONG_INIT;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;

/**
 * User: pedro@didekin
 * Date: 09/04/2018
 * Time: 19:08
 */
public class UserUiExceptionActionTest {

    @Rule
    public IntentsTestRule<ActivityMock> intentRule = new IntentsTestRule<ActivityMock>(ActivityMock.class, true, true) {
        @Override
        protected void beforeActivityLaunched()
        {
            initSec_Http_Router(getTargetContext());
        }
    };
    private ActivityMock activity;
    private UiExceptionRouterIf router;

    @Before
    public void setUp()
    {
        activity = intentRule.getActivity();
        router = routerInitializer.get().getExceptionRouter();
    }

    // ============================================================
    //    ................ TESTS ..............
    // ============================================================

    @Test
    public void test_show_login_noUser()
    {
        final UiException ue = new UiException(new ErrorBean(USERCOMU_WRONG_INIT));
        run(ue, show_login_noUser, loginAcResourceId);
    }

    @Test
    public void test_show_login_tokenNull()
    {
        final UiException ue = new UiException(new ErrorBean(TOKEN_ENCRYP_DECRYP_ERROR));
        run(ue, show_login_unauthorized, loginAcResourceId);
    }

    @Test
    public void test_show_login_unauthorized()
    {
        final UiException ue = new UiException(new ErrorBean(AUTH_HEADER_WRONG));
        run(ue, show_login_no_authHeader, loginAcResourceId);
    }

    @Test
    public void test_show_userData_wrongMail()
    {
        // Preconditions.
        regComuUserUserComuGetAuthTk(comu_real_rodrigo);
        final UiException ue = new UiException(new ErrorBean(PASSWORD_NOT_SENT));
        run(ue, show_userData_wrongMail, userDataAcRsId);

        cleanOptions(CLEAN_RODRIGO);
    }

    // ============================  Helpers ==============================

    private void run(UiException ue, UserUiExceptionAction uiExceptionAction, int checkLayout)
    {
        activity.runOnUiThread(() -> router.getActionFromMsg(ue.getErrorHtppMsg()).initActivity(activity));
        check(uiExceptionAction, checkLayout);
    }

    private void check(UserUiExceptionAction uiExceptionAction, int checkLayout)
    {
        waitAtMost(10, SECONDS).until(isToastInView(uiExceptionAction.getResourceIdForToast(), activity));
        waitAtMost(8, SECONDS).until(isResourceIdDisplayed(checkLayout));
        intended(hasFlag(FLAG_ACTIVITY_NEW_TASK));
    }
}