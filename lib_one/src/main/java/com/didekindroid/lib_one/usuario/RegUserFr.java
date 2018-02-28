package com.didekindroid.lib_one.usuario;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.api.InjectorOfParentViewerIf;

import timber.log.Timber;

@SuppressWarnings("unused")
public class RegUserFr extends Fragment {

    InjectorOfParentViewerIf viewerInjector;
    ViewerRegUserFr viewer;
    private View regUserFrView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        regUserFrView = inflater.inflate(R.layout.reg_user_fr, container, false);
        return regUserFrView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        viewerInjector = (InjectorOfParentViewerIf) getActivity();
        viewer = new ViewerRegUserFr(regUserFrView, getActivity(), viewerInjector.getInjectedParentViewer());
        viewer.doViewInViewer(savedInstanceState, null);
        viewerInjector.setChildInParentViewer(viewer);
    }

    public ViewerRegUserFr getViewer()
    {
        return viewer;
    }
}
