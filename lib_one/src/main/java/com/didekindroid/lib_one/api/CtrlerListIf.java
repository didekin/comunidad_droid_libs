package com.didekindroid.lib_one.api;

import android.os.Bundle;

import io.reactivex.observers.DisposableSingleObserver;

/**
 * User: pedro@didekin
 * Date: 24/10/2017
 * Time: 10:39
 */

public interface CtrlerListIf<E> extends CtrlerSelectListIf {
    
    default boolean selectItem(DisposableSingleObserver<Bundle> observer, E item)
    {
        throw new UnsupportedOperationException();
    }
}
