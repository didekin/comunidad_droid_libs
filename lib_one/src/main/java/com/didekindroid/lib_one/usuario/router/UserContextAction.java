package com.didekindroid.lib_one.usuario.router;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.didekindroid.lib_one.api.router.ContextualNameIf;
import com.didekindroid.lib_one.api.router.RouterActionIf;
import com.didekindroid.lib_one.usuario.LoginAc;
import com.didekindroid.lib_one.usuario.PasswordSentDialog;
import com.didekindroid.lib_one.util.MuteActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

import static com.didekindroid.lib_one.usuario.router.UserContextName.default_reg_user;
import static com.didekindroid.lib_one.usuario.router.UserContextName.new_comu_user_usercomu_just_registered;
import static com.didekindroid.lib_one.usuario.router.UserContextName.new_user_usercomu_just_registered;
import static com.didekindroid.lib_one.usuario.router.UserContextName.pswd_just_sent_to_user;
import static com.didekindroid.lib_one.usuario.router.UserContextName.user_name_just_modified;
import static com.didekindroid.lib_one.usuario.UsuarioAssertionMsg.user_name_should_be_initialized;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.usuario_object;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static java.util.EnumSet.of;

/**
 * User: pedro@didekin
 * Date: 05/02/2018
 * Time: 16:37
 */
public enum UserContextAction implements RouterActionIf {

    // Usuario
    login_from_default(of(default_reg_user), LoginAc.class),
    login_from_user(of(pswd_just_sent_to_user), LoginAc.class),
    showPswdSentMessage(of(
            new_comu_user_usercomu_just_registered,
            new_user_usercomu_just_registered,
            user_name_just_modified), MuteActivity.class) {
        @Override
        public void initActivity(@NonNull Activity activity, @Nullable Bundle bundle, int flags)
        {
            Timber.d("initActivity(), three params.");
            assertTrue(bundle != null
                    && bundle.getSerializable(usuario_object.key) != null, user_name_should_be_initialized);
            DialogFragment newFragment = PasswordSentDialog.newInstance(bundle);
            newFragment.show(activity.getFragmentManager(), "passwordMailDialog");
        }
    },
    ;

    // ==========================  Static members ============================

    public static final Map<ContextualNameIf, RouterActionIf> userContextAcMap = new HashMap<>(values().length * 3);

    static {
        for (UserContextAction action : values()) {
            for (ContextualNameIf contextualName : action.contextualNmSet) {
                userContextAcMap.put(contextualName, action);
            }
        }
    }

    /* ==========================  Instance members ============================*/

    private final Set<? extends ContextualNameIf> contextualNmSet;
    private final Class<? extends Activity> activityToGo;

    UserContextAction(Set<? extends ContextualNameIf> contextualNmSet, Class<? extends Activity> activityToGo)
    {
        this.contextualNmSet = contextualNmSet;
        this.activityToGo = activityToGo;
    }

    public Class<? extends Activity> getAcToGo()
    {
        return activityToGo;
    }
}