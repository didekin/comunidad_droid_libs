package com.didekindroid.lib_one.usuario;

import com.didekindroid.lib_one.accesorio.ConfidencialidadAcTest;
import com.didekindroid.lib_one.usuario.dao.CtrlerUsuario_Test;
import com.didekindroid.lib_one.usuario.dao.UsuarioDaoTest;
import com.didekindroid.lib_one.usuario.dao.UsuarioObservableTest;
import com.didekindroid.lib_one.usuario.notification.CtrlerNotifyTokenTest;
import com.didekindroid.lib_one.usuario.notification.ViewerNotifyTokenTest;
import com.didekindroid.lib_one.usuario.router.UserContextActionTest;
import com.didekindroid.lib_one.usuario.router.UserMnActionTest;
import com.didekindroid.lib_one.usuario.router.UserUiExceptionActionTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 18/02/17
 * Time: 12:33
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        // usuario.accesorio
        ConfidencialidadAcTest.class,
        // usuario.dao.
        CtrlerUsuario_Test.class,
        UsuarioObservableTest.class,
        UsuarioDaoTest.class,
        // usuario.notification.
        CtrlerNotifyTokenTest.class,
        ViewerNotifyTokenTest.class,
        // usuario.router
        UserContextActionTest.class,
        UserMnActionTest.class,
        UserUiExceptionActionTest.class,
        // usuario.
        DeleteMeAcTest.class,
        LoginAcTest.class,
        PasswordChangeTest.class,
        UserDataAcTest.class,
        UsuarioMockDaoTest.class,
        UsuarioBeanTests.class,
        ViewerLoginTest.class,
        ViewerPasswordChangeTest.class,
        ViewerRegUserFrTest.class,
        ViewerUserDataTest.class,
        ViewerUserDrawerTest.class,
})
public class UsuarioSuite {
}
