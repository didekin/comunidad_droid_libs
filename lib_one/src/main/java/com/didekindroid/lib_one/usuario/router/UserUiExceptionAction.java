package com.didekindroid.lib_one.usuario.router;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.api.router.UiExceptionActionIf;
import com.didekindroid.lib_one.usuario.LoginAc;
import com.didekindroid.lib_one.usuario.UserDataAc;
import com.didekinlib.http.exception.ExceptionMsgIf;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekindroid.lib_one.util.UiUtil.makeToast;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.BAD_REQUEST;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.PASSWORD_NOT_SENT;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.TOKEN_NULL;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.UNAUTHORIZED;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.UNAUTHORIZED_TX_TO_USER;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USERCOMU_WRONG_INIT;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_COMU_NOT_FOUND;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_DATA_NOT_INSERTED;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_DATA_NOT_MODIFIED;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_NAME_DUPLICATE;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_WRONG_INIT;
import static java.util.Collections.unmodifiableSet;
import static java.util.EnumSet.of;

/**
 * User: pedro@didekin
 * Date: 09/01/17
 * Time: 13:13
 */

public enum UserUiExceptionAction implements UiExceptionActionIf {

    show_login_noUser(
            of(BAD_REQUEST, USERCOMU_WRONG_INIT, USER_COMU_NOT_FOUND, USER_DATA_NOT_INSERTED,
                    USER_NAME_DUPLICATE, USER_NAME_NOT_FOUND, USER_WRONG_INIT),
            R.string.user_without_signedUp,
            LoginAc.class),
    show_login_tokenNull(
            of(TOKEN_NULL, UNAUTHORIZED, UNAUTHORIZED_TX_TO_USER, USER_DATA_NOT_MODIFIED),
            R.string.user_with_token_null,
            LoginAc.class),
    show_userData_wrongMail(of(PASSWORD_NOT_SENT),
            R.string.user_email_wrong,
            UserDataAc.class),;

    // ==========================  Static members ============================

    public static final Map<String, UiExceptionActionIf> exceptionMsgMap = new HashMap<>(values().length * 3);

    static {
        for (UserUiExceptionAction action : values()) {
            for (ExceptionMsgIf message : action.exceptionMsgSet) {
                exceptionMsgMap.put(message.getHttpMessage(), action);
            }
        }
    }

    /* ==========================  Instance members ============================*/

    private final int resourceIdForToast;
    private final Class<? extends Activity> acToGo;
    private final Set<? extends ExceptionMsgIf> exceptionMsgSet;

    UserUiExceptionAction(EnumSet<? extends ExceptionMsgIf> httpMessages, int resourceString, Class<? extends Activity> acClassToGo)
    {
        exceptionMsgSet = unmodifiableSet(httpMessages);
        resourceIdForToast = resourceString;
        acToGo = acClassToGo;
    }

    @Override
    public void initActivity(@NonNull Activity activity, @Nullable Bundle bundle)
    {
        Timber.d("initActivity(), two parameters.");
        if (resourceIdForToast > 0) {
            makeToast(activity, resourceIdForToast);
        }
        initActivity(activity, bundle, FLAG_ACTIVITY_NEW_TASK);
    }

    public int getResourceIdForToast()
    {
        return resourceIdForToast;
    }

    public Class<? extends Activity> getAcToGo()
    {
        return acToGo;
    }
}
