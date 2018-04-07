package com.didekindroid.lib_one.api;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.security.TokenIdentityCacher;
import com.didekinlib.http.exception.ErrorBean;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.lib_one.security.SecurityTestUtils.doSpringOauthToken;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_WITH_EXCEPTION_EXEC;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initRouter;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.BAD_REQUEST;
import static io.reactivex.Completable.error;
import static io.reactivex.Completable.fromSingle;
import static io.reactivex.Single.just;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 30/05/17
 * Time: 12:42
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class ObserverCacheCleanerTest {

    private final AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    private ActivityMock activity;
    private Viewer<?, Controller> viewer;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        initRouter();

        viewer = new Viewer<View, Controller>(null, activity, null) {
            @Override
            public void onErrorInObserver(Throwable error)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_WITH_EXCEPTION_EXEC), is(BEFORE_METHOD_EXEC));
            }
        };
        viewer.setController(new Controller(new TokenIdentityCacher(activity)));
    }

    @Test
    public void test_OnComplete()
    {
        assertThat(fromSingle(just("hola")).subscribeWith(new ObserverCacheCleaner(viewer)).isDisposed(), is(true));
    }

    @Test
    public void test_OnError()
    {
        // Preconditions.
        viewer.getController().getTkCacher().initIdentityCache(doSpringOauthToken());

        activity.runOnUiThread(() -> {
            assertThat(error(new UiException(new ErrorBean(BAD_REQUEST))).subscribeWith(new ObserverCacheCleaner(viewer)).isDisposed(), is(true));
            // Borra caché.
            assertThat(viewer.getController().getTkCacher().getTokenCache().get(), nullValue());
            // Call to viewer's method.
            assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_WITH_EXCEPTION_EXEC));
        });
    }
}