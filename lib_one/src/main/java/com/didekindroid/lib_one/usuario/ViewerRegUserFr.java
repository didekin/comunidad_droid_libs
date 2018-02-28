package com.didekindroid.lib_one.usuario;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.api.Controller;
import com.didekindroid.lib_one.api.ParentViewerIf;
import com.didekindroid.lib_one.api.Viewer;
import com.didekinlib.model.usuario.Usuario;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 23/05/17
 * Time: 09:56
 */

@SuppressWarnings("unused")
public class ViewerRegUserFr extends Viewer<View, Controller> {

    ViewerRegUserFr(View view, Activity activity, @NonNull ParentViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    // ==================================== ViewerIf ===================================

    // ==================================== Helpers ====================================

    public Usuario getUserFromViewer(StringBuilder errorBuilder)
    {
        Timber.d("getUserFromViewer()");

        UsuarioBean usuarioBean = new UsuarioBean(
                ((EditText) view.findViewById(R.id.reg_usuario_email_editT)).getText()
                        .toString(),
                ((EditText) view.findViewById(R.id.reg_usuario_alias_ediT)).getText()
                        .toString(),
                null, null
        );
        if (usuarioBean.validateUserNameAlias(activity.getResources(), errorBuilder)) {
            return usuarioBean.getUsuario();
        } else {
            return null;
        }
    }
}
