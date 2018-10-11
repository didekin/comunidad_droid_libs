package com.didekindroid.lib_one.usuario.notification;

import com.didekindroid.lib_one.api.Controller;
import com.didekindroid.lib_one.api.exception.UiException;
import com.google.firebase.iid.FirebaseInstanceIdService;

import timber.log.Timber;

/**
 * On initial startup of your app, the FCM SDK generates a registration token for the client app instance.
 * This service accesses that token.
 */
public class InstanceIdService extends FirebaseInstanceIdService {

    /**
     * Called if InstanceID token is created or updated. This may occur if the security of
     * the previous token had been compromised.
     * It simply erase the authToken in local, which contains the old version of the appId or gcmToken. This forces
     * to do login in the next user transaction with the server.
     */
    @Override
    public void onTokenRefresh()
    {
        Timber.d("onTokenRefresh()");
        try {
            new Controller().getTkCacher().updateAuthToken(null);
        } catch (UiException e) {
            Timber.e(e);
        }
    }
}
