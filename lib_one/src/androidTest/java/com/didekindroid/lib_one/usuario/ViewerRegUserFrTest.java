package com.didekindroid.lib_one.usuario;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.InjectorParentViewerAc;
import com.didekindroid.lib_one.api.router.FragmentInitiator;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initRouter;
import static com.didekindroid.lib_one.usuario.testutil.UserEspressoTestUtil.typeUserNameAlias;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/05/17
 * Time: 11:00
 */
@RunWith(AndroidJUnit4.class)
public class ViewerRegUserFrTest {

    @Rule
    public ActivityTestRule<InjectorParentViewerAc> activityRule = new ActivityTestRule<InjectorParentViewerAc>(InjectorParentViewerAc.class, false, true) {
        @Override
        protected void beforeActivityLaunched()
        {
            initRouter();
        }
    };
    private RegUserFr fragment;
    private ActivityMock activity;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        fragment = new RegUserFr();
        new FragmentInitiator<RegUserFr>(activity, R.id.mock_ac_layout).initFragmentTx(fragment);
        waitAtMost(4, SECONDS).until(() -> activity.getSupportFragmentManager().findFragmentByTag(fragment.getClass().getName()) != null);
        waitAtMost(4, SECONDS).until(() -> fragment.viewer != null);
    }

    @Test
    public void test_GetUserFromViewerOk()
    {
        typeUserNameAlias("yo@email.com", "alias1");
        assertThat(fragment.viewer.getUserFromViewer(new StringBuilder()), allOf(
                notNullValue(),
                is(new Usuario.UsuarioBuilder().userName("yo@email.com").alias("alias1").password("password1").build())
        ));
    }

    @Test
    public void test_GetUserFromViewerWrong()
    {
        typeUserNameAlias("yo_email.com", "alias1");
        assertThat(fragment.viewer.getUserFromViewer(new StringBuilder()), nullValue());
    }
}