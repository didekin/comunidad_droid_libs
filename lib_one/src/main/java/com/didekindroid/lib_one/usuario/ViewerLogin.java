package com.didekindroid.lib_one.usuario;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.api.Viewer;
import com.didekindroid.lib_one.api.ViewerIf;
import com.didekindroid.lib_one.api.router.RouterInitializerIf;
import com.didekindroid.lib_one.usuario.dao.CtrlerUsuario;
import com.didekinlib.model.usuario.Usuario;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableCompletableObserver;
import timber.log.Timber;

import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.login_counter_atomic_int;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.usuario_object;
import static com.didekindroid.lib_one.usuario.ViewerLogin.PasswordMailDialog.newInstance;
import static com.didekindroid.lib_one.usuario.router.UserContextName.login_just_done;
import static com.didekindroid.lib_one.util.CommonAssertionMsg.bean_fromView_should_be_initialized;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static com.didekindroid.lib_one.util.UiUtil.checkInternet;
import static com.didekindroid.lib_one.util.UiUtil.getContetViewInAc;
import static com.didekindroid.lib_one.util.UiUtil.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UiUtil.getUiExceptionFromThrowable;
import static com.didekindroid.lib_one.util.UiUtil.makeToast;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.PASSWORD_WRONG;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_NOT_FOUND;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_WRONG_INIT;

/**
 * User: pedro@didekin
 * Date: 21/03/17
 * Time: 12:05
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public final class ViewerLogin extends Viewer<View, CtrlerUsuario> {

    public final AtomicReference<UsuarioBean> usuarioBean;
    private AtomicInteger counterWrong;

    protected ViewerLogin(Activity activity)
    {
        super(getContetViewInAc(activity), activity, null);
        counterWrong = new AtomicInteger(0);
        usuarioBean = new AtomicReference<>(null);
    }

    protected ViewerLogin(Activity activity, ViewerIf parentViewer, RouterInitializerIf routerInitializer)
    {
        super(getContetViewInAc(activity), activity, null, routerInitializer);
        usuarioBean = new AtomicReference<>(null);
    }

    public static ViewerLogin newViewerLogin(Activity activity)
    {
        Timber.d("newViewerLogin()");
        ViewerLogin instance = new ViewerLogin(activity);
        instance.setController(new CtrlerUsuario());
        return instance;
    }

    // .............................. ViewerIf ..................................

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");

        if (viewBean != null) {
            ((EditText) view.findViewById(R.id.reg_usuario_email_editT)).setText(String.class.cast(viewBean));
        }

        Button validateLoginButton = view.findViewById(R.id.login_ac_button);
        validateLoginButton.setOnClickListener(
                v -> {
                    Timber.d("onClick()");
                    if (checkLoginData()) {
                        controller.login(
                                new DisposableCompletableObserver() {
                                    @Override
                                    public void onComplete()
                                    {
                                        processLoginBackInView();
                                    }

                                    @Override
                                    public void onError(Throwable e)
                                    {
                                        processLoginErrorBackInView(e);
                                    }
                                },
                                usuarioBean.get().getUsuario()
                        );
                    }
                }
        );

        FloatingActionButton fab = view.findViewById(R.id.login_help_fab);
        fab.setOnClickListener(v -> {
            if (checkEmailData()) {
                showDialogAfterErrors();
            }
        });

        if (savedState != null) {
            counterWrong.set(savedState.getInt(login_counter_atomic_int.key));
        }
    }

    public boolean checkEmailData()
    {
        Timber.d("checkEmailData()");
        usuarioBean.set(new UsuarioBean(getLoginDataFromView()[0], null, null, null));

        StringBuilder errorBuilder = getErrorMsgBuilder(activity);
        if (!usuarioBean.get().validateUserName(activity.getResources(), errorBuilder)) {
            makeToast(activity, errorBuilder.toString());
            return false;
        }
        return checkInternet(activity);
    }

    public boolean checkLoginData()
    {
        Timber.i("checkLoginData()");
        usuarioBean.set(new UsuarioBean(getLoginDataFromView()[0], null, getLoginDataFromView()[1], null));

        StringBuilder errorBuilder = getErrorMsgBuilder(activity);
        if (!usuarioBean.get().validateLoginData(activity.getResources(), errorBuilder)) {
            makeToast(activity, errorBuilder.toString());
            return false;
        }
        return checkInternet(activity);
    }

    public String[] getLoginDataFromView()
    {
        Timber.d("getLoginDataFromView()");
        return new String[]{
                ((EditText) view.findViewById(R.id.reg_usuario_email_editT)).getText().toString(),
                ((EditText) view.findViewById(R.id.reg_usuario_password_ediT)).getText().toString()
        };
    }

    public void processLoginBackInView()
    {
        Timber.d("processLoginBackInView()");
        getContextualRouter().getActionFromContextNm(login_just_done).initActivity(activity);
        activity.finish();
    }

    void processLoginErrorBackInView(Throwable error)
    {
        Timber.d("processLoginErrorBackInView()");
        String messageErr = getUiExceptionFromThrowable(error).getErrorBean().getMessage();

        if (messageErr.equals(PASSWORD_WRONG.getHttpMessage())
                || messageErr.equals(USER_NOT_FOUND.getHttpMessage())
                || messageErr.equals(USER_WRONG_INIT.getHttpMessage())) {

            int counter = counterWrong.addAndGet(1);
            Timber.d("Password wrong, counterWrong = %d%n", counter - 1);
            if (counter > 3) {
                showDialogAfterErrors();
            } else {
                makeToast(activity, R.string.password_wrong);
            }
        } else {
            super.onErrorInObserver(error);
        }
    }

    public void showDialogAfterErrors()
    {
        Timber.d("showDialogAfterErrors()");
        assertTrue(usuarioBean != null && usuarioBean.get().getUsuario() != null, bean_fromView_should_be_initialized);
        DialogFragment newFragment = newInstance(usuarioBean.get(), this);
        newFragment.show(activity.getFragmentManager(), "passwordMailDialog");
    }

    public void doDialogPositiveClick(Usuario usuario)
    {
        Timber.d("passwordSend()");
        if (usuario == null) {
            makeToast(activity, R.string.username_wrong_in_login);
            return;
        }
        controller.passwordSend(new DisposableCompletableObserver() {
            @Override
            public void onComplete()
            {
                processBackSendPswdInView();
            }

            @Override
            public void onError(Throwable e)
            {
                Timber.d("onError, message: %s", e.getMessage());
                onErrorInObserver(e);
            }
        }, usuario);
    }

    public void processBackSendPswdInView()
    {
        Timber.d("processBackSendPswdInView()");
        makeToast(activity, R.string.password_new_in_login);
        activity.recreate();
    }


    @SuppressWarnings("ThrowableNotThrown") // Recycled in a UI message.
    @Override
    public void onErrorInObserver(Throwable error)
    {
        Timber.d("onErrorInObserver()");
        if (getUiExceptionFromThrowable(error).getErrorBean().getMessage().equals(USER_NOT_FOUND.getHttpMessage())) {
            makeToast(activity, R.string.username_wrong_in_login);
        } else {
            super.onErrorInObserver(error);
        }
    }

    // =========================  LyfeCicle  =========================

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
        if (savedState == null) {
            savedState = new Bundle(1);
        }
        savedState.putInt(login_counter_atomic_int.key, counterWrong.get());
    }

    // =========================  HELPERS  =========================

    public AtomicInteger getCounterWrong()
    {
        Timber.d("getCounterWrong()");
        return counterWrong;
    }

    // ============================================================
    // ....................... SUBSCRIBERS ...................
    // ============================================================

    public static class PasswordMailDialog extends DialogFragment {

        private ViewerLogin viewerLogin;

        public static PasswordMailDialog newInstance(@NonNull UsuarioBean usuarioBean, @NonNull ViewerLogin viewerIn)
        {
            Timber.d("newInstance()");
            PasswordMailDialog dialog = new PasswordMailDialog();
            dialog.viewerLogin = viewerIn;
            Bundle bundle = new Bundle();
            bundle.putSerializable(usuario_object.key, usuarioBean.getUsuario());
            dialog.setArguments(bundle);
            return dialog;
        }

        @Override
        public AppCompatDialog onCreateDialog(Bundle savedInstanceState)
        {
            Timber.d("onCreateDialog()");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.alertDialogTheme);

            builder.setMessage(R.string.send_password_by_mail_dialog)
                    .setPositiveButton(
                            R.string.send_password_by_mail_YES,
                            (dialog, id) -> {
                                dialog.dismiss();
                                viewerLogin.doDialogPositiveClick((Usuario) getArguments().getSerializable(usuario_object.key));
                            }
                    );
            return builder.create();
        }
    }
}
