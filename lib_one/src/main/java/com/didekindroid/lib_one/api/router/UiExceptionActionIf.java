package com.didekindroid.lib_one.api.router;

import android.app.Activity;
import android.support.annotation.NonNull;

import timber.log.Timber;

import static com.didekindroid.lib_one.util.UiUtil.makeToast;

/**
 * User: pedro@didekin
 * Date: 08/04/2018
 * Time: 11:45
 */
public interface UiExceptionActionIf extends RouterActionIf {


    int getResourceIdForToast();

    void handleExceptionInUi(@NonNull Activity activity);

    default void showToast(@NonNull Activity activity)
    {
        Timber.d("showToast()");
        if (getResourceIdForToast() > 0) {
            makeToast(activity, getResourceIdForToast());
        }
    }
}
