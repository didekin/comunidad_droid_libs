package com.didekindroid.lib_one.security;

import com.didekindroid.lib_one.api.Controller;
import com.didekindroid.lib_one.api.ObserverCacheCleaner;
import com.didekindroid.lib_one.api.Viewer;

import timber.log.Timber;

import static com.didekindroid.lib_one.security.OauthTokenObservable.oauthTokenFromRefreshTk;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 15/05/17
 * Time: 16:25
 */

public class CtrlerAuthToken extends Controller implements CtrlerAuthTokenIf {

    // TODO: 1. probablemente sobra este clase. Reutilizar su código en CtrlerUsuario.
    // 2.

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    /**
     * TODO: pendiente implementar preconditions.
     * Preconditions:
     * - token in cache has expired. {There must be an expiration period in json tokens.}
     * - FirebaseInstanceID.getId() should be the same as the one encoded in the local json token.
     *
     *
     * Postconditions:
     * If tokenCache.getRefreshToken() != null, but tokenCache.get().getValue() is null (no access token in cache,
     * but there exists a refresh token), the access token is remotely retrieved and updated in cache.
     */
    @Override
    public void refreshAccessToken(Viewer viewer)
    {
        Timber.d("refreshAccessToken()");

        if (isRegisteredUser()
                && getTkCacher().getTokenCache().get() != null
                && getTkCacher().getTokenCache().get().getRefreshToken() != null
                && (getTkCacher().getTokenCache().get().getValue() == null || getTkCacher().getTokenCache().get().getValue().isEmpty())
                ) {
            getSubscriptions().add(
                    oauthTokenFromRefreshTk(getTkCacher().getRefreshTokenValue())
                            .subscribeOn(io())
                            .observeOn(mainThread())
                            .subscribeWith(new ObserverCacheCleaner(viewer))
            );
        }
    }

    @Override       // TODO: probablemente sobra. Sólo se utiliza en mock implementation en los tests de esta clase.
    public boolean updateTkCacheFromRefreshTk(final String refreshToken, Viewer viewer)
    {
        Timber.d("updateTkCacheFromRefreshTk()");
        return getSubscriptions().add(
                oauthTokenFromRefreshTk(refreshToken)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new ObserverCacheCleaner(viewer))
        );
    }
}
