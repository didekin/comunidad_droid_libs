package com.didekindroid.lib_one.usuario.dao;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.usuario.UserTestData;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.execCheckSchedulersTest;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_NOTHING;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_RODRIGO;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regGetUserComu;
import static com.didekindroid.lib_one.usuario.UserTestData.regUserComuWithTkCache;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 23/02/17
 * Time: 14:17
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerUsuario_Test {

    private CtrlerUsuario controller;
    private UserTestData.CleanUserEnum whatClean = CLEAN_RODRIGO;

    @Before
    public void setUp()
    {
        initSec_Http(getTargetContext());
        controller = new CtrlerUsuario();
    }

    @After
    public void cleanUp()
    {
        resetAllSchedulers();
        assertThat(controller.clearSubscriptions(), is(0));
        cleanOptions(whatClean);
    }

    //    .................................... INSTANCE METHODS .................................

    @Test
    public void testDeleteMe() throws Exception
    {
        whatClean = CLEAN_NOTHING;

        assertThat(regUserComuWithTkCache(comu_real_rodrigo), notNullValue());
        execCheckSchedulersTest(ctrler -> ctrler.deleteMe(new TestCompletableObserver()), controller);
    }

    @Test
    public void testGetUserData() throws Exception
    {
        assertThat(regUserComuWithTkCache(comu_real_rodrigo), notNullValue());
        execCheckSchedulersTest(ctrler -> ctrler.getUserData(new TestSingleObserver<>()), controller);
    }

    @Test
    public void testLogin() throws Exception
    {
        assertThat(regUserComuWithTkCache(comu_real_rodrigo), notNullValue());
        execCheckSchedulersTest(ctrler -> ctrler.login(new TestCompletableObserver(), user_crodrigo), controller);
    }

    @Test
    public void testModifyUserName() throws Exception
    {
        whatClean = CLEAN_DROID;

        execCheckSchedulersTest(
                ctrler -> ctrler.modifyUserName(
                        new TestSingleObserver<>(),
                        new Usuario.UsuarioBuilder()
                                .copyUsuario(regGetUserComu(comu_real_rodrigo))
                                .userName(USER_DROID.getUserName())
                                .build()),
                controller
        );
    }

    @Test
    public void testModifyUserAlias() throws Exception
    {
        execCheckSchedulersTest(
                ctrler -> ctrler.modifyUserAlias(
                        new TestSingleObserver<>(),
                        new Usuario.UsuarioBuilder()
                                .copyUsuario(regGetUserComu(comu_real_rodrigo))
                                .alias("new_pepe_alias")
                                .build()),
                controller);
    }

    @Test
    public void testPasswordChange() throws Exception
    {
        // Precondition.
        Usuario oldUser = new Usuario.UsuarioBuilder()
                .copyUsuario(regGetUserComu(comu_real_rodrigo))
                .password(user_crodrigo.getPassword())
                .build();

        execCheckSchedulersTest(
                ctrler -> ctrler.passwordChange(
                        new TestCompletableObserver(),
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
        // Precondition.
        execCheckSchedulersTest(
                ctrler -> ctrler.passwordSend(
                        new TestCompletableObserver(),
                        regGetUserComu(comu_real_rodrigo)),
                controller);
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    class TestSingleObserver<T> extends DisposableSingleObserver<T> {

        @Override
        public void onSuccess(T successBack)
        {
            assertThat(successBack, notNullValue());
        }

        @Override
        public void onError(Throwable e)
        {
            dispose();
            Timber.d("============= %s =============", e.getClass().getName());
            fail();
        }
    }

    class TestCompletableObserver extends DisposableCompletableObserver {

        @Override
        public void onComplete()
        {
        }

        @Override
        public void onError(Throwable e)
        {
            dispose();
            Timber.d("============= %s =============", e.getClass().getName());
            fail();
        }
    }
}