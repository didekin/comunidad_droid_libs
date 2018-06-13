package com.didekindroid.lib_one.security;

import android.content.Context;
import android.content.SharedPreferences;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.exception.ErrorBean;
import com.didekinlib.http.exception.ExceptionMsgIf;
import com.didekinlib.http.usuario.AuthHeaderIf;
import com.google.gson.Gson;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;
import static android.util.Base64.NO_WRAP;
import static android.util.Base64.URL_SAFE;
import static android.util.Base64.decode;
import static android.util.Base64.encodeToString;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.app_pref_file_name;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.authToken_key;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.is_gcmTk_sent_server_key;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.is_user_registered_key;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.user_name_key;
import static com.google.firebase.iid.FirebaseInstanceId.getInstance;

/**
 * User: pedro@didekin
 * Date: 25/06/15
 * Time: 17:28
 */
public final class AuthTkCacher implements AuthTkCacherIf {

    public static final String identity_token_should_be_notnull = "identity_token_should_be_notnull";

    private final AtomicBoolean isRegisteredCache;
    private final AtomicReference<String> authTokenCache;
    private final AtomicReference<String> userNameCache;
    private final Context context;

    public AuthTkCacher(Context contextIn)
    {
        Timber.d("AuthTkCacher(Context)");
        context = contextIn;
        isRegisteredCache = new AtomicBoolean(isRegisteredUser());
        userNameCache = new AtomicReference<>(getUserName());
        authTokenCache = new AtomicReference<>(getAuthToken());
    }

    //  ======================================================================================
    //    ............................... SHARED PREFERENCES .................................
    //  ======================================================================================

    SharedPreferences getSharedPref()
    {
        return context.getSharedPreferences(app_pref_file_name.toString(), MODE_PRIVATE);
    }

    boolean isRegisteredUser()
    {
        Timber.d("isRegisteredUser()");
        return getSharedPref().getBoolean(is_user_registered_key.toString(), false);
    }

    public String getUserName()
    {
        Timber.d("getUserName()");
        return getSharedPref().getString(user_name_key.toString(), null);
    }

    @Override
    public boolean isGcmTokenSentServer()
    {
        Timber.d("isGcmTokenSentServer()");
        return getSharedPref().getBoolean(is_gcmTk_sent_server_key.toString(), false);
    }

    public String getAuthToken()
    {
        Timber.d("isAuthToken()");
        return getSharedPref().getString(authToken_key.toString(), null);
    }

    @Override
    public AuthTkCacher updateIsRegistered(boolean isRegisteredUser)
    {
        Timber.d("updateIsRegistered()");
        SharedPreferences.Editor editor = getSharedPref().edit();
        synchronized (this) {
            editor.putBoolean(is_user_registered_key.toString(), isRegisteredUser).apply();
            isRegisteredCache.set(isRegisteredUser);
        }
        if (!isRegisteredCache.get()) {
            updateUserName(null);
            updateAuthToken(null);
            updateIsGcmTokenSentServer(false);
        }
        return this;
    }

    @Override
    public AuthTkCacher updateUserName(String userName)
    {
        Timber.d("updateUserName()");
        if (userName != null) {
            updateIsRegistered(true);
        }
        getSharedPref().edit().putString(user_name_key.toString(), userName).apply();
        userNameCache.set(getUserName());
        return this;
    }

    @Override
    public AuthTkCacher updateIsGcmTokenSentServer(boolean isSentToServer)
    {
        Timber.d("updateIsGcmTokenSentServer(), isSentToServer = %b", isSentToServer);
        if (isSentToServer) {
            updateIsRegistered(true);
        }
        getSharedPref().edit().putBoolean(is_gcmTk_sent_server_key.toString(), isSentToServer).apply();
        return this;
    }

    @Override
    public AuthTkCacher updateAuthToken(String authTokenIn)
    {
        Timber.d("updateAuthToken()");
        getSharedPref().edit().putString(authToken_key.toString(), authTokenIn).apply();
        authTokenCache.set(authTokenIn);
        if (authTokenIn != null) {
            updateIsRegistered(true);
        }
        return this;
    }

    //  ======================================================================================
    //    .................................... ACCESSORS .................................
    /*  ======================================================================================*/

    @Override
    public boolean isRegisteredCache()
    {
        return isRegisteredCache.get();
    }

    String getUserNameCache()
    {
        return userNameCache.get();
    }

    String getAuthTokenCache()
    {
        return authTokenCache.get();
    }

    @Override
    public Context getContext()
    {
        return context;
    }

    @Override
    public String doAuthHeaderStr() throws UiException
    {
        return doAuthHeader().getBase64Str();
    }

    //    ............................ Helpers .................................

    AuthHeaderIf doAuthHeader() throws UiException
    {
        return new AuthHeaderDroid(userNameCache.get(), getInstance().getToken(), authTokenCache.get());
    }

    //  ======================================================================================
    //    ............................ Inner classes .................................
    //  ======================================================================================

    enum SharedPrefConstant {

        // name of the file.
        app_pref_file_name,
        // keys.
        is_gcmTk_sent_server_key,
        is_user_registered_key,
        user_name_key,
        authToken_key,;

        @Override
        public String toString()
        {
            return getClass().getName().concat(".").concat(name());
        }
    }

    static class AuthTkCacherExceptionMsg implements ExceptionMsgIf {

        @Override
        public String getHttpMessage()
        {
            return "AuthTkCache not properly initialized.";
        }

        @Override
        public int getHttpStatus()
        {
            throw new UnsupportedOperationException();
        }
    }

    static class AuthHeaderDroid implements AuthHeaderIf {

        private final String userName;
        private final String appID;
        private final String token;


        AuthHeaderDroid(String userNameIn, String appIDIn, String tokenIn) throws UiException
        {
            this.userName = userNameIn;
            this.appID = appIDIn;
            this.token = tokenIn;
            if (userName == null || appID == null || token == null) {
                throw new UiException(new ErrorBean(new AuthTkCacherExceptionMsg()));
            }
        }

        public AuthHeaderDroid(String base64Str)
        {
            AuthHeaderIf header = new Gson()
                    .fromJson(new String(decode(base64Str, URL_SAFE)), AuthHeaderDroid.class);
            userName = header.getUserName();
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
        public String getUserName()
        {
            return userName;
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

