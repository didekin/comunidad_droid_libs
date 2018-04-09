package com.didekindroid.lib_one.testutil;

import android.app.Activity;
import android.content.Context;

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
@SuppressWarnings("WeakerAccess")
public final class InitializerTestUtil {

    private static final int webHost = R.string.didekin_web_host;
    private static final int webHostPort = R.string.didekin_web_port;
    private static final int timeOut = R.string.timeOut;
    private static final int bks_pswd = R.string.didekindroid_bks_pswd;
    private static final int bks_name = R.string.didekindroid_bks_name;

    private InitializerTestUtil()
    {
    }

    public static void initRouter()
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

    public static void initSec_Http(Context context)
    {
        secInitializer.compareAndSet(null, new SecInitializer(context, bks_pswd, bks_name));
        httpInitializer.compareAndSet(
                null,
                new HttpInitializer.HttpInitializerBuilder(context)
                        .webHostAndPort(webHost, webHostPort)
                        .timeOut(timeOut)
                        .jksInClient(secInitializer.get().getJksInClient())
                        .build()
        );
    }

    public static void initSec_Http_Router(Context context)
    {
        initRouter();
        initSec_Http(context);
    }
}
