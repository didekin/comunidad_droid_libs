package com.didekindroid.lib_one.security;


import android.content.Context;

import com.didekindroid.lib_one.api.exception.UiException;

/**
 * User: pedro@didekin
 * Date: 21/12/16
 * Time: 18:17
 */
public interface AuthTkCacherIf {

    Context getContext();

    boolean isRegisteredCache();

    boolean isGcmTokenSentServer();

    AuthTkCacher updateIsRegistered(boolean isRegisteredUser);

    AuthTkCacher updateIsGcmTokenSentServer(boolean isSentToServer);

    AuthTkCacher updateAuthToken(String authTokenIn);

    String doAuthHeaderStr() throws UiException;
}
