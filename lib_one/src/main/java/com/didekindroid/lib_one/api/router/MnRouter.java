package com.didekindroid.lib_one.api.router;

import java.util.Map;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 12/02/2018
 * Time: 19:50
 */

@SuppressWarnings("unused")
public final class MnRouter implements MnRouterIf {

    private final Map<Integer, MnRouterActionIf> mnActionMap;

    public MnRouter(Map<Integer, MnRouterActionIf> mnActionMap)
    {
        this.mnActionMap = mnActionMap;
    }

    @Override
    public RouterActionIf getActionFromMnItemId(int menuItemRsId)
    {
        Timber.d("getActionFromMnItemId()");
        return mnActionMap.get(menuItemRsId);
    }
}
