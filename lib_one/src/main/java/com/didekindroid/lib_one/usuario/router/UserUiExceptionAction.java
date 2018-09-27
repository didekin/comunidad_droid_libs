package com.didekindroid.lib_one.usuario.router;

import android.app.Activity;
import android.support.annotation.NonNull;

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
import static com.didekindroid.lib_one.security.AuthTkCacher.AuthTkCacherExceptionMsg.AUTH_HEADER_WRONG;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.BAD_REQUEST;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.FIREBASE_SERVICE_NOT_AVAILABLE;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.PASSWORD_NOT_SENT;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.TOKEN_ENCRYP_DECRYP_ERROR;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.UNAUTHORIZED;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.UNAUTHORIZED_TX_TO_USER;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.USERCOMU_WRONG_INIT;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.USER_COMU_NOT_FOUND;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.USER_DATA_NOT_INSERTED;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.USER_DATA_NOT_MODIFIED;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.USER_DUPLICATE;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.USER_NOT_FOUND;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.USER_WRONG_INIT;
import static java.util.Collections.unmodifiableSet;
import static java.util.EnumSet.of;

/**
 * User: pedro@didekin
 * Date: 09/01/17
 * Time: 13:13
 */

public enum UserUiExceptionAction implements UiExceptionActionIf {

    firebase_service_unavailable(
            of(FIREBASE_SERVICE_NOT_AVAILABLE),
            R.string.no_internet_conn_toast,
            null) {
        @Override
        public void handleExceptionInUi(@NonNull Activity activity)
        {
            Timber.d("handleExceptionInUi()");
            showToast(activity);
        }
    },
    show_login_noUser(
            of(
                    BAD_REQUEST,
                    USERCOMU_WRONG_INIT,
                    USER_COMU_NOT_FOUND,
                    USER_DATA_NOT_INSERTED,
                    USER_DUPLICATE,
                    USER_NOT_FOUND,
                    USER_WRONG_INIT,
                    USER_DATA_NOT_MODIFIED),
            R.string.user_without_signedUp,
            LoginAc.class),
    show_login_unauthorized(
            of(
                    TOKEN_ENCRYP_DECRYP_ERROR,
                    UNAUTHORIZED,
                    UNAUTHORIZED_TX_TO_USER),
            R.string.user_with_token_null,
            LoginAc.class),
    show_login_no_authHeader(
            of(AUTH_HEADER_WRONG),
            show_login_unauthorized.getResourceIdForToast(),
            show_login_unauthorized.getAcToGo()),
    show_userData_wrongMail(
            of(PASSWORD_NOT_SENT),
            R.string.user_email_wrong,
            UserDataAc.class),;

    // ==========================  Static members ============================

    public static final Map<String, UiExceptionActionIf> userExceptionMsgMap = new HashMap<>(values().length * 3);

    static {
        for (UserUiExceptionAction action : values()) {
            for (ExceptionMsgIf message : action.exceptionMsgSet) {
                userExceptionMsgMap.put(message.getHttpMessage(), action);
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

    public int getResourceIdForToast()
    {
        return resourceIdForToast;
    }

    @Override
    public void handleExceptionInUi(@NonNull Activity activity)
    {
        handleExceptionInUi(activity, null, FLAG_ACTIVITY_NEW_TASK);
    }

    public Class<? extends Activity> getAcToGo()
    {
        return acToGo;
    }
}
