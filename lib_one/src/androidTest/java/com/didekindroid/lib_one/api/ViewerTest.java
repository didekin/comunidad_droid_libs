package com.didekindroid.lib_one.api;

import android.app.Activity;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.view.View;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.security.AuthTkCacher;
import com.didekinlib.http.exception.ErrorBean;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_EXEC_A;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.cleanInitialSec;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http_Router;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSecurity;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.loginAcResourceId;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.BAD_REQUEST;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 17:51
 */
public class ViewerTest {

    private final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public IntentsTestRule<ActivityMock> activityRule = new IntentsTestRule<>(ActivityMock.class, true, true);

    private Activity activity;
    private Viewer<View, ControllerIf> viewer;
    private ViewerMock<View, ControllerIf> parentViewer;
    private View viewInViewer;

    @BeforeClass
    public static void setMore(){
        initSec_Http_Router(getTargetContext());
    }

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        parentViewer = new ViewerMock<>(new View(activity), activity);
        viewInViewer = new View(activity);
        viewer = new Viewer<>(viewInViewer, activity, parentViewer);
    }

    @AfterClass
    public static void cleanUp()
    {
        cleanInitialSec();
    }

    @Test
    public void testGetActivity()
    {
        assertThat(viewer.getActivity(), is(activity));
    }

    @Test
    public void test_OnErrorInObserver()
    {
        UiException uiException = new UiException(new ErrorBean(BAD_REQUEST)); // error -> LoginAc
        activity.runOnUiThread(() -> viewer.onErrorInObserver(uiException));
        onView(withId(loginAcResourceId)).check(matches(isDisplayed()));
    }

    @Test
    public void clearSubscriptions()
    {
        ControllerIf controller = new Controller(new AuthTkCacher(activity)) {
            @Override
            public int clearSubscriptions()
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return 0;
            }
        };
        viewer.setController(controller);
        viewer.clearSubscriptions();
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void getViewInViewer()
    {
        assertThat(viewer.getViewInViewer(), is(viewInViewer));
    }

    @Test
    public void test_GetController()
    {
        final ControllerIf controllerLocal = new Controller(new AuthTkCacher(activity));
        viewer.setController(controllerLocal);
        assertThat(viewer.getController(), is(controllerLocal));
    }

    @Test
    public void test_GetParentViewer()
    {
        assertThat(viewer.getParentViewer(), is(parentViewer));
    }
}