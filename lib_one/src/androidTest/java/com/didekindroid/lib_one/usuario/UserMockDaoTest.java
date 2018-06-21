package com.didekindroid.lib_one.usuario;

import android.support.test.runner.AndroidJUnit4;

import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import retrofit2.Response;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http;
import static com.didekindroid.lib_one.usuario.UserMockDao.usuarioMockDao;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regUserComuWithTkCache;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static com.didekinlib.http.usuario.TkValidaPatterns.tkEncrypted_direct_symmetricKey_REGEX;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 10/11/2017
 * Time: 17:34
 */
@RunWith(AndroidJUnit4.class)
public class UserMockDaoTest {

    @Before
    public void setUp()
    {
        initSec_Http(getTargetContext());
    }

    @Test
    public void test_DeleteUser()
    {
        assertThat(regUserComuWithTkCache(comu_real_rodrigo), notNullValue());
        // Exec, check.
        usuarioMockDao.deleteUser(user_crodrigo.getUserName()).map(Response::body).test().assertResult(true);
    }

    @Test
    public void testRegComuAndUserAndUserComu()
    {
        usuarioMockDao.regComuAndUserAndUserComu(comu_real_rodrigo)
                .map(Response::body)
                .test()
                .assertOf(
                        testObserver -> assertThat(tkEncrypted_direct_symmetricKey_REGEX.isPatternOk(testObserver.values().get(0)), is(true))
                );
        cleanOneUser(user_crodrigo.getUserName());
    }
}