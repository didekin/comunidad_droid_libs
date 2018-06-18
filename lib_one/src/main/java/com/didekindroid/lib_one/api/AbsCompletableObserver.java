package com.didekindroid.lib_one.api;

import io.reactivex.observers.DisposableCompletableObserver;
import timber.log.Timber;

public abstract class AbsCompletableObserver extends DisposableCompletableObserver {

    private final ViewerIf viewer;

    public AbsCompletableObserver(ViewerIf viewer)
    {
        this.viewer = viewer;
    }

    @Override
    public void onError(Throwable e)
    {
        Timber.d("onError()");
        viewer.onErrorInObserver(e);
    }
}
