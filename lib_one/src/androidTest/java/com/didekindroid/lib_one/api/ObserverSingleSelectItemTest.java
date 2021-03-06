package com.didekindroid.lib_one.api;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Spinner;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.exception.ErrorBean;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;
import io.reactivex.functions.Function;

import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_EXEC_A;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_WITH_EXCEPTION_EXEC;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initRouterAll;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.BAD_REQUEST;
import static io.reactivex.Single.just;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 30/05/17
 * Time: 12:55
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
@RunWith(AndroidJUnit4.class)
public class ObserverSingleSelectItemTest {

    private final AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);
    private ObserverSingleSelectItem<ViewerSelectList<Spinner, CtrlerSelectList<String>, String>, String> observer;

    @Before
    public void setUp()
    {
        final Activity activity = activityRule.getActivity();
        initRouterAll();

        activity.runOnUiThread(() -> observer = new ObserverSingleSelectItem<>(
                new ViewerSelectList<Spinner, CtrlerSelectList<String>, String>(new Spinner(activity), activity, null) {
                    @Override
                    public void initSelectedItemId(Bundle savedState)
                    {
                    }

                    @Override
                    public Function<String, Long> getBeanIdFunction()
                    {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public void onSuccessLoadSelectedItem(@NonNull Bundle bundle)
                    {
                        assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                    }

                    @Override
                    public void onErrorInObserver(Throwable error)
                    {
                        assertThat(flagMethodExec.getAndSet(AFTER_METHOD_WITH_EXCEPTION_EXEC), is(BEFORE_METHOD_EXEC));
                    }
                }
        ));
        waitAtMost(2, SECONDS).until(() -> observer != null);
    }

    @Test
    public void test_OnSuccess()
    {
        just(new Bundle(0)).subscribeWith(observer);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void test_OnError()
    {
        Single.<Bundle>error(new UiException(new ErrorBean(BAD_REQUEST))).subscribeWith(observer);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_WITH_EXCEPTION_EXEC));
    }
}