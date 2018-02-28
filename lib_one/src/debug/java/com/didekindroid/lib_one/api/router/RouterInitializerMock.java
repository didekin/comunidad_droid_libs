package com.didekindroid.lib_one.api.router;

import com.didekindroid.lib_one.api.ActivityNextMock;

/**
 * User: pedro@didekin
 * Date: 18/02/2018
 * Time: 13:41
 */
public class RouterInitializerMock implements RouterInitializerIf {

    @Override
    public UiExceptionRouterIf getExceptionRouter()
    {
        return httpMsg -> (RouterActionIf) () -> ActivityNextMock.class;
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
}
