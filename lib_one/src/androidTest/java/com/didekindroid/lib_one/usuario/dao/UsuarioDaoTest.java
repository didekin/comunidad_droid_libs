package com.didekindroid.lib_one.usuario.dao;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.security.AuthTkCacher;
import com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.FirebaseInitializer.firebaseInitializer;
import static com.didekindroid.lib_one.security.AuthTkCacher.AuthTkCacherExceptionMsg.AUTH_HEADER_WRONG;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.cleanInitialSec;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_NOTHING;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_RODRIGO;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetAuthTk;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetUser;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static com.didekindroid.lib_one.usuario.dao.UsuarioDao.usuarioDaoRemote;
import static com.didekinlib.model.usuario.http.TkValidaPatterns.tkEncrypted_direct_symmetricKey_REGEX;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.USER_NOT_FOUND;
import static java.lang.Boolean.TRUE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
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
@SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
@RunWith(AndroidJUnit4.class)
public class UsuarioDaoTest {

    private CleanUserEnum whatClean = CLEAN_NOTHING;
    private AuthTkCacher tkCacher;

    @BeforeClass
    public static void setMore()
    {
        initSec_Http(getTargetContext());
        waitAtMost(4, SECONDS).until(
                () -> secInitializer.get() != null
                        && secInitializer.get().getJksInClient() != null
                        && secInitializer.get().getTkCacher() != null
                        && firebaseInitializer.get() != null);
    }

    @Before
    public void setUp() throws UiException
    {
        tkCacher = (AuthTkCacher) secInitializer.get().getTkCacher();
        tkCacher.updateAuthToken(null);
    }

    @After
    public void cleaningUp()
    {
        cleanOptions(whatClean);
    }

    @AfterClass
    public static void cleanUp()
    {
        cleanInitialSec();
    }

//    ========================= INTERFACE TESTS =======================

    @Test
    public void testDeleteUser_1() throws Exception
    {
        // Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        assertThat(regComuUserUserComuGetAuthTk(comu_real_rodrigo), notNullValue());
        // Exec, check.
        usuarioDaoRemote.deleteUser().test();
        assertThat(tkCacher.isUserRegistered(), is(false));
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
    public void testGetGcmToken() throws Exception
    {
        whatClean = CLEAN_RODRIGO;

        Usuario userDb = regComuUserUserComuGetUser(comu_real_rodrigo);
        usuarioDaoRemote.getGcmToken().test().assertResult(userDb.getGcmToken());
    }

    @Test
    public void testGetUserData() throws Exception
    {
        whatClean = CLEAN_RODRIGO;

        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        assertThat(regComuUserUserComuGetAuthTk(comu_real_rodrigo), notNullValue());
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
        assertThat(tkCacher.isUserRegistered(), is(false));
    }

    @SuppressWarnings("ThrowableNotThrown")
    @Test
    public void testLogin_2() throws Exception
    {
        whatClean = CLEAN_RODRIGO;

        String tokenAuth_1 = regComuUserUserComuGetAuthTk(comu_real_rodrigo);
        assertThat(tkEncrypted_direct_symmetricKey_REGEX.isPatternOk(tokenAuth_1), is(true));
        String gcmToken_1 = usuarioDaoRemote.getGcmToken().blockingGet();
        // Simulo una nueva instalación de app.
        tkCacher.updateAuthToken(null);
        firebaseInitializer.get().deleteFirebaseInstance().blockingGet();
        assertThat(tkCacher.isUserRegistered(), is(false));
        // Exec.
        usuarioDaoRemote.login(user_crodrigo.getUserName(), user_crodrigo.getPassword()).blockingAwait();
        String tokenAuth_2 = tkCacher.getAuthTokenCache();
        String gcmToken_2 = usuarioDaoRemote.getGcmToken().blockingGet();
        assertThat(tokenAuth_2.equals(tokenAuth_1), is(false));
        assertThat(gcmToken_2.equals(gcmToken_1), is(false));
        assertThat(firebaseInitializer.get().getSingleAppIdToken().blockingGet(), is(gcmToken_2));
        assertThat(tkCacher.isUserRegistered(), is(true));
    }

    @Test
    public void testmodifyAlias() throws Exception
    {
        whatClean = CLEAN_RODRIGO;

        // Changed alias; not userName.
        Usuario usuarioIn = new Usuario.UsuarioBuilder()
                .copyUsuario(regComuUserUserComuGetUser(comu_real_rodrigo))
                .alias("new_alias")
                .build();

        usuarioDaoRemote.modifyUser(usuarioIn).test().assertResult(TRUE);
    }

    @Test
    public void testmodifyUserName() throws Exception
    {
        Usuario usuarioIn = new Usuario.UsuarioBuilder()
                .copyUsuario(regComuUserUserComuGetUser(comu_real_rodrigo))
                .userName(USER_DROID.getUserName())
                .build();

        usuarioDaoRemote.modifyUser(usuarioIn).test().await();
        assertThat(tkCacher.isUserRegistered(), is(true));

        cleanOneUser(USER_DROID.getUserName());
    }

    @Test
    public void testPasswordChange() throws Exception
    {
        whatClean = CLEAN_RODRIGO;

        Usuario usuarioIn = regComuUserUserComuGetUser(comu_real_rodrigo);
        usuarioDaoRemote.passwordChange(user_crodrigo.getPassword(), "new_password").test().await();
        assertThat(tkCacher.getAuthTokenCache(), allOf(
                not(is(usuarioIn.getTokenAuth())),
                notNullValue()
        ));
    }

    @Test
    public void test_PasswordSend() throws Exception
    {
        whatClean = CLEAN_RODRIGO;
        regComuUserUserComuGetAuthTk(comu_real_rodrigo);
        usuarioDaoRemote.passwordSend(user_crodrigo.getUserName()).test().assertComplete();
        assertThat(tkCacher.getAuthTokenCache(), nullValue());
    }
}