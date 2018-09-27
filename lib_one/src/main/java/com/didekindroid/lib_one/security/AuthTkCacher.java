package com.didekindroid.lib_one.security;

import android.content.Context;
import android.support.annotation.WorkerThread;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.exception.ErrorBean;
import com.didekinlib.http.exception.ExceptionMsgIf;
import com.didekinlib.model.usuario.http.AuthHeaderIf;
import com.google.gson.Gson;

import java.util.concurrent.atomic.AtomicReference;

import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;
import static android.util.Base64.NO_WRAP;
import static android.util.Base64.URL_SAFE;
import static android.util.Base64.decode;
import static android.util.Base64.encodeToString;
import static com.didekindroid.lib_one.security.AuthTkCacher.AuthTkCacherExceptionMsg.AUTH_HEADER_WRONG;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.app_pref_file_name;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.authToken_key;

/**
 * User: pedro@didekin
 * Date: 25/06/15
 * Time: 17:28
 */
public final class AuthTkCacher implements AuthTkCacherIf {

    public static final String identity_token_should_be_notnull = "identity_token_should_be_notnull";

    private final AtomicReference<String> authTokenCache;
    private final Context context;

    public AuthTkCacher(Context contextIn)
    {
        Timber.d("AuthTkCacher(Context)");
        context = contextIn;
        Timber.d("getAuthToken()");
        authTokenCache = new AtomicReference<>(
                context.getSharedPreferences(
                        app_pref_file_name.toString(), MODE_PRIVATE)
                        .getString(authToken_key.toString(), null)
        );
    }

    @Override
    @WorkerThread
    public String doAuthHeaderStr(String gcmToken) throws UiException
    {
        Timber.d("doAuthHeaderStr()");
        return new AuthHeaderDroid(gcmToken, authTokenCache.get()).getBase64Str();
    }

    public String doAuthHeaderStrMock(String mockGcmTk) throws UiException
    {
        return new AuthHeaderDroid(mockGcmTk, authTokenCache.get()).getBase64Str();
    }

    //  ======================================================================================
    //    ............................... SHARED PREFERENCES .................................
    //  ======================================================================================

    @Override
    public AuthTkCacher updateAuthToken(String authTokenIn)
    {
        Timber.d("updateAuthToken()");
        authTokenCache.set(authTokenIn);
        context.getSharedPreferences(app_pref_file_name.toString(), MODE_PRIVATE)
                .edit()
                .putString(authToken_key.toString(), authTokenCache.get())
                .apply();
        return this;
    }

    //  ======================================================================================
    //    .................................... ACCESSORS .................................
    /*  ======================================================================================*/

    @Override
    public boolean isUserRegistered()
    {
        Timber.d("isUserRegistered()");
        return getAuthTokenCache() != null;
    }

    @Override
    public String getAuthTokenCache()
    {
        return authTokenCache.get();
    }

    @Override
    public Context getContext()
    {
        return context;
    }

    //  ======================================================================================
    //    ............................ Inner classes .................................
    //  ======================================================================================

    enum SharedPrefConstant {

        app_pref_file_name,
        user_name_key,
        authToken_key,;

        @Override
        public String toString()
        {
            return getClass().getName().concat(".").concat(name());
        }
    }

    public enum AuthTkCacherExceptionMsg implements ExceptionMsgIf {

        AUTH_HEADER_WRONG("AuthHeaderDroid wrongly initialized", 401),;

        private final String message;
        private final int httpStatus;

        AuthTkCacherExceptionMsg(String messageIn, int htppStatusIn)
        {
            message = messageIn;
            httpStatus = htppStatusIn;
        }

        @Override
        public String getHttpMessage()
        {
            return message;
        }

        @Override
        public int getHttpStatus()
        {
            return httpStatus;
        }
    }

    static class AuthHeaderDroid implements AuthHeaderIf {

        private final String appID;
        private final String token;

        AuthHeaderDroid(String appIDIn, String tokenIn) throws UiException
        {
            this.appID = appIDIn;
            this.token = tokenIn;
            if (appID == null || token == null) {
                throw new UiException(new ErrorBean(AUTH_HEADER_WRONG));
            }
        }

        public AuthHeaderDroid(String base64Str)
        {
            AuthHeaderIf header = new Gson().fromJson(new String(decode(base64Str, URL_SAFE)), AuthHeaderDroid.class);
            appID = header.getAppID();
            token = header.getToken();
        }

        @Override
        public String toString()
        {
            return new Gson().toJson(this);
        }

        @Override
        public String getBase64Str()
        {
            return encodeToString(toString().getBytes(), URL_SAFE | NO_WRAP);
        }

        @Override
        public String getAppID()
        {
            return appID;
        }

        @Override
        public String getToken()
        {
            return token;
        }
    }
}

