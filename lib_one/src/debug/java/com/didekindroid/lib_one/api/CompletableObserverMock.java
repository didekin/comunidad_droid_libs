package com.didekindroid.lib_one.api;

import io.reactivex.observers.DisposableCompletableObserver;
import timber.log.Timber;

public class CompletableObserverMock extends DisposableCompletableObserver {

    @Override
    public void onComplete()
    {
    }

    @Override
    public void onError(Throwable e)
    {
        dispose();
        Timber.d("============= %s =============", e.getClass().getName());
    }
}
