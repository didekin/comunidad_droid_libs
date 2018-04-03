package com.didekindroid.lib_one.api;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 20/02/2018
 * Time: 11:08
 */

@SuppressWarnings("unused")
public interface DrawerDecoratedIf {

    static TextView getHeaderText(NavigationView navigationView, int navHeaderViewId)
    {
        Timber.d("getHeaderText()");
        return navigationView.getHeaderView(0).findViewById(navHeaderViewId);
    }

    Viewer<DrawerLayout, ? extends Controller> getViewerDrawer();

    DrawerLayout getDrawerDecoratedView();

    int getDrawerMnRsId();

    default NavigationView getNavView(int viewId)
    {
        Timber.d("getNavView()");
        return getDrawerDecoratedView().findViewById(viewId);
    }
}
