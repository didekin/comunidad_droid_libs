package com.didekindroid.lib_one.usuario.dao;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.CompletableObserverMock;
import com.didekindroid.lib_one.api.SingleObserverMock;
import com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.cleanInitialSec;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.execCheckSchedulersTest;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_NOTHING;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_RODRIGO;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetAuthTk;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetUser;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 23/02/17
 * Time: 14:17
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerUsuario_Test {

    private CtrlerUsuario controller;
    private CleanUserEnum whatClean = CLEAN_RODRIGO;

    @BeforeClass
    public static void setMore()
    {
        initSec_Http(getTargetContext());
    }

    @Before
    public void setUp()
    {
        controller = new CtrlerUsuario();
    }

    @After
    public void cleanUp()
    {
        assertThat(controller.clearSubscriptions(), is(0));
        cleanOptions(whatClean);
    }

    @AfterClass
    public static void cleanMore()
    {
        cleanInitialSec();
        resetAllSchedulers();
    }

    //    .................................... INSTANCE METHODS .................................

    @Test
    public void testDeleteMe() throws Exception
    {
        whatClean = CLEAN_NOTHING;

        assertThat(regComuUserUserComuGetAuthTk(comu_real_rodrigo), notNullValue());
        execCheckSchedulersTest(ctrler -> ctrler.deleteMe(new CompletableObserverMock()), controller);
    }

    @Test
    public void test_GetAppIdToken() throws Exception
    {
        whatClean = CLEAN_NOTHING;
        execCheckSchedulersTest(ctrler -> ctrler.getAppIdToken(new SingleObserverMock<>()), controller);
    }

    @Test
    public void testGetUserData() throws Exception
    {
        whatClean = CLEAN_RODRIGO;
        assertThat(regComuUserUserComuGetAuthTk(comu_real_rodrigo), notNullValue());
        execCheckSchedulersTest(ctrler -> ctrler.getUserData(new SingleObserverMock<>()), controller);
    }

    @Test
    public void testLogin() throws Exception
    {
        whatClean = CLEAN_RODRIGO;
        assertThat(regComuUserUserComuGetAuthTk(comu_real_rodrigo), notNullValue());
        execCheckSchedulersTest(ctrler -> ctrler.login(new CompletableObserverMock(), user_crodrigo), controller);
    }

    @Test
    public void testModifyUserName() throws Exception
    {
        whatClean = CLEAN_DROID;

        execCheckSchedulersTest(
                ctrler -> ctrler.modifyUserName(
                        new SingleObserverMock<>(),
                        new Usuario.UsuarioBuilder()
                                .copyUsuario(regComuUserUserComuGetUser(comu_real_rodrigo))
                                .userName(USER_DROID.getUserName())
                                .build()),
                controller
        );
    }

    @Test
    public void testModifyUserAlias() throws Exception
    {
        whatClean = CLEAN_RODRIGO;
        execCheckSchedulersTest(
                ctrler -> ctrler.modifyUserAlias(
                        new SingleObserverMock<>(),
                        new Usuario.UsuarioBuilder()
                                .copyUsuario(regComuUserUserComuGetUser(comu_real_rodrigo))
                                .alias("new_pepe_alias")
                                .build()),
                controller);
    }

    @Test
    public void testPasswordChange() throws Exception
    {
        whatClean = CLEAN_RODRIGO;

        // Precondition.
        Usuario oldUser = new Usuario.UsuarioBuilder()
                .copyUsuario(regComuUserUserComuGetUser(comu_real_rodrigo))
                .password(user_crodrigo.getPassword())
                .build();

        execCheckSchedulersTest(
                ctrler -> ctrler.passwordChange(
                        new CompletableObserverMock(),
                        oldUser,
                        new Usuario.UsuarioBuilder()
                                .copyUsuario(oldUser)
                                .password("new_password")
                                .build()),
                controller);
    }

    @Test
    public void testPasswordSend() throws Exception
    {
        whatClean = CLEAN_RODRIGO;
        // Precondition.
        execCheckSchedulersTest(
                ctrler -> ctrler.passwordSend(
                        new CompletableObserverMock(),
                        regComuUserUserComuGetUser(comu_real_rodrigo)),
                controller);
    }
}