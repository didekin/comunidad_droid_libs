package com.didekindroid.lib_one.api.router;

import android.support.annotation.NonNull;

import java.util.Map;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 10/02/2018
 * Time: 14:36
 */
@SuppressWarnings("unused")
public final class UiExceptionRouter implements UiExceptionRouterIf {

    private final Map<String, UiExceptionActionIf> actionMap;

    public UiExceptionRouter(Map<String, UiExceptionActionIf> actionMap)
    {
        this.actionMap = actionMap;
    }

    @Override
    public UiExceptionActionIf getActionFromMsg(@NonNull String httpMsg)
    {
        Timber.d("getActionFromMsg()");
        return actionMap.get(httpMsg);
    }
}
