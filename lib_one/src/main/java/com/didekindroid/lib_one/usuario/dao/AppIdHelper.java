package com.didekindroid.lib_one.usuario.dao;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.exception.ErrorBean;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.InstanceIdResult;

import io.reactivex.Single;
import timber.log.Timber;

import static com.didekinlib.http.usuario.UsuarioExceptionMsg.FIREBASE_SERVICE_NOT_AVAILABLE;
import static com.google.firebase.iid.FirebaseInstanceId.getInstance;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class AppIdHelper {

    public static final AppIdHelper appIdSingle = new AppIdHelper();

    private AppIdHelper()
    {
    }

    public Single<String> getTokenSingle()
    {
        Timber.d("getTokenSingle()");
        return Single.create(emitter -> {
            Task<InstanceIdResult> taskToken = getInstance().getInstanceId();
            while (!taskToken.isComplete()) {
                if (taskToken.isCanceled()) {
                    Timber.d("============== TaskToken is cancelled");
                    return;
                }
                MILLISECONDS.sleep(50);
            }
            if (taskToken.getException() != null) { // java.io.IOException: SERVICE_NOT_AVAILABLE
                Timber.e(taskToken.getException());
                emitter.onError(new UiException(new ErrorBean(FIREBASE_SERVICE_NOT_AVAILABLE)));
            } else {
                emitter.onSuccess(taskToken.getResult().getToken());
            }
        });
    }
//
}
