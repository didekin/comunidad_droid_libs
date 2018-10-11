package com.didekindroid.lib_one.security;

import android.content.Context;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.exception.ErrorBean;
import com.didekinlib.http.exception.ExceptionMsgIf;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;
import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;
import static com.didekindroid.lib_one.security.AuthTkCacher.AuthTkCacherExceptionMsg.AUTH_HEADER_WRONG;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.app_pref_file_name;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.authToken_key;
import static com.didekinlib.model.usuario.http.TkValidaPatterns.tkEncrypted_direct_symmetricKey_REGEX;
import static io.reactivex.Single.error;
import static io.reactivex.Single.just;

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

    //  ======================================================================================
    //    ............................... SHARED PREFERENCES .................................
    //  ======================================================================================

    @Override
    public synchronized AuthTkCacher updateAuthToken(String authTokenIn) throws UiException
    {
        Timber.d("updateAuthToken()");
        if (authTokenIn != null && !tkEncrypted_direct_symmetricKey_REGEX.isPatternOk(authTokenIn)) {
            throw new UiException(new ErrorBean(AUTH_HEADER_WRONG));
        }
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
    public Single<String> getSingleAuthToken()
    {
        if (getAuthTokenCache() == null) {
            return error(new UiException(new ErrorBean(AUTH_HEADER_WRONG)));
        }
        return just(getAuthTokenCache());
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
}

