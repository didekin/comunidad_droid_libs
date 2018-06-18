package com.didekindroid.lib_one.api;

import android.widget.AdapterView;

import java.io.Serializable;
import java.util.List;

/**
 * User: pedro@didekin
 * Date: 24/10/2017
 * Time: 10:12
 */

public interface ViewerListIf<T extends AdapterView, C extends CtrlerListIf>
        extends ViewerIf<T, C> {
    void onSuccessLoadItemList(List<? extends Serializable> itemsList);
}
