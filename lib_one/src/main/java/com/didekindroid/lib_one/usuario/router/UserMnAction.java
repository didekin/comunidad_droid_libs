package com.didekindroid.lib_one.usuario.router;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.accesorio.ConfidencialidadAc;
import com.didekindroid.lib_one.api.router.MnRouterActionIf;
import com.didekindroid.lib_one.usuario.DeleteMeAc;
import com.didekindroid.lib_one.usuario.LoginAc;
import com.didekindroid.lib_one.usuario.PasswordChangeAc;
import com.didekindroid.lib_one.usuario.UserDataAc;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static android.support.v4.app.NavUtils.getParentActivityIntent;
import static android.support.v4.app.NavUtils.navigateUpTo;
import static android.support.v4.app.NavUtils.shouldUpRecreateTask;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.usuario.router.UserContextAction.login_from_default;
import static com.didekindroid.lib_one.util.CommonAssertionMsg.parent_activity_should_be_not_null;
import static com.didekindroid.lib_one.util.CommonAssertionMsg.user_should_not_be_registered;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;


/**
 * User: pedro@didekin
 * Date: 05/02/2018
 * Time: 17:02
 */

public enum UserMnAction implements MnRouterActionIf {

    // UP.
    navigateUp(android.R.id.home, null) {
        @SuppressWarnings("ConstantConditions")
        @Override
        public void initActivity(@NonNull Activity activity)
        {
            Timber.d("doUpMenuActivity()");

            // Invariant: getParentActivityIntent() should return not null.
            Intent parentAcIntent = getParentActivityIntent(activity);
            assertTrue(parentAcIntent != null, parent_activity_should_be_not_null);

            // Check if user presses the Up button after entering your activity from another app's task.
            if (shouldUpRecreateTask(activity, parentAcIntent)) {
                Intent intent =
                        new Intent(
                                activity,
                                secInitializer.get().getTkCacher().isRegisteredUser() ? login_from_default.getAcToGo() : routerInitializer.get().getDefaultAc()
                        );
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                return;
            }

            parentAcIntent.setFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP | FLAG_ACTIVITY_NEW_TASK);
            navigateUpTo(activity, parentAcIntent);
        }
    },
    // ACCESORIOS.
    confidencialidad_mn(R.id.confidenc_item_mn, ConfidencialidadAc.class),
    // USUARIO.
    delete_me_mn(R.id.delete_me_ac_mn, DeleteMeAc.class),
    login_mn(R.id.login_ac_mn, LoginAc.class) {
        @Override
        public void initActivity(@NonNull Activity activity)
        {
            assertTrue(!secInitializer.get().getTkCacher().isRegisteredUser(), user_should_not_be_registered);
            super.initActivity(activity);
        }
    },
    password_change_mn(R.id.password_change_ac_mn, PasswordChangeAc.class),
    user_data_mn(R.id.user_data_ac_mn, UserDataAc.class),;

    // ==========================  Static members ============================

    @SuppressLint("UseSparseArrays")
    public static final Map<Integer, MnRouterActionIf> userMnItemMap = new HashMap<>();

    static {
        for (UserMnAction menuItem : values()) {
            userMnItemMap.put(menuItem.mnItemRsId, menuItem);
        }
    }

    // ==========================  Instance members ============================

    private final Class<? extends Activity> acToGo;
    private final int mnItemRsId;

    UserMnAction(int menuItemRsIdIn, Class<? extends Activity> classToGo)
    {
        mnItemRsId = menuItemRsIdIn;
        acToGo = classToGo;
    }

    @Override
    public int getMnItemRsId()
    {
        return mnItemRsId;
    }

    public Class<? extends Activity> getAcToGo()
    {
        return acToGo;
    }
}
