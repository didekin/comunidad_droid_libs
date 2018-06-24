package com.didekindroid.lib_one.api;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.AdapterView;

import java.io.Serializable;
import java.util.List;

import io.reactivex.functions.Function;

/**
 * User: pedro@didekin
 * Date: 20/02/17
 * Time: 10:11
 */

public interface ViewerSelectListIf<T extends AdapterView, C extends CtrlerSelectListIf<E>, E extends Serializable> extends
        ViewerIf<T, C> {

    void onSuccessLoadItemList(List<E> incidCloseList);

    void initSelectedItemId(Bundle savedState);

    Function<E, Long> getBeanIdFunction();

    long getSelectedItemId();

    void setSelectedItemId(long itemSelectedId);

    int getSelectedPositionFromItemId(Function<E, Long> beanIdFunc) throws Exception;

    void onSuccessLoadSelectedItem(@NonNull Bundle bundle);
}
