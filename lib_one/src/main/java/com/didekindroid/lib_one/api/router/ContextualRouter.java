package com.didekindroid.lib_one.api.router;

import android.support.annotation.NonNull;

import java.util.Map;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 14/02/2018
 * Time: 15:55
 */
@SuppressWarnings("unused")
public final class ContextualRouter implements ContextualRouterIf {

    private final Map<ContextualNameIf, RouterActionIf> contextualActionMap;

    public ContextualRouter(Map<ContextualNameIf, RouterActionIf> contextualAcMap)
    {
        contextualActionMap = contextualAcMap;
    }

    @Override
    public RouterActionIf getActionFromContextNm(@NonNull ContextualNameIf contextualName)
    {
        Timber.d("getActionFromContextNm()");
        return contextualActionMap.get(contextualName);
    }
}
