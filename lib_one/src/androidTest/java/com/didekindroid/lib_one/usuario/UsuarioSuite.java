package com.didekindroid.lib_one.usuario;

import com.didekindroid.lib_one.usuario.dao.CtrlerUsuario_Test;
import com.didekindroid.lib_one.usuario.dao.UsuarioDaoTest;
import com.didekindroid.lib_one.usuario.dao.UsuarioObservableTest;
import com.didekindroid.lib_one.usuario.notification.CtrlerNotifyTokenTest;
import com.didekindroid.lib_one.usuario.notification.ViewerNotifyTokenTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 18/02/17
 * Time: 12:33
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        // usuario.dao.
        CtrlerUsuario_Test.class,
        UsuarioObservableTest.class,
        UsuarioDaoTest.class,
        // usuario.notification.
        CtrlerNotifyTokenTest.class,
        ViewerNotifyTokenTest.class,
        // usuario.
        UsuarioMockDaoTest.class,
        UsuarioBeanTests.class,
})
public class UsuarioSuite {
}
