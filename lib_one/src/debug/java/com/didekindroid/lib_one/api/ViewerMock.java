package com.didekindroid.lib_one.api;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 24/02/17
 * Time: 17:23
 */
@SuppressWarnings("WeakerAccess")
public class ViewerMock<T extends View, C extends ControllerIf> extends Viewer<T, C> implements
        SpinnerEventListener {

    public ViewerMock(Activity activity)
    {
        this(null, activity);
    }

    public ViewerMock(T view,Activity activity)
    {
        super(view, activity, null);
    }

    @Override
    public int clearSubscriptions()
    {
        return 0;
    }

    @Override
    public void doOnClickItemId(@NonNull SpinnerEventItemSelectIf spinnerEventItemSelect)
    {
        Timber.d("doOnClickItemId()");
    }
}
