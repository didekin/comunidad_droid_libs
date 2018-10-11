package com.didekindroid.lib_one.security;


import android.content.Context;

import com.didekindroid.lib_one.api.exception.UiException;

import io.reactivex.Single;

/**
 * User: pedro@didekin
 * Date: 21/12/16
 * Time: 18:17
 */
public interface AuthTkCacherIf {

    String getAuthTokenCache();

    Single<String> getSingleAuthToken();

    Context getContext();

    boolean isUserRegistered();

    AuthTkCacher updateAuthToken(String authTokenIn) throws UiException;
}
