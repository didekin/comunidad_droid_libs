package com.didekindroid.lib_one.usuario.notification;

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
        controller.modifyGcmTokenSync(new ServiceDisposableObserver(controller));
        controller.clearSubscriptions();
    }

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
            Timber.d("============= onComplete() ===============");
        }

        @Override
        public void onError(Throwable error)
        {
            Timber.d("============ onError() ============");
            Timber.d(error);
        }
    }
}
