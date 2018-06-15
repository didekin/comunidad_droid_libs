package com.didekindroid.lib_one.api;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.lib_one.util.UiUtil.assertTrue;

/**
 * User: pedro@didekin
 * Date: 06/06/17
 * Time: 13:14
 */

public class SingleObserverMock<T> extends DisposableSingleObserver<T> {

    @Override
    public void onSuccess(T t)
    {
        assertTrue(t != null, "Succes value not null");
    }

    @Override
    public void onError(Throwable e)
    {
        dispose();
        Timber.d("============= %s =============", e.getClass().getName());
    }
}
