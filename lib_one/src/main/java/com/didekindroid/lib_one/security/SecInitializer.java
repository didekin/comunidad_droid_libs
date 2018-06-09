package com.didekindroid.lib_one.security;

import android.content.Context;
import android.content.res.Resources;

import com.didekinlib.http.JksInClient;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

/**
 * User: pedro@didekin
 * Date: 09/02/2018
 * Time: 10:51
 */

public class SecInitializer implements SecInitializerIf {

    // Singleton.
    public static final AtomicReference<SecInitializerIf> secInitializer = new AtomicReference<>();

    //  ======================================================================================
    //    ................................ Instance methods ..............................
    //  ======================================================================================

    private final JksInClient jksInClient;
    private final AuthTkCacherIf tkCacher;
    private final Resources appResources;

    public SecInitializer(Context contextIn, int jksPswdResourceId, int jksNameResourceId)
    {
        appResources = contextIn.getResources();
        jksInClient = new JksInClientApp(
                appResources.getString(jksPswdResourceId),
                appResources.getIdentifier(appResources.getString(jksNameResourceId), "raw", contextIn.getPackageName())
        );
        tkCacher = new AuthTkCacher(contextIn);
    }

    @Override
    public JksInClient getJksInClient()
    {
        return jksInClient;
    }

    @Override
    public AuthTkCacherIf getTkCacher()
    {
        return tkCacher;
    }

    @Override
    public Resources getAppResources()
    {
        return appResources;
    }

    static class JksInClientApp implements JksInClient {

        private final String jksPswd;
        private final int jksResourceId;

        JksInClientApp(String jksPswd, int jksResourceId)
        {
            this.jksPswd = jksPswd;
            this.jksResourceId = jksResourceId;
        }

        @Override
        public InputStream getInputStream()
        {
            return secInitializer.get().getAppResources().openRawResource(jksResourceId);
        }

        @Override
        public String getJksPswd()
        {
            return jksPswd;
        }
    }
}
