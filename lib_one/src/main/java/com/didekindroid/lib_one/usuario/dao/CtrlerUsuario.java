package com.didekindroid.lib_one.usuario.dao;

import android.support.annotation.NonNull;

import com.didekindroid.lib_one.api.Controller;
import com.didekinlib.model.usuario.Usuario;

import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.lib_one.testutil.AppIdHelper.appIdSingle;
import static com.didekindroid.lib_one.usuario.dao.UsuarioDao.usuarioDaoRemote;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 21/02/17
 * Time: 12:53
 */
public class CtrlerUsuario extends Controller implements CtrlerUsuarioIf {

    private final UsuarioDao usuarioDao;

    public CtrlerUsuario()
    {
        super();
        usuarioDao = usuarioDaoRemote;
    }

    @Override
    public boolean deleteMe(DisposableCompletableObserver observer)
    {
        Timber.d("deleteMe()");
        return getSubscriptions().add(
                usuarioDao.deleteUser()
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    @Override
    public boolean getAppIdToken(DisposableSingleObserver<String> observer)
    {
        Timber.d("getAppIdToken()");
        return getSubscriptions().add(
                appIdSingle.getTokenSingle()
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    @Override
    public boolean getUserData(DisposableSingleObserver<Usuario> observer)
    {
        Timber.d("getUserData()");
        return getSubscriptions().add(
                usuarioDao.getUserData()
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    @Override
    public boolean login(@NonNull DisposableCompletableObserver observer, @NonNull Usuario usuario)
    {
        Timber.i("login()");
        return getSubscriptions().add(
                usuarioDao.login(usuario.getUserName(), usuario.getPassword())
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    @Override
    public boolean modifyUserName(DisposableSingleObserver<Boolean> observer, Usuario newUser)
    {
        Timber.d("modifyUserName()");
        return getSubscriptions().add(
                usuarioDao.modifyUserName(newUser)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer));
    }

    @Override
    public boolean modifyUserAlias(DisposableSingleObserver<Boolean> observer, Usuario newUser)
    {
        Timber.d("modifyUserName()");
        return getSubscriptions().add(
                usuarioDao.modifyUserAlias(newUser)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer));
    }

    @Override
    public boolean passwordChange(DisposableCompletableObserver observer, final Usuario oldUser, final Usuario newUser)
    {
        Timber.d("passwordChange()");
        return getSubscriptions().add(
                usuarioDao.passwordChange(oldUser.getPassword(), newUser.getPassword())
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    @Override
    public boolean passwordSend(@NonNull DisposableCompletableObserver observer, @NonNull final Usuario usuario)
    {
        Timber.d("passwordSend()");
        return getSubscriptions().add(
                usuarioDao.passwordSend(usuario.getUserName())
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}
