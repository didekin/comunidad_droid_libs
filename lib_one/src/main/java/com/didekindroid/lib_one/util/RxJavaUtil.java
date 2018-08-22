package com.didekindroid.lib_one.util;

import android.support.annotation.NonNull;

import com.didekindroid.lib_one.api.exception.UiException;

import java.io.Serializable;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.functions.Function;
import retrofit2.Response;

import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static io.reactivex.Maybe.empty;

public class RxJavaUtil {

    private RxJavaUtil()
    {
    }

    @NonNull
    public static <T extends Serializable> Function<Response<T>, MaybeSource<T>> getResponseMaybeFunction()
    {
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
}

