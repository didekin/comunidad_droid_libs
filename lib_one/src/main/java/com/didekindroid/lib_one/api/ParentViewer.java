package com.didekindroid.lib_one.api;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 29/05/17
 * Time: 15:25
 */

public class ParentViewer<T extends View, C extends ControllerIf> extends Viewer<T, C> implements
        ParentViewerIf<T, C> {

    private final Map<Class<? extends ViewerIf>, ViewerIf> childViewers;

    @SuppressWarnings({"WeakerAccess", "unused"})
    protected ParentViewer(T view, AppCompatActivity activity, ParentViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
        childViewers = new HashMap<>(1);
    }

    @Override
    public void setChildViewer(@NonNull ViewerIf childViewer)
    {
        Timber.d("setChildViewer()");
        childViewers.put(childViewer.getClass(), childViewer);
    }

    @Override
    public <H extends ViewerIf> H getChildViewer(@NonNull Class<H> viewerChildClass)
    {
        Timber.d("getChildViewer()");
        return viewerChildClass.cast(childViewers.get(viewerChildClass));
    }

    @Override
    public <H extends ViewerIf> List<H> getChildViewersFromSuperClass(@NonNull Class<H> viewerChildClass)
    {
        Timber.d("getChildViewer()");
        List<H> childsList = new ArrayList<>(1);
        for (ViewerIf child : childViewers.values())
            if (viewerChildClass.isInstance(child)) {
                childsList.add(viewerChildClass.cast(child));
            }
        return childsList;
    }
}
