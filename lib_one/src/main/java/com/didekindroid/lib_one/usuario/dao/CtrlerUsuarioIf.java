package com.didekindroid.lib_one.usuario.dao;

import android.support.annotation.NonNull;

import com.didekindroid.lib_one.api.ControllerIf;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.model.usuario.Usuario;

import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * User: pedro@didekin
 * Date: 31/10/2017
 * Time: 11:22
 */

public interface CtrlerUsuarioIf extends ControllerIf {

    boolean passwordChange(DisposableCompletableObserver observer, Usuario oldUser, Usuario newUser) throws UiException;

    boolean deleteMe(DisposableCompletableObserver observer) throws UiException;

    boolean getUserData(DisposableSingleObserver<Usuario> observer) throws UiException;

    boolean modifyUserName(DisposableSingleObserver<Boolean> observer, Usuario newUser) throws UiException;

    boolean modifyUserAlias(DisposableSingleObserver<Boolean> observer, Usuario newUser) throws UiException;

    boolean passwordSend(@NonNull DisposableCompletableObserver observer, @NonNull Usuario usuario);

    boolean login(@NonNull DisposableCompletableObserver observer, @NonNull Usuario usuario);
}
