package com.didekindroid.lib_one.usuario;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.api.AbstractSingleObserver;
import com.didekindroid.lib_one.api.Viewer;
import com.didekindroid.lib_one.usuario.dao.CtrlerUsuario;
import com.didekindroid.lib_one.usuario.dao.CtrlerUsuarioIf;
import com.didekinlib.model.usuario.Usuario;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

import timber.log.Timber;

import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.usuario_object;
import static com.didekindroid.lib_one.usuario.ViewerUserDataIf.UserChangeToMake.alias_only;
import static com.didekindroid.lib_one.usuario.ViewerUserDataIf.UserChangeToMake.nothing;
import static com.didekindroid.lib_one.usuario.ViewerUserDataIf.UserChangeToMake.userName;
import static com.didekindroid.lib_one.usuario.router.UserContextName.user_alias_just_modified;
import static com.didekindroid.lib_one.usuario.router.UserContextName.user_name_just_modified;
import static com.didekindroid.lib_one.util.CommonAssertionMsg.user_should_be_registered;
import static com.didekindroid.lib_one.util.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static com.didekindroid.lib_one.util.UiUtil.getContetViewInAc;
import static com.didekindroid.lib_one.util.UiUtil.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UiUtil.getUiExceptionFromThrowable;
import static com.didekindroid.lib_one.util.UiUtil.makeToast;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.BAD_REQUEST;

/**
 * User: pedro@didekin
 * Date: 22/03/17
 * Time: 10:27
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class ViewerUserData extends Viewer<View, CtrlerUsuarioIf> implements ViewerUserDataIf {

    private final EditText emailView;
    private final EditText aliasView;
    private final EditText passwordView;
    private final AtomicReference<UsuarioBean> usuarioBean;
    private final AtomicReference<Usuario> oldUser;
    final AtomicReference<Usuario> newUser;

    private ViewerUserData(View view, Activity activity)
    {
        super(view, activity, null);
        emailView = view.findViewById(R.id.reg_usuario_email_editT);
        aliasView = view.findViewById(R.id.reg_usuario_alias_ediT);
        passwordView = view.findViewById(R.id.password_validation_ediT);
        oldUser = new AtomicReference<>(null);
        newUser = new AtomicReference<>(null);
        usuarioBean = new AtomicReference<>(null);
    }

    public static ViewerUserData newViewerUserData(Activity activity)
    {
        Timber.d("newViewerUserData()");
        ViewerUserData instance = new ViewerUserData(getContetViewInAc(activity), activity);
        instance.setController(new CtrlerUsuario());
        return instance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        assertTrue(controller.isRegisteredUser(), user_should_be_registered);
        controller.getUserData(new AbstractSingleObserver<Usuario>(this) {
            @Override
            public void onSuccess(Usuario usuario)
            {
                processBackUserDataLoaded(usuario);
            }
        });
    }

    @Override
    public void processBackUserDataLoaded(@NonNull Usuario usuario)
    {
        Timber.d("processBackUserDataLoaded()");

        oldUser.set(usuario);

        Button modifyButton = view.findViewById(R.id.user_data_modif_button);
        modifyButton.setOnClickListener(v -> {
            if (checkUserData()) {
                modifyUserData(whatDataChangeToMake());
            }
        });

        emailView.setText(oldUser.get().getUserName());
        aliasView.setText(oldUser.get().getAlias());
        passwordView.setHint(R.string.user_data_ac_password_hint);
    }

    /**
     * Preconditions:
     * 1. The user has typed and pressed modifyButton.
     */
    @Override
    public boolean checkUserData()
    {
        Timber.d("checkUserData()");

        usuarioBean.set(new UsuarioBean(
                emailView.getText().toString(),
                aliasView.getText().toString(),
                passwordView.getText().toString(),
                null)
        );
        Timber.d("email: %s", usuarioBean.get().getUserName());
        Timber.d("alias: %s", usuarioBean.get().getAlias());

        StringBuilder errorBuilder = getErrorMsgBuilder(activity);
        if (!usuarioBean.get().validateUserNameAliasPswd(activity.getResources(), errorBuilder)) {
            Timber.d("checkUserData(): %s", errorBuilder.toString());
            makeToast(activity, errorBuilder.toString());
            return false;
        }
        return checkInternetConnected(activity);
    }

    /**
     * Preconditions:
     * 1. Typed user data has been validated.
     */
    @Override
    public UserChangeToMake whatDataChangeToMake()
    {
        Timber.d("whatDataChangeToMake()");

        if (oldUser.get().getAlias().equals(usuarioBean.get().getAlias()) && oldUser.get().getUserName().equals(usuarioBean.get().getUserName())) {
            return nothing;
        }

        // Password in screen and old userName will be used for authentication.
        oldUser.set(new Usuario.UsuarioBuilder()
                .copyUsuario(oldUser.get())
                .password(usuarioBean.get().getPassword())
                .build());

        // Inicializo datos en nuevo usuario y añado PK.
        newUser.set(new Usuario.UsuarioBuilder()
                .copyUsuario(usuarioBean.get().getUsuario())
                .uId(oldUser.get().getuId())
                .build());

        if (!oldUser.get().getAlias().equals(newUser.get().getAlias())
                && oldUser.get().getUserName().equals(newUser.get().getUserName())) {
            return alias_only;
        }
        if (!oldUser.get().getUserName().equals(newUser.get().getUserName())) {
            return userName;
        }
        return nothing;
    }

    /**
     * @return true if the new subscription has been successfully added to the controller's set.
     */
    @Override
    public boolean modifyUserData(UserChangeToMake userChangeToMake)
    {
        Timber.d("modifyUserData()");
        switch (userChangeToMake) {
            case nothing:
                makeToast(activity, R.string.no_user_data_to_be_modified);
                return false;
            case userName:
                return modifyUserName();
            case alias_only:
                return modifyAlias();
            default:
                return false;
        }
    }

    // ================================= ViewerIf ==================================

    @SuppressWarnings("ThrowableNotThrown")
    @Override
    public void onErrorInObserver(Throwable error)
    {
        Timber.d("onErrorInObserver()");
        if (getUiExceptionFromThrowable(error).getErrorBean().getMessage().equals(BAD_REQUEST.getHttpMessage())) {
            makeToast(activity, R.string.password_wrong);
        } else {
            super.onErrorInObserver(error);
        }
    }

    /* ================================= Helpers ==================================*/

    boolean modifyUserName()
    {
        Timber.d("modifyUser()");
        return controller.modifyUser(
                new AbstractSingleObserver<Boolean>(this) {
                    @Override
                    public void onSuccess(Boolean isCompleted)
                    {
                        getContextualRouter().getActionFromContextNm(user_name_just_modified)
                                .initActivity(activity, usuario_object.getBundleForKey(newUser.get()));
                    }
                },
                newUser.get());
    }

    boolean modifyAlias()
    {
        Timber.d("modifyAlias()");
        return controller.modifyUser(
                new AbstractSingleObserver<Boolean>(this) {
                    @Override
                    public void onSuccess(Boolean isCompleted)
                    {
                        getContextualRouter().getActionFromContextNm(user_alias_just_modified).initActivity(activity);
                    }
                },
                newUser.get());
    }

    /* ================================= Getters ==================================*/

    public EditText getEmailView()
    {
        return emailView;
    }

    public EditText getAliasView()
    {
        return aliasView;
    }

    public EditText getPasswordView()
    {
        return passwordView;
    }

    public AtomicReference<UsuarioBean> getUsuarioBean()
    {
        return usuarioBean;
    }

    public AtomicReference<Usuario> getOldUser()
    {
        return oldUser;
    }

    public AtomicReference<Usuario> getNewUser()
    {
        return newUser;
    }
}
