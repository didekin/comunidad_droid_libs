package com.didekindroid.lib_one.api;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.didekindroid.lib_one.api.router.UiExceptionRouterIf;

import java.io.Serializable;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 13:29
 */
public interface ViewerIf<T extends View, C extends ControllerIf> {


    Activity getActivity();

    UiExceptionRouterIf getExceptionRouter();

    void onErrorInObserver(Throwable error);

    int clearSubscriptions();

    T getViewInViewer();

    void doViewInViewer(Bundle savedState, Serializable viewBean);

    @Nullable
    C getController();

    void setController(@NonNull C controller);

    /**
     * The data saved by this method is used in doViewInViewer().
     */
    void saveState(Bundle savedState);

    @Nullable
    ViewerIf getParentViewer();

    default void onErrorInController(Throwable error){
        onErrorInObserver(error);
    }
}
