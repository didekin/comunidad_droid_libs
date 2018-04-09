package com.didekindroid.lib_one.testutil;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.View;

import com.didekindroid.lib_one.api.ControllerIf;
import com.didekindroid.lib_one.api.CtrlerSelectListIf;
import com.didekindroid.lib_one.api.InjectorOfParentViewerIf;
import com.didekindroid.lib_one.api.ViewerMock;
import com.didekindroid.lib_one.api.ViewerSelectListIf;
import com.didekindroid.lib_one.util.BundleKey;

import java.io.Serializable;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;

/**
 * User: pedro@didekin
 * Date: 18/02/2018
 * Time: 13:28
 */

@SuppressWarnings({"ConstantConditions", "unused"})
public class UiTestUtil {

    //    ============================= CONTROLLER/Adapters ===================================

    public static CompositeDisposable addSubscription(ControllerIf controller)
    {
        int oldNumberSubscriptions = controller.getSubscriptions().size();
        controller.getSubscriptions().add(new Disposable() {
            @Override
            public void dispose()
            {
            }

            @Override
            public boolean isDisposed()
            {
                return false;
            }
        });
        assertTrue(controller.getSubscriptions().size() == ++oldNumberSubscriptions, "subscriptions number OK");
        assertTrue(controller.getSubscriptions().size() > 0, "subscriptions > 0");
        return controller.getSubscriptions();
    }

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

    public static int focusOnView(Activity activity, int viewRsId)
    {
        final View view = activity.findViewById(viewRsId);

        activity.runOnUiThread(() -> {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
        });
        return viewRsId;
    }

    public static Menu doMockMenu(Activity activity, int menuMockRsId)
    {
        PopupMenu popupMenu = new PopupMenu(activity, null);
        Menu menu = popupMenu.getMenu();
        activity.getMenuInflater().inflate(menuMockRsId, menu);
        return menu;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void cleanTasks(Activity activity)
    {
        ActivityManager manager = (ActivityManager) activity.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> tasks = manager.getAppTasks();
        for (ActivityManager.AppTask task : tasks) {
            task.finishAndRemoveTask();
        }
    }

    //    ============================ VIEWERS ============================

    public static void checkSavedStateWithItemSelected(ViewerSelectListIf viewer, BundleKey bundleKey)
    {
        viewer.setSelectedItemId(18L);
        Bundle bundle = new Bundle(1);
        viewer.saveState(bundle);
        assertTrue(bundle.getLong(bundleKey.getKey()) == 18L, "Bundle key value OK");
    }

    public static <T extends AppCompatActivity & InjectorOfParentViewerIf> void checkChildInViewer(T activity)
    {
        final ViewerMock viewerChild = new ViewerMock(activity);
        activity.setChildInParentViewer(viewerChild);
        assertTrue(activity.getInjectedParentViewer().getChildViewer(ViewerMock.class).equals(viewerChild), "viewerChild OK");
    }
}
