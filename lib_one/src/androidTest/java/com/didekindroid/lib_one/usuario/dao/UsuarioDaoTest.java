package com.didekindroid.lib_one.usuario.dao;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.security.AuthTkCacher;
import com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.security.AuthTkCacher.AuthTkCacherExceptionMsg.AUTH_HEADER_WRONG;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_NOTHING;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_RODRIGO;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regGetUserComu;
import static com.didekindroid.lib_one.usuario.UserTestData.regUserComuWithTkCache;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static com.didekindroid.lib_one.usuario.dao.UsuarioDao.usuarioDaoRemote;
import static com.didekinlib.http.usuario.TkValidaPatterns.tkEncrypted_direct_symmetricKey_REGEX;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_NOT_FOUND;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 07/09/15
 * Time: 11:08
 */
@RunWith(AndroidJUnit4.class)
public class UsuarioDaoTest {

    private CleanUserEnum whatClean = CLEAN_NOTHING;
    private AuthTkCacher tkCacher;

    @Before
    public void setUp()
    {
        initSec_Http(getTargetContext());
        tkCacher = (AuthTkCacher) secInitializer.get().getTkCacher();
    }

    @After
    public void cleaningUp()
    {
        cleanOptions(whatClean);
    }

//    ========================= INTERFACE TESTS =======================

    @Test
    public void testDeleteUser_1()
    {
        /*Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.*/
        assertThat(regUserComuWithTkCache(comu_real_rodrigo), notNullValue());
        // Exec, check.
        usuarioDaoRemote.deleteUser().test();
        assertThat(tkCacher.isRegisteredCache(), is(false));
    }

    @Test
    public void testDeleteUser_2()
    {
        // No valid authHeader because not registered user.
        usuarioDaoRemote.deleteUser().test().assertError(
                uiexception -> UiException.class.cast(uiexception).getErrorHtppMsg().equals(AUTH_HEADER_WRONG.getHttpMessage())
        );
    }

    @Test
    public void testGetGcmToken() throws UiException
    {
        whatClean = CLEAN_RODRIGO;

        Usuario userDb = regGetUserComu(comu_real_rodrigo);
        usuarioDaoRemote.getGcmToken().test().assertResult(userDb.getGcmToken());
    }

    @Test
    public void testGetUserData()
    {
        whatClean = CLEAN_RODRIGO;

        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        assertThat(regUserComuWithTkCache(comu_real_rodrigo), notNullValue());
        usuarioDaoRemote.getUserData().test()
                .assertOf(tester -> assertThat(tester.values().get(0).getUserName(), is(user_crodrigo.getUserName())));
    }

    @Test
    public void testLogin_1()
    {
        // User not in DB.
        usuarioDaoRemote.login("user@notfound.com", "password_wrong").test()
                .assertError(
                        exception -> UiException.class.cast(exception).getErrorBean().getMessage().equals(USER_NOT_FOUND.getHttpMessage())
                );
        assertThat(tkCacher.isRegisteredCache(), is(false));
    }

    @Test
    public void testLogin_2() throws InterruptedException, UiException
    {
        whatClean = CLEAN_RODRIGO;
        Usuario userDb = regGetUserComu(comu_real_rodrigo);
        // Pongo a null el authToken para ver el cambio:
        tkCacher.updateAuthToken(null);
        usuarioDaoRemote.login(user_crodrigo.getUserName(), user_crodrigo.getPassword()).test().await();
        assertThat(tkEncrypted_direct_symmetricKey_REGEX.isPatternOk(tkCacher.getAuthToken()), is(true));
        assertThat(tkCacher.isRegisteredCache(), is(true));
        assertThat(tkCacher.isGcmTokenSentServer(), is(true));
    }

    @Test
    public void testmodifyGcmToken() throws InterruptedException, UiException
    {
        whatClean = CLEAN_RODRIGO;
        Usuario userDb = regGetUserComu(comu_real_rodrigo);

        usuarioDaoRemote.modifyGcmToken("new_crodrigo_gcmtoken").test().await();
        assertThat(tkEncrypted_direct_symmetricKey_REGEX.isPatternOk(tkCacher.getAuthToken()),
                is(true));
        assertThat(tkCacher.isGcmTokenSentServer(), is(true));
    }

    @Test
    public void testmodifyAlias() throws UiException
    {
        whatClean = CLEAN_RODRIGO;

        // Changed alias; not userName.
        Usuario usuarioIn = new Usuario.UsuarioBuilder()
                .copyUsuario(regGetUserComu(comu_real_rodrigo))
                .alias("new_alias")
                .build();

        usuarioDaoRemote.modifyUserAlias(usuarioIn).test().assertResult(TRUE);
    }

    @Test
    public void testmodifyUserName() throws InterruptedException, UiException
    {
        Usuario usuarioIn = new Usuario.UsuarioBuilder()
                .copyUsuario(regGetUserComu(comu_real_rodrigo))
                .userName(USER_DROID.getUserName())
                .build();

        usuarioDaoRemote.modifyUserName(usuarioIn).test().await();
        assertThat(tkCacher.isRegisteredCache(), is(true));

        cleanOneUser(USER_DROID.getUserName());
    }

    @Test
    public void testPasswordChange() throws InterruptedException, UiException
    {
        whatClean = CLEAN_RODRIGO;

        Usuario usuarioIn = regGetUserComu(comu_real_rodrigo);
        usuarioDaoRemote.passwordChange(user_crodrigo.getPassword(), "new_password").test().await();
        assertThat(tkCacher.getAuthToken(), allOf(
                not(is(usuarioIn.getTokenAuth())),
                notNullValue()
        ));
    }

    @Test
    public void test_PasswordSend()
    {
        whatClean = CLEAN_RODRIGO;
        regUserComuWithTkCache(comu_real_rodrigo);
        usuarioDaoRemote.passwordSend(user_crodrigo.getUserName()).test().assertComplete();
        assertThat(tkCacher.getAuthToken(), nullValue());
    }
}