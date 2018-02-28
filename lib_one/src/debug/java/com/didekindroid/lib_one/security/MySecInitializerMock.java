package com.didekindroid.lib_one.security;

import android.content.Context;
import android.content.res.Resources;

import com.didekinlib.http.JksInClient;

/**
 * User: pedro@didekin
 * Date: 28/02/2018
 * Time: 11:21
 */
public class MySecInitializerMock implements SecInitializerIf {

    private final Context context;
    private final IdentityCacherIf identityCacher;

    public MySecInitializerMock(Context contextIn, IdentityCacherIf identityCacherIn)
    {
        context = contextIn;
        identityCacher = identityCacherIn;
    }

    @Override
    public JksInClient getJksInClient()
    {
        return null;
    }

    @Override
    public IdentityCacherIf getTkCacher()
    {
        return identityCacher;
    }

    @Override
    public Resources getAppResources()
    {
        return context.getResources();
    }
}
