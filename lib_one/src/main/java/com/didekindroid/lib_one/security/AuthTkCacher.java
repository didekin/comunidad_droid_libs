package com.didekindroid.lib_one.security;

import android.content.Context;
import android.content.SharedPreferences;

import com.didekinlib.http.usuario.AuthHeader;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.app_pref_file_name;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.authToken_key;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.is_gcmTk_sent_server_key;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.is_user_registered_key;
import static com.didekindroid.lib_one.security.AuthTkCacher.SharedPrefConstant.user_name_key;
import static com.google.firebase.iid.FirebaseInstanceId.getInstance;
import static java.util.Objects.requireNonNull;

/**
 * User: pedro@didekin
 * Date: 25/06/15
 * Time: 17:28
 */
@SuppressWarnings("UnusedReturnValue")
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
        editor.putBoolean(is_user_registered_key.toString(), isRegisteredUser);
        if (!isRegisteredUser) {
            updateUserName(null);
            updateAuthToken(null);
            updateIsGcmTokenSentServer(false);
        }
        editor.apply();
        isRegisteredCache.set(isRegisteredUser);
        return this;
    }

    @Override
    public AuthTkCacher updateUserName(String userName)
    {
        Timber.d("updateUserName()");
        if (userName != null) {
            updateIsRegistered(true);
            getSharedPref().edit().putString(user_name_key.toString(), userName).apply();
            userNameCache.set(userName);
        }
        return this;
    }

    @Override
    public AuthTkCacher updateIsGcmTokenSentServer(boolean isSentToServer)
    {
        Timber.d("updateIsGcmTokenSentServer(), isSentToServer = %b", isSentToServer);
        if (isSentToServer) {
            updateIsRegistered(true);
            getSharedPref().edit().putBoolean(is_gcmTk_sent_server_key.toString(), true).apply();
        }
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
    public String doAuthHeaderStr()
    {
        return doAuthHeader().getBase64Str();
    }

    //    ............................ Helpers .................................

    AuthHeader doAuthHeader()
    {
        return new AuthHeader.AuthHeaderBuilder()
                .userName(userNameCache.get())
                .appId(requireNonNull(getInstance().getToken()))
                .tokenInLocal(authTokenCache.get())
                .build();
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
}

