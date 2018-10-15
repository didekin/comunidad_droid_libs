package com.didekindroid.lib_one;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.exception.ErrorBean;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.InstanceIdResult;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Completable;
import io.reactivex.Single;
import timber.log.Timber;

import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.FIREBASE_SERVICE_NOT_AVAILABLE;
import static com.google.firebase.iid.FirebaseInstanceId.getInstance;
import static io.reactivex.Single.create;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public final class FirebaseInitializer {

    public static final AtomicReference<FirebaseInitializer> firebaseInitializer = new AtomicReference<>();
    private final String firebaseProjectId;
    private final String scopeAppIdToken;

    public FirebaseInitializer(String projectId, String scopeToken)
    {
        Timber.d(" ==== FirebaseInitializer(); firebaseProjectId: " + projectId + " scopeAppIdToken: " + scopeToken);
        firebaseProjectId = projectId;
        scopeAppIdToken = scopeToken;
    }

    public Single<String> getSingleAppIdToken()
    {
        Timber.d("getSingleAppIdToken()");
        return create(
                emitter -> {
                    try {
                        emitter.onSuccess(getInstance().getToken(firebaseProjectId, scopeAppIdToken));
                    } catch (IOException ie) {
                        emitter.onError(new UiException(new ErrorBean(FIREBASE_SERVICE_NOT_AVAILABLE)));
                    }
                });
    }

    public Single<String> getSingleAppIdTokenForTest()
    {
        Timber.d("getSingleAppIdTokenForTest()");
        return create(emitter -> {
            Task<InstanceIdResult> taskToken = getInstance().getInstanceId();
            while (!taskToken.isComplete()) {
                if (taskToken.isCanceled()) {
                    Timber.d(" ==== TaskToken is cancelled");
                    return;
                }
                MILLISECONDS.sleep(10);
            }
            if (taskToken.getException() != null) { // java.io.IOException: SERVICE_NOT_AVAILABLE
                Timber.e(taskToken.getException());
                emitter.onError(new UiException(new ErrorBean(FIREBASE_SERVICE_NOT_AVAILABLE)));
            } else {
                emitter.onSuccess(taskToken.getResult().getToken());
            }
        });
    }

    public Completable deleteFirebaseInstance()
    {
        Timber.d("deleteFirebaseInstance()");
        return Completable.create(emitter -> {
            try {
                getInstance().deleteInstanceId();
                emitter.onComplete();
            } catch (IOException ie) {
                emitter.onError(new UiException(new ErrorBean(FIREBASE_SERVICE_NOT_AVAILABLE)));
            }
        });
    }
}
