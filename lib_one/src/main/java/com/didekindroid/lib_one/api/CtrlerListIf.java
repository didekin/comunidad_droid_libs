package com.didekindroid.lib_one.api;

import java.io.Serializable;
import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;

/**
 * User: pedro@didekin
 * Date: 24/10/2017
 * Time: 10:39
 */

public interface CtrlerListIf extends ControllerIf {
    boolean loadItemsByEntitiyId(DisposableSingleObserver<List<? extends Serializable>> observer, long entityId);
}
