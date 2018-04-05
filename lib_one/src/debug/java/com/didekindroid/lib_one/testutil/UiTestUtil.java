package com.didekindroid.lib_one.testutil;

import android.os.Bundle;

import com.didekindroid.lib_one.api.CtrlerSelectListIf;
import com.didekindroid.lib_one.api.ViewerSelectListIf;
import com.didekindroid.lib_one.util.BundleKey;

import java.io.Serializable;
import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;

import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;

/**
 * User: pedro@didekin
 * Date: 18/02/2018
 * Time: 13:28
 */

@SuppressWarnings("ConstantConditions")
public class UiTestUtil {

    public static <E extends Serializable> void checkSpinnerCtrlerLoadItems(CtrlerSelectListIf<E> controller, Long... entityId)
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertTrue(controller.loadItemsByEntitiyId(new DisposableSingleObserver<List<E>>() {
                @Override
                public void onSuccess(List<E> es)
                {
                }

                @Override
                public void onError(Throwable e)
                {
                    throw new AssertionError();
                }
            }, entityId), "LoadedItems");
        } finally {
            resetAllSchedulers();
        }
        assertTrue(controller.getSubscriptions().size() == 1, "subscriptions size OK");
    }

    public static void checkSavedStateWithItemSelected(ViewerSelectListIf viewer, BundleKey bundleKey)
    {
        viewer.setSelectedItemId(18L);
        Bundle bundle = new Bundle(1);
        viewer.saveState(bundle);
        assertTrue(bundle.getLong(bundleKey.getKey()) == 18L, "Bundle key value OK");
    }
}
