package com.didekindroid.lib_one.testutil;

import android.app.Activity;
import android.content.Context;

import com.didekindroid.lib_one.FirebaseInitializer;
import com.didekindroid.lib_one.HttpInitializer;
import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.router.ContextualRouter;
import com.didekindroid.lib_one.api.router.ContextualRouterIf;
import com.didekindroid.lib_one.api.router.MnRouter;
import com.didekindroid.lib_one.api.router.MnRouterIf;
import com.didekindroid.lib_one.api.router.RouterInitializerMock;
import com.didekindroid.lib_one.api.router.UiExceptionRouter;
import com.didekindroid.lib_one.api.router.UiExceptionRouterIf;
import com.didekindroid.lib_one.security.SecInitializer;

import static com.didekindroid.lib_one.BuildConfig.didekin_web_host;
import static com.didekindroid.lib_one.BuildConfig.didekin_web_port;
import static com.didekindroid.lib_one.FirebaseInitializer.firebaseInitializer;
import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.usuario.router.UserContextAction.userContextAcMap;
import static com.didekindroid.lib_one.usuario.router.UserMnAction.userMnItemMap;
import static com.didekindroid.lib_one.usuario.router.UserUiExceptionAction.userExceptionMsgMap;

/**
 * User: pedro@didekin
 * Date: 02/03/2018
 * Time: 16:29
 */
public final class InitializerTestUtil {

    // Passed from buil.gradle (lib_one)
    private static final String webHost = didekin_web_host;
    // Passed from buil.gradle (lib_one)
    private static final String webHostPort = didekin_web_port;
    private static final int timeOut = R.string.timeOut;
    public static final int bks_pswd = R.string.didekindroid_bks_pswd;
    public static final int bks_name = R.string.didekindroid_bks_name;

    private static final String project_id = "61369502868";
    private static final String scope_token = "FCM";

    private InitializerTestUtil()
    {
    }

    public static void initRouterAll()
    {
        routerInitializer.set(new RouterInitializerMock() {
            @Override
            public ContextualRouterIf getContextRouter()
            {
                return new ContextualRouter(userContextAcMap);
            }

            @Override
            public UiExceptionRouterIf getExceptionRouter()
            {
                return new UiExceptionRouter(userExceptionMsgMap);
            }

            @Override
            public MnRouterIf getMnRouter()
            {
                return new MnRouter(userMnItemMap);
            }

            @Override
            public Class<? extends Activity> getDefaultAc()
            {
                return ActivityMock.class;
            }
        });
    }

    public static void initSecurity(Context context)
    {
        secInitializer.compareAndSet(null, new SecInitializer(context, bks_pswd, bks_name));
    }

    public static void cleanInitialSec()
    {
        secInitializer.set(null);
    }

    public static void initSec_Http(Context context)
    {
        initSecurity(context);
        httpInitializer.compareAndSet(
                null,
                new HttpInitializer.HttpInitializerBuilder(context)
                        .webHostAndPort(webHost, webHostPort)
                        .timeOut(timeOut)
                        .jksInClient(secInitializer.get().getJksInClient())
                        .build()
        );
        firebaseInitializer.compareAndSet(null,
                new FirebaseInitializer.FirebaseInitializerBuilder(project_id, scope_token).build());
    }

    public static void initSec_Http_Router(Context context)
    {
        initRouterAll();
        initSec_Http(context);
    }
}
