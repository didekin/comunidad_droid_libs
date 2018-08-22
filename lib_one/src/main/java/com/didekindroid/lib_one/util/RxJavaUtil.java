package com.didekindroid.lib_one.util;

import android.support.annotation.NonNull;

import com.didekindroid.lib_one.api.exception.UiException;

import java.io.Serializable;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static io.reactivex.Completable.complete;
import static io.reactivex.Maybe.empty;

public class RxJavaUtil {

    private RxJavaUtil()
    {
    }

    @NonNull
    public static <T extends Serializable> Function<Response<T>, MaybeSource<T>> getResponseMaybeFunction()
    {
        Timber.d("getResponseMaybeFunction()");
        return response -> {
            if (response.body() == null) {
                if (response.errorBody() == null) {
                    return empty();
                } else {
                    return Maybe.error(new UiException(httpInitializer.get().getHttpHandler().getErrorBean(response)));
                }

            } else return Maybe.just(httpInitializer.get().getResponseBody(response));
        };
    }

    @NonNull
    public static <T extends Serializable> Function<Response<T>, CompletableSource> getResponseCompletableFunction()
    {
        Timber.d("getResponseCompletableFunction()");
        return response -> {
            if (response.errorBody() == null) {
                return complete();
            } else {
                return Completable.error(new UiException(httpInitializer.get().getHttpHandler().getErrorBean(response)));
            }
        };
    }

    @NonNull
    public static <T extends Serializable> Function<Response<T>, SingleSource<T>> getResponseSingleFunction()
    {
        Timber.d("getResponseSingleFunction()");
        return response -> {
            if (response.errorBody() != null) {
                return Single.error(new UiException(httpInitializer.get().getHttpHandler().getErrorBean(response)));
            } else {
                return Single.just(httpInitializer.get().getResponseBody(response));
            }
        };
    }
}

