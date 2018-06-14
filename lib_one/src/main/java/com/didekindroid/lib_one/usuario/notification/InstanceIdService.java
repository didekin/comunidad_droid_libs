package com.didekindroid.lib_one.usuario.notification;

import com.didekindroid.lib_one.api.exception.UiException;
import com.google.firebase.iid.FirebaseInstanceIdService;

import io.reactivex.observers.DisposableCompletableObserver;
import timber.log.Timber;

/**
 * On initial startup of your app, the FCM SDK generates a registration token for the client app instance.
 * This service accesses that token.
 */
public class InstanceIdService extends FirebaseInstanceIdService {

    /**
     * Called if InstanceID token is created or updated. This may occur if the security of
     * the previous token had been compromised.
     */
    @Override
    public void onTokenRefresh()
    {
        Timber.d("onTokenRefresh()");
        final CtrlerNotifyTokenIf controller = new CtrlerNotifyToken();
        try {
            controller.modifyGcmTokenSync(new ServiceDisposableObserver(controller));
        } catch (UiException e) {
            Timber.e("======= onTokenRefresh(); exception: %s%n ============", e.getErrorHtppMsg());
        }
        controller.clearSubscriptions();
    }

    /**
     * Inner class to make easier to test the service's method in the controller.
     */
    @SuppressWarnings("WeakerAccess")
    public static class ServiceDisposableObserver extends DisposableCompletableObserver {

        private final CtrlerNotifyTokenIf controller;

        ServiceDisposableObserver(CtrlerNotifyTokenIf controller)
        {
            this.controller = controller;
        }

        @Override
        public void onComplete()
        {
            Timber.d("onComplete()");
            controller.getTkCacher().updateIsGcmTokenSentServer(true);
        }

        @Override
        public void onError(Throwable error)
        {
            Timber.d("onError()");
        }
    }
}
