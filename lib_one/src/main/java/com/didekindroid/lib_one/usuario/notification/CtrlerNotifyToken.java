package com.didekindroid.lib_one.usuario.notification;

import com.didekindroid.lib_one.api.Controller;

import io.reactivex.observers.DisposableCompletableObserver;
import timber.log.Timber;

import static com.didekindroid.lib_one.usuario.dao.UsuarioDao.usuarioDaoRemote;
import static com.google.firebase.iid.FirebaseInstanceId.getInstance;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 03/03/17
 * Time: 14:23
 */
public class CtrlerNotifyToken extends Controller implements CtrlerNotifyTokenIf {

/* .................................... INSTANCE METHODS .................................*/

    /**
     * Synchronous variant for the service InstanceIdService.
     * The method does not check if the gcmToken has been sent previously to database.
     *
     * @param observer is instantiated by the viewer who calls the controller.
     */
    @Override
    public boolean modifyGcmTokenSync(DisposableCompletableObserver observer)
    {
        Timber.d("modifyGcmTokenSync()");
        return getTkCacher().isRegisteredCache()
                && getSubscriptions().add(usuarioDaoRemote.modifyGcmToken(getInstance().getToken())
                        .subscribeWith(observer));
    }
}
