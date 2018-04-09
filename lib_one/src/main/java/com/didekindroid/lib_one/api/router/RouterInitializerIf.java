package com.didekindroid.lib_one.api.router;

import android.app.Activity;

/**
 * User: pedro@didekin
 * Date: 14/02/2018
 * Time: 15:39
 */

public interface RouterInitializerIf {

    UiExceptionRouterIf getExceptionRouter();

    MnRouterIf getMnRouter();

    ContextualRouterIf getContextRouter();

    Class<? extends Activity> getDefaultAc();
}
