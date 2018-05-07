package com.didekindroid.lib_one.usuario;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.exception.UiException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static com.didekindroid.lib_one.usuario.UsuarioMockDao.usuarioMockDao;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 10/11/2017
 * Time: 17:34
 */
@RunWith(AndroidJUnit4.class)
public class UsuarioMockDaoTest {

    @Before
    public void setUp()
    {
        initSec_Http(getTargetContext());
    }

    @Test
    public void test_DeleteUser() throws IOException
    {
        assertThat(usuarioMockDao.regComuAndUserAndUserComu(UserTestData.comu_real_rodrigo).execute().body(), is(true));
        assertThat(usuarioMockDao.deleteUser(user_crodrigo.getUserName()).execute().body(), is(true));
    }

    @Test
    public void testRegComuAndUserAndUserComu() throws IOException, UiException
    {
        assertThat(usuarioMockDao.regComuAndUserAndUserComu(UserTestData.comu_real_rodrigo).execute().body(), is(true));
        cleanOneUser(user_crodrigo);
    }
}