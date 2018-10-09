package com.didekindroid.lib_one.security;

import android.content.Context;
import android.support.annotation.WorkerThread;

import com.didekinlib.http.exception.ExceptionMsgIf;
import com.didekinlib.model.usuario.http.AuthHeader;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.functions.Function;
import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;
import static android.util.Base64.NO_WRAP;
import static android.util.Base64.URL_SAFE;
import static android.util.Base64.decode;
import static android.util.Base64.encodeToString;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.app_pref_file_name;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.authToken_key;

/**
 * User: pedro@didekin
 * Date: 25/06/15
 * Time: 17:28
 */
public final class AuthTkCacher implements AuthTkCacherIf {

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
    public String doAuthHeaderStr(String gcmToken)
    {
        Timber.d("doAuthHeaderStr()");
        return new AuthHeader.AuthHeaderBuilder(new Base64SupplierDroid()).build().toBase64FromJsonStr();
    }

    //  ======================================================================================
    //    ............................... SHARED PREFERENCES .................................
    //  ======================================================================================

    @Override
    public synchronized AuthTkCacher updateAuthToken(String authTokenIn)
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

    public static class Base64SupplierDroid implements AuthHeader.Base64Supplier {

        public Base64SupplierDroid()
        {
        }

        @Override
        public Function<String, String> getDecoderFunction()
        {
            return (String s) -> new String(decode(s, URL_SAFE));
        }

        @Override
        public Function<String, String> getEncoderFunction()
        {
            return (String s) -> encodeToString(s.getBytes(), URL_SAFE | NO_WRAP);
        }
    }
}

