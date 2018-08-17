package com.didekindroid.lib_one.usuario.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.InstanceIdResult;

import io.reactivex.Single;
import timber.log.Timber;

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
            if (taskToken.getException() != null) {
                emitter.onError(taskToken.getException());
            } else {
                emitter.onSuccess(taskToken.getResult().getToken());
            }
        });
    }
}
