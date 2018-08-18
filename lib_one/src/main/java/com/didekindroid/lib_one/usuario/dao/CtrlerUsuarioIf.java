package com.didekindroid.lib_one.usuario.dao;

import android.support.annotation.NonNull;

import com.didekindroid.lib_one.api.ControllerIf;
import com.didekinlib.model.usuario.Usuario;

import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * User: pedro@didekin
 * Date: 31/10/2017
 * Time: 11:22
 */

public interface CtrlerUsuarioIf extends ControllerIf {

    boolean passwordChange(DisposableCompletableObserver observer, Usuario oldUser, Usuario newUser);

    boolean deleteMe(DisposableCompletableObserver observer);

    boolean getAppIdToken(DisposableSingleObserver<String> observer);

    boolean getUserData(DisposableSingleObserver<Usuario> observer);

    boolean modifyUserName(DisposableSingleObserver<Boolean> observer, Usuario newUser);

    boolean modifyUserAlias(DisposableSingleObserver<Boolean> observer, Usuario newUser);

    boolean passwordSend(@NonNull DisposableCompletableObserver observer, @NonNull Usuario usuario);

    boolean login(@NonNull DisposableCompletableObserver observer, @NonNull Usuario usuario);
}
