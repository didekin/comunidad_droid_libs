package com.didekindroid.lib_one.api.router;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import timber.log.Timber;

import static com.didekindroid.lib_one.util.UiUtil.makeToast;

/**
 * User: pedro@didekin
 * Date: 08/04/2018
 * Time: 11:45
 */
public interface UiExceptionActionIf extends RouterActionIf {


    int getResourceIdForToast();

    default void handleExceptionInUi(@NonNull Activity activity)
    {
        Timber.d("handleExceptionInUi()");
        showToast(activity);
        initActivity(activity);
    }

    default void handleExceptionInUi(@NonNull Activity activity, @Nullable Bundle bundle)
    {
        Timber.d("handleExceptionInUi()");
        showToast(activity);
        initActivity(activity, bundle);
    }

    default void handleExceptionInUi(@NonNull Activity activity, @Nullable Bundle bundle, int flags)
    {
        Timber.d("handleExceptionInUi()");
        showToast(activity);
        initActivity(activity, bundle, flags);
    }

    default void showToast(@NonNull Activity activity)
    {
        Timber.d("showToast()");
        if (getResourceIdForToast() > 0) {
            makeToast(activity, getResourceIdForToast());
        }
    }
}
