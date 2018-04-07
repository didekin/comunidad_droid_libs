package com.didekindroid.lib_one.api;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekindroid.lib_one.api.router.RouterActionIf;
import com.didekindroid.lib_one.api.router.UiExceptionRouterIf;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initRouter;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 29/05/17
 * Time: 15:34
 */
@RunWith(AndroidJUnit4.class)
public class ParentViewerTest {

    @Rule
    public IntentsTestRule<ActivityMock> activityRule = new IntentsTestRule<>(ActivityMock.class, true, true);

    private ActivityMock activity;
    private ParentViewer<View, Controller> parentViewer;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        initRouter();

        parentViewer = new ParentViewer<View, Controller>(null, activity, null) {
            @Override
            public UiExceptionRouterIf getExceptionRouter()
            {
                return httpMsg -> (RouterActionIf) () -> ActivityNextMock.class;
            }
        };
    }

    @Test
    public void test_SetChildViewer()
    {
        final ViewerMock childViewer = new ViewerMock(activity);
        parentViewer.setChildViewer(childViewer);
        assertThat(parentViewer.getChildViewer(ViewerMock.class), is(childViewer));
    }

    @Test
    public void test_GetChildViewersFromSuperClass()
    {
        final ViewerMock childViewer = new ViewerMock(activity);
        parentViewer.setChildViewer(childViewer);
        final Viewer<View, Controller> childViewer2 = new Viewer<View, Controller>(null, activity, null) {
            @Override
            public UiExceptionRouterIf getExceptionRouter()
            {
                return httpMsg -> (RouterActionIf) () -> ActivityNextMock.class;
            }
        };
        parentViewer.setChildViewer(childViewer2);
        assertThat(parentViewer.getChildViewersFromSuperClass(ViewerIf.class).size(), is(2));
    }
}