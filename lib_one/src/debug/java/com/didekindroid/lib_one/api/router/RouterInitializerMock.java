package com.didekindroid.lib_one.api.router;

import android.app.Activity;

/**
 * User: pedro@didekin
 * Date: 18/02/2018
 * Time: 13:41
 */
public class RouterInitializerMock implements RouterInitializerIf {

    @Override
    public UiExceptionRouterIf getExceptionRouter()
    {
        return null;
    }

    @Override
    public MnRouterIf getMnRouter()
    {
        return null;
    }

    @Override
    public ContextualRouterIf getContextRouter()
    {
        return null;
    }

    @Override
    public Class<? extends Activity> getDefaultAc()
    {
        return null;
    }
}
