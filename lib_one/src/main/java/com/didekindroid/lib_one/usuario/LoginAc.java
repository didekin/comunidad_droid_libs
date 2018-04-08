package com.didekindroid.lib_one.usuario;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.lib_one.R;

import timber.log.Timber;

import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.lib_one.usuario.ViewerLogin.newViewerLogin;
import static com.didekindroid.lib_one.util.UiUtil.doToolBar;

/**
 * User: pedro
 * Date: 15/12/14
 * Time: 10:04
 * <p>
 * Preconditions:
 * 1. The user may not be registered: there is not tokenCache initialized and it is not marked as 'registered'.
 * 2. There may be an intent with a userName (after registering, p.e.).
 * Results:
 * 1a. If successful, the activity ComuSearchAc is presented and the security data are updated.
 * 1b. If the userName doesn't exist, the user is invited to register.
 * 1c. If the userName exists, but the passowrd is not correct, after three failed intents,  a new passord is sent
 * by mail, after her confirmation.
 */
public class LoginAc extends AppCompatActivity {

    View acView;
    ViewerLogin viewerLogin;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Timber.i("Entered onCreate()");
        super.onCreate(savedInstanceState);

        acView = getLayoutInflater().inflate(R.layout.login_ac, null);
        setContentView(acView);
        doToolBar(this, true);

        viewerLogin = newViewerLogin(this);
        viewerLogin.doViewInViewer(savedInstanceState, getIntent().hasExtra(user_name.key) ? getIntent().getStringExtra(user_name.key) : null);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedState)
    {
        Timber.d("onSaveInstanceState()");
        super.onSaveInstanceState(savedState);
        viewerLogin.saveState(savedState);
    }

    @Override
    protected void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        viewerLogin.clearSubscriptions();
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");
        int resourceId = item.getItemId();

        switch (resourceId) {
            case android.R.id.home:
                routerInitializer.get().getMnRouter().getActionFromMnItemId(resourceId).initActivity(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
