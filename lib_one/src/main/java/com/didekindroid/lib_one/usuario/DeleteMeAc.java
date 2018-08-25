package com.didekindroid.lib_one.usuario;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.usuario.dao.CtrlerUsuario;
import com.didekindroid.lib_one.usuario.dao.CtrlerUsuarioIf;

import io.reactivex.observers.DisposableCompletableObserver;
import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.usuario.router.UserContextName.user_just_deleted;
import static com.didekindroid.lib_one.util.CommonAssertionMsg.user_should_be_registered;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static com.didekindroid.lib_one.util.UiUtil.doToolBar;
import static com.didekindroid.lib_one.util.UiUtil.getUiExceptionFromThrowable;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Unregistered user, if she chooses so. ComuSearchAc is to be showed.
 */
public class DeleteMeAc extends AppCompatActivity {

    View acView;
    CtrlerUsuarioIf controller;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        acView = getLayoutInflater().inflate(R.layout.delete_me_ac, null);
        setContentView(acView);
        doToolBar(this, true);

        Button mUnregisterButton = findViewById(R.id.delete_me_ac_unreg_button);
        mUnregisterButton.setOnClickListener(v -> controller.deleteMe(new DeleteMeSingleObserver()));
    }

    @Override
    protected void onStart()
    {
        Timber.d("onStart()");
        super.onStart();
        // Controller initialization.
        controller = new CtrlerUsuario();
        // Preconditions.
        assertTrue(controller.isRegisteredUser(), user_should_be_registered);
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        controller.clearSubscriptions();
    }

    // ============================================================
    //    .................... ACTION BAR ........................
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

    // .............................. HELPERS ..................................

    @SuppressWarnings("WeakerAccess")
    class DeleteMeSingleObserver extends DisposableCompletableObserver {

        @Override
        public void onComplete()
        {
            Timber.d("onSuccess(), Thread: %s", Thread.currentThread().getName());
            routerInitializer.get().getContextRouter().getActionFromContextNm(user_just_deleted)
                    .initActivity(DeleteMeAc.this, null, FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
        }

        @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
        @Override
        public void onError(Throwable e)
        {
            Timber.d("onErrorObserver, Thread: %s", Thread.currentThread().getName());
            routerInitializer.get().getExceptionRouter().getActionFromMsg(getUiExceptionFromThrowable(e).getErrorHtppMsg())
                    .handleExceptionInUi(DeleteMeAc.this);
        }
    }
}
