package com.didekindroid.lib_one.api.router;

/**
 * User: pedro@didekin
 * Date: 14/02/2018
 * Time: 15:39
 */

public interface RouterInitializerIf {

    UiExceptionRouterIf getExceptionRouter();

    MnRouterIf getMnRouter();

    ContextualRouterIf getContextRouter();
}
