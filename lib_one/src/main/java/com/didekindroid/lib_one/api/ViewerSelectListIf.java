package com.didekindroid.lib_one.api;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.AdapterView;

import java.io.Serializable;
import java.util.List;

/**
 * User: pedro@didekin
 * Date: 20/02/17
 * Time: 10:11
 */

public interface ViewerSelectListIf<T extends AdapterView, C extends CtrlerSelectListIf<E>, E extends Serializable> extends
        ViewerIf<T, C> {

    void onSuccessLoadItemList(List<E> incidCloseList);

    void initSelectedItemId(Bundle savedState);

    long getSelectedItemId();

    void setSelectedItemId(long itemSelectedId);

    int getSelectedPositionFromItemId(long itemId);

    void onSuccessLoadSelectedItem(@NonNull Bundle bundle);
}
