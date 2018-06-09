package com.didekindroid.lib_one.usuario.dao;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.testutil.RxSchedulersUtils;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
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

    private final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    private CtrlerUsuario controller;

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
    }

    //    .................................... INSTANCE METHODS .................................

    @Test
    public void testDeleteMe()
    {
        assertThat(regUserComuWithTkCache(comu_real_rodrigo), notNullValue());
        RxSchedulersUtils.execCheckSchedulersTest(controller.deleteMe(new TestCompletableObserver()));
//        assertThat(controller.getSubscriptions().size(), is(1));
    }

    @Test
    public void testGetUserData()
    {
        assertThat(regUserComuWithTkCache(comu_real_rodrigo), notNullValue());
        RxSchedulersUtils.execCheckSchedulersTest(controller.getUserData(new TestSingleObserver<>()));
        cleanOneUser(user_crodrigo.getUserName());
    }

    @Test
    public void testLogin()
    {
        assertThat(regUserComuWithTkCache(comu_real_rodrigo), notNullValue());
        RxSchedulersUtils.execCheckSchedulersTest(controller.login(new TestCompletableObserver(), user_crodrigo));
        cleanOneUser(user_crodrigo.getUserName());
    }

    @Test
    public void testModifyUserName()
    {
        RxSchedulersUtils.execCheckSchedulersTest(controller.modifyUserName(
                new TestSingleObserver<>(),
                new Usuario.UsuarioBuilder()
                        .copyUsuario(regGetUserComu(comu_real_rodrigo))
                        .userName(USER_DROID.getUserName())
                        .build()));

        cleanOneUser(USER_DROID.getUserName());
    }

    @Test
    public void testModifyUserAlias()
    {
        RxSchedulersUtils.execCheckSchedulersTest(controller.modifyUserAlias(
                new TestSingleObserver<>(),
                new Usuario.UsuarioBuilder()
                        .copyUsuario(regGetUserComu(comu_real_rodrigo))
                        .alias("new_pepe_alias")
                        .build()));

        cleanOneUser(user_crodrigo.getUserName());
    }

    @Test
    public void testPasswordChange()
    {
        // Precondition.
        Usuario oldUser = new Usuario.UsuarioBuilder()
                .copyUsuario(regGetUserComu(comu_real_rodrigo))
                .password(user_crodrigo.getPassword())
                .build();

        RxSchedulersUtils.execCheckSchedulersTest(controller.passwordChange(new TestCompletableObserver(),
                oldUser,
                new Usuario.UsuarioBuilder()
                        .copyUsuario(oldUser)
                        .password("new_password")
                        .build()));

        cleanOneUser(user_crodrigo.getUserName());
    }

    @Test
    public void testPasswordSend()
    {
        // Precondition.
        RxSchedulersUtils.execCheckSchedulersTest(controller.passwordSend(new TestCompletableObserver(), regGetUserComu(comu_real_rodrigo)));
        cleanOneUser(user_crodrigo.getUserName());
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    class TestSingleObserver<T> extends DisposableSingleObserver<T> {

        @Override
        public void onSuccess(T successBack)
        {
            assertThat(controller.getSubscriptions().size(), is(1));
            dispose();
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
            assertThat(controller.getSubscriptions().size(), is(1));
            dispose();
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