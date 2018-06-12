package com.didekindroid.lib_one;

import android.app.Activity;

import com.didekindroid.lib_one.api.router.ContextualRouterIf;
import com.didekindroid.lib_one.api.router.MnRouterIf;
import com.didekindroid.lib_one.api.router.RouterInitializerIf;
import com.didekindroid.lib_one.api.router.UiExceptionRouterIf;
import com.didekinlib.model.common.dominio.BeanBuilder;

import java.util.concurrent.atomic.AtomicReference;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 14/02/2018
 * Time: 15:13
 */
public final class RouterInitializer implements RouterInitializerIf {

    public static final AtomicReference<RouterInitializerIf> routerInitializer = new AtomicReference<>();
    private final UiExceptionRouterIf exceptionRouter;
    private final MnRouterIf mnRouter;
    private final ContextualRouterIf contextRouter;
    private final Class<? extends Activity> defaultAc;

    private RouterInitializer(RouterInitializerBuilder builder)
    {
        exceptionRouter = builder.exceptionRouter;
        mnRouter = builder.mnRouter;
        contextRouter = builder.contextRouter;
        defaultAc = builder.defaultAc;
    }

    @Override
    public UiExceptionRouterIf getExceptionRouter()
    {
        return exceptionRouter;
    }

    @Override
    public MnRouterIf getMnRouter()
    {
        return mnRouter;
    }

    @Override
    public ContextualRouterIf getContextRouter()
    {
        return contextRouter;
    }

    @Override
    public Class<? extends Activity> getDefaultAc()
    {
        return defaultAc;
    }

    //    ==================== BUILDER ====================

    public static class RouterInitializerBuilder implements BeanBuilder<RouterInitializer> {

        private UiExceptionRouterIf exceptionRouter;
        private MnRouterIf mnRouter;
        private ContextualRouterIf contextRouter;
        private Class<? extends Activity> defaultAc;

        public RouterInitializerBuilder exceptionRouter(UiExceptionRouterIf exceptionRouterIn)
        {
            exceptionRouter = exceptionRouterIn;
            return this;
        }

        public RouterInitializerBuilder mnRouter(MnRouterIf mnRouterIn)
        {
            mnRouter = mnRouterIn;
            return this;
        }

        public RouterInitializerBuilder contexRouter(ContextualRouterIf contextRouterIn)
        {
            contextRouter = contextRouterIn;
            return this;
        }

        public RouterInitializerBuilder defaultAc(Class<? extends Activity> defaultAcIn)
        {
            defaultAc = defaultAcIn;
            return this;
        }

        @Override
        public RouterInitializer build()
        {
            Timber.d("build()");
            return new RouterInitializer(this);
        }
    }
}
