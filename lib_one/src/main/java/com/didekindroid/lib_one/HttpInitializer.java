package com.didekindroid.lib_one;

import android.content.Context;

import com.didekindroid.lib_one.api.HttpInitializerIf;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.BeanBuilder;
import com.didekinlib.http.JksInClient;
import com.didekinlib.http.retrofit.HttpHandler;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Response;
import timber.log.Timber;

import static com.didekindroid.lib_one.util.CommonAssertionMsg.httpInitializer_wrong_build_data;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static java.lang.Integer.parseInt;

/**
 * User: pedro@didekin
 * Date: 17/11/16
 * Time: 14:21
 */
public final class HttpInitializer implements HttpInitializerIf {

    public static final AtomicReference<HttpInitializer> httpInitializer = new AtomicReference<>();
    private final Context context;
    private final HttpHandler httpHandler;

    private HttpInitializer(HttpInitializerBuilder builder)
    {
        context = builder.context;
        httpHandler = new HttpHandler.HttpHandlerBuilder(builder.webHostPortStr)
                .timeOutSec(builder.timeOut)
                .keyStoreClient(builder.jksInClient)
                .build();
    }

    @Override
    public Context getContext()
    {
        return context;
    }

    @Override
    public HttpHandler getHttpHandler()
    {
        return httpHandler;
    }

    @Override
    public <T> T getResponseBody(Response<T> response) throws UiException, IOException
    {
        Timber.d("getResponseBody()");
        if (response.isSuccessful()) {
            Timber.d("getResponseBody(): successful");
            return response.body();
        } else {
            Timber.d("getResponseBody(), error %s", httpHandler.getErrorBean(response).getMessage());
            throw new UiException(httpHandler.getErrorBean(response));
        }
    }

    //    ==================== BUILDER ====================

    public static class HttpInitializerBuilder implements BeanBuilder<HttpInitializer> {

        private final Context context;
        private String webHostPortStr;
        private int timeOut;
        private JksInClient jksInClient;

        public HttpInitializerBuilder(Context context)
        {
            this.context = context;
        }

        public HttpInitializerBuilder webHostAndPort(String webHostIn, String webPortIn)
        {
            webHostPortStr = webHostIn + webPortIn;
            return this;
        }

        public HttpInitializerBuilder timeOut(int timeOutIn)
        {
            timeOut = parseInt(context.getString(timeOutIn));
            return this;
        }

        public HttpInitializerBuilder jksInClient(JksInClient jksInClientIn)
        {
            assertTrue(jksInClientIn != null, httpInitializer_wrong_build_data);
            jksInClient = jksInClientIn;
            return this;
        }

        @SuppressWarnings("SyntheticAccessorCall")
        @Override
        public HttpInitializer build()
        {
            return new HttpInitializer(this);
        }
    }
}
