package com.didekindroid.lib_one.api;

/**
 * User: pedro@didekin
 * Date: 08/04/2018
 * Time: 13:11
 */
public class InjectorParentViewerAc extends ActivityMock implements InjectorOfParentViewerIf {

    @Override
    public ParentViewerIf getInjectedParentViewer()
    {
        return new ParentViewer<>(null, this, null);
    }

    @Override
    public void setChildInParentViewer(ViewerIf childViewer)
    {
    }
}
