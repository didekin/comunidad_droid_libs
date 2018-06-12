package com.didekindroid.lib_one.usuario.notification;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.security.AuthTkCacherIf;
import com.didekindroid.lib_one.usuario.notification.InstanceIdService.ServiceDisposableObserver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regUserComuWithTkCache;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 06/03/17
 * Time: 17:26
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerNotifyTokenTest {

    static final AtomicReference<String> flagControl = new AtomicReference<>(BEFORE_METHOD_EXEC);

    private CtrlerNotifyToken controller;
    private AuthTkCacherIf identityCacher;

    @Before
    public void setUp()
    {
        initSec_Http(getTargetContext());
        assertThat(regUserComuWithTkCache(comu_real_rodrigo), notNullValue());
        controller = new CtrlerNotifyToken();
        identityCacher = controller.getTkCacher();
    }

    @After
    public void cleanUp()
    {
        cleanOneUser(user_crodrigo.getUserName());
    }

    //    ................................. INSTANCE METHODS ...............................

    @Test
    public void test_modifyGcmTokenSync_1()
    {
        // Preconditions.
        identityCacher.updateIsGcmTokenSentServer(false);
        /* Execute.*/
        controller.modifyGcmTokenSync(new ServiceDisposableObserver(controller));
        assertThat(identityCacher.isGcmTokenSentServer(), is(true));
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    @Test
    public void test_modifyGcmTokenSync_2()
    {
        // Preconditions.
        identityCacher.updateIsRegistered(false);
        identityCacher.updateIsGcmTokenSentServer(false);
        /* Execute.*/
        assertThat(controller.modifyGcmTokenSync(new ServiceDisposableObserver(controller)), is(false));
        assertThat(controller.getSubscriptions().size(), is(0));
        assertThat(identityCacher.isGcmTokenSentServer(), is(false));
    }
}