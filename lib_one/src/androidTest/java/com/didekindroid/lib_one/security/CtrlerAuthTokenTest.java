package com.didekindroid.lib_one.security;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.ObserverCacheCleaner;
import com.didekindroid.lib_one.api.Viewer;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.auth.SpringOauthToken;
import com.didekinlib.http.exception.ErrorBean;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableCompletableObserver;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.security.SecurityTestUtils.checkInitTokenCache;
import static com.didekindroid.lib_one.security.SecurityTestUtils.checkNoInitCache;
import static com.didekindroid.lib_one.security.SecurityTestUtils.updateSecurityData;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_EXEC_A;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_WITH_EXCEPTION_EXEC;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http_Router;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static com.didekindroid.lib_one.usuario.UsuarioMockDao.usuarioMockDao;
import static com.didekinlib.http.exception.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static io.reactivex.Completable.error;
import static io.reactivex.Completable.fromCallable;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 15/05/17
 * Time: 16:37
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class CtrlerAuthTokenTest {

    private final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<ActivityMock>(ActivityMock.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            initSec_Http_Router(getTargetContext());
            try {
                assertThat(usuarioMockDao.regComuAndUserAndUserComu(comu_real_rodrigo).execute().body(), is(true));
                updateSecurityData(comu_real_rodrigo.getUsuario().getUserName(), comu_real_rodrigo.getUsuario().getPassword());
            } catch (Exception e) {
                fail();
            }
        }
    };

    private ActivityMock activity;
    private Viewer<?, CtrlerAuthToken> viewer;
    private CtrlerAuthToken controller;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        viewer = new Viewer<View, CtrlerAuthToken>(null, activity, null) {
            @Override
            public void onErrorInObserver(Throwable error)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_WITH_EXCEPTION_EXEC), is(BEFORE_METHOD_EXEC));
                super.onErrorInObserver(error);
            }
        };
        viewer.setController(new CtrlerAuthToken());
        controller = viewer.getController();
    }

    @After
    public void tearDown() throws UiException
    {
        viewer.getController().clearSubscriptions();
        resetAllSchedulers();
        cleanOneUser(user_crodrigo);
    }

    //  =======================================================================================
    // ............................ SUBSCRIBERS ..................................
    //  =======================================================================================

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     * onErrorObserver method: we test that the token cache is cleaned.
     */
    @Test
    public void testOauthUpdateTokenCacheObserver_1()
    {
        checkInitTokenCache(controller.getTkCacher());
        activity.runOnUiThread(() -> {
            DisposableCompletableObserver disposable = error(new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR)))
                    .subscribeWith(new ObserverCacheCleaner(viewer));
            checkNoInitCache(controller.getTkCacher());
            assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_WITH_EXCEPTION_EXEC));
            assertThat(disposable.isDisposed(), is(true));
        });
    }

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     * onComplete method: we test that the token cache is NOT cleaned.
     */
    @Test
    public void testOauthUpdateTokenCacheObserver_2()
    {
        checkInitTokenCache(controller.getTkCacher());

        DisposableCompletableObserver disposable = fromCallable(() -> null).subscribeWith(new ObserverCacheCleaner(viewer));

        checkInitTokenCache(controller.getTkCacher());
        assertThat(disposable.isDisposed(), is(true));
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    @Test
    public void test_UpdateTkCacheFromRefreshTk()
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.updateTkCacheFromRefreshTk(controller.getTkCacher().getRefreshTokenValue(), viewer), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    @Test
    public void testRefreshAccessToken_1()
    {
        // Precondition: a fully initialized cache.
        assertThat(controller.getTkCacher().getTokenCache().get().getValue().isEmpty(), is(false));
        // Initial state.
        assertThat(controller.getTkCacher().isRegisteredUser(), is(true));
        assertThat(controller.getTkCacher().getTokenCache().get(), notNullValue());
        assertThat(controller.getTkCacher().getTokenCache().get().getValue(), notNullValue());
        assertThat(controller.getTkCacher().getRefreshTokenFile().exists(), is(true));

        controller = new CtrlerAuthToken() {
            @Override
            public boolean updateTkCacheFromRefreshTk(String refreshToken, Viewer viewer)
            {
                // No hay llamada al método del controller.
                fail();
                return false;
            }
        };
        controller.refreshAccessToken(viewer);
    }

    @Test
    public void testRefreshAccessToken_2()
    {
        // Precondition: a user in DB, cache is null.
        // Borramos cache.
        controller.getTkCacher().cleanIdentityCache();
        // Initial state.
        assertThat(controller.getTkCacher().isRegisteredUser(), is(true));
        assertThat(controller.getTkCacher().getTokenCache().get(), nullValue());

        controller = new CtrlerAuthToken() {
            @Override
            public boolean updateTkCacheFromRefreshTk(String refreshToken, Viewer viewer)
            {
                // No hay llamada al método del controller.
                fail();
                return false;
            }
        };
        controller.refreshAccessToken(viewer);
    }

    @Test
    public void testRefreshAccessToken_3()
    {
        // Precondition: a user in DB, refreshToken in cache, accessToken is null.
        String refreshTkOriginal = controller.getTkCacher().getRefreshTokenValue();
        // Clean cache and initialize with a refreshToken.
        controller.getTkCacher().cleanIdentityCache();
        controller.getTkCacher().getTokenCache().compareAndSet(null, new SpringOauthToken(refreshTkOriginal));
        // Initial state.
        assertThat(controller.getTkCacher().isRegisteredUser(), is(true));
        assertThat(controller.getTkCacher().getTokenCache().get().getRefreshToken(), notNullValue());
        assertThat(controller.getTkCacher().getTokenCache().get().getValue(), nullValue());

        controller = new CtrlerAuthToken() {
            @Override
            public boolean updateTkCacheFromRefreshTk(String refreshToken, Viewer viewer)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return false;
            }
        };

        controller.refreshAccessToken(viewer);
        // Hay llamada al método del controller.
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }
}