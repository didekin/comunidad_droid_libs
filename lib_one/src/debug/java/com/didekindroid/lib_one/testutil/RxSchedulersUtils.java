package com.didekindroid.lib_one.testutil;

import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;

import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static io.reactivex.schedulers.Schedulers.single;
import static io.reactivex.schedulers.Schedulers.trampoline;

/**
 * User: pedro@didekin
 * Date: 25/01/17
 * Time: 11:52
 */
public class RxSchedulersUtils {

    public static void trampolineReplaceIoScheduler()
    {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> trampoline());
    }

    public static void trampolineReplaceAndroidMain()
    {
        RxAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> trampoline());
    }

    public static void trampolineReplaceIoMain()
    {
        trampolineReplaceAndroidMain();
        trampolineReplaceIoScheduler();
    }

    public static void singleReplaceAndroidMain()
    {
        RxAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> single());
    }

    public static void singleReplaceIoScheduler()
    {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> single());
    }

    public static void resetAllSchedulers()
    {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    public static void execCheckSchedulersTest(boolean isSubscribed)
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertTrue(isSubscribed, MockTestConstant.subscription_added_in_observer_ok);
        } finally {
            resetAllSchedulers();
        }
    }
}
