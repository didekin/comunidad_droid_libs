package com.didekindroid.lib_one.usuario;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.api.AbsCompletableObserver;
import com.didekindroid.lib_one.api.AbstractSingleObserver;
import com.didekindroid.lib_one.api.Viewer;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.usuario.dao.CtrlerUsuario;
import com.didekinlib.model.usuario.Usuario;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

import timber.log.Timber;

import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.lib_one.usuario.router.UserContextName.pswd_just_modified;
import static com.didekindroid.lib_one.usuario.router.UserContextName.pswd_just_sent_to_user;
import static com.didekindroid.lib_one.util.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.lib_one.util.UiUtil.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UiUtil.getUiExceptionFromThrowable;
import static com.didekindroid.lib_one.util.UiUtil.makeToast;
import static com.didekinlib.model.common.dominio.ValidDataPatterns.PASSWORD;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.BAD_REQUEST;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.PASSWORD_NOT_SENT;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.USER_NOT_FOUND;

/**
 * User: pedro@didekin
 * Date: 21/03/17
 * Time: 20:08
 * <p>
 * Preconditions:
 * 1. An intent is received with the userName.
 */
public final class ViewerPasswordChange extends Viewer<View, CtrlerUsuario> {

    final AtomicReference<UsuarioBean> usuarioBean;
    final AtomicReference<Usuario> oldUserPswd;
    final AtomicReference<String> userName;

    private ViewerPasswordChange(PasswordChangeAc activity)
    {
        super(activity.acView, activity, null);
        usuarioBean = new AtomicReference<>(null);
        oldUserPswd = new AtomicReference<>(null);
        userName = new AtomicReference<>(null);
        if (activity.getIntent().hasExtra(user_name.key)) {
            userName.set(activity.getIntent().getStringExtra(user_name.key));
        }
    }

    static ViewerPasswordChange newViewerPswdChange(PasswordChangeAc activity)
    {
        Timber.d("newViewerPswdChange()");
        final ViewerPasswordChange instance = new ViewerPasswordChange(activity);
        instance.setController(new CtrlerUsuario());
        return instance;
    }

    // .............................. ViewerIf ..................................

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");

        // Check for userName; if there isn't one, retrieve it from DB remote.
        if (userName.get() == null) {
            controller.getUserData(new AbstractSingleObserver<Usuario>(this) {
                @Override
                public void onSuccess(Usuario usuario)
                {
                    processBackUserDataLoaded(usuario);
                }
            });
        }

        Button modifyButton = view.findViewById(R.id.password_change_ac_button);
        modifyButton.setOnClickListener(
                v -> {
                    if (checkLoginData()) {
                        controller.passwordChange(
                                new AbsCompletableObserver(this) {
                                    @Override
                                    public void onComplete()
                                    {
                                        Timber.d("onComplete(), Thread: %s", Thread.currentThread().getName());
                                        makeToast(activity, R.string.password_remote_change);
                                        getContextualRouter().getActionFromContextNm(pswd_just_modified).initActivity(activity);
                                    }
                                },
                                oldUserPswd.get(),
                                usuarioBean.get().getUsuario()
                        );
                    }
                }
        );

        Button sendPswdButton = view.findViewById(R.id.password_send_ac_button);
        sendPswdButton.setOnClickListener(
                v -> controller.passwordSend(
                        new AbsCompletableObserver(this) {
                            @Override
                            public void onComplete()
                            {
                                Timber.d("onComplete()");
                                makeToast(activity, R.string.password_new_in_login);
                                getContextualRouter().getActionFromContextNm(pswd_just_sent_to_user).initActivity(activity);
                            }
                        },
                        new Usuario.UsuarioBuilder().userName(userName.get()).build())
        );
    }

    @Override
    public void onErrorInObserver(Throwable error)
    {
        Timber.d("onErrorInObserver()");
        UiException uiException = getUiExceptionFromThrowable(error);
        String errorMsg = uiException.getErrorBean().getMessage();

        if (errorMsg.equals(USER_NOT_FOUND.getHttpMessage())
                || errorMsg.equals(PASSWORD_NOT_SENT.getHttpMessage())) {
            getExceptionRouter().getActionFromMsg(PASSWORD_NOT_SENT.getHttpMessage()).handleExceptionInUi(activity);
        } else if (errorMsg.equals(BAD_REQUEST.getHttpMessage())) {
            makeToast(activity, R.string.password_wrong);
        } else {
            getExceptionRouter().getActionFromMsg(uiException.getErrorHtppMsg()).handleExceptionInUi(activity);
        }
    }

    @SuppressWarnings("WeakerAccess")
    void processBackUserDataLoaded(Usuario usuario)
    {
        Timber.d("processBackUserDataLoaded()");
        userName.compareAndSet(null, usuario.getUserName());
    }

    boolean checkLoginData()
    {
        Timber.i("checkLoginData()");

        usuarioBean.set(new UsuarioBean(userName.get(), null, getPswdDataFromView()[0], getPswdDataFromView()[1]));
        oldUserPswd.set(new Usuario.UsuarioBuilder().userName(userName.get()).password(getPswdDataFromView()[2]).build());
        StringBuilder errorBuilder = getErrorMsgBuilder(activity);

        if (!usuarioBean.get().validateUserNamePswd(activity.getResources(), errorBuilder)) {
            makeToast(activity, errorBuilder.toString());
            return false;
        }
        if (!PASSWORD.isPatternOk(oldUserPswd.get().getPassword())) {
            makeToast(activity, R.string.password_wrong);
            return false;
        }
        return checkInternetConnected(activity);
    }

    @NonNull
    String[] getPswdDataFromView()
    {
        Timber.d("getPswdDataFromView()");
        return new String[]{
                ((EditText) view.findViewById(R.id.reg_usuario_password_ediT)).getText().toString(),
                ((EditText) view.findViewById(R.id.reg_usuario_password_confirm_ediT)).getText().toString(),
                ((EditText) view.findViewById(R.id.password_validation_ediT)).getText().toString()
        };
    }
}
