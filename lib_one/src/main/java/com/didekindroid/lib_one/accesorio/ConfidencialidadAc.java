package com.didekindroid.lib_one.accesorio;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.didekindroid.lib_one.R;

import timber.log.Timber;

import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.usuario.router.UserMnAction.navigateUp;
import static com.didekindroid.lib_one.util.UiUtil.doToolBar;

public class ConfidencialidadAc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confidencialidad_ac);
        doToolBar(this, true);
        // Adding Floating Action Button to bottom right of main view
        FloatingActionButton fab = findViewById(R.id.confidencialidad_fab);
        fab.setOnClickListener(v -> navigateUp.initActivity(this));
    }

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
