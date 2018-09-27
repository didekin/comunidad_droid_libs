package com.didekindroid.lib_one.api;

import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.didekinlib.model.usuario.Usuario;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.functions.Function;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.cleanInitialSec;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http_Router;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 19/04/17
 * Time: 12:21
 */
public class ViewerSelectListTest {

    @Rule
    public IntentsTestRule<ActivityMock> activityRule = new IntentsTestRule<>(ActivityMock.class, true, true);

    private ActivityMock activity;
    private ViewerSelectList<Spinner, CtrlerSelectList<Usuario>, Usuario> viewer;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        initSec_Http_Router(getTargetContext());

        activity.runOnUiThread(() ->
                viewer = new ViewerSelectList<Spinner, CtrlerSelectList<Usuario>, Usuario>(new Spinner(activity), activity, null) {
                    @Override
                    public void initSelectedItemId(Bundle savedState)
                    {
                    }

                    @Override
                    public Function<Usuario, Long> getBeanIdFunction()
                    {
                        return Usuario::getuId;
                    }
                });
        waitAtMost(2, SECONDS).until(() -> viewer != null);
    }

    @AfterClass
    public static void cleanUp()
    {
        cleanInitialSec();
    }

    // ---------------------------------------------------------------------------------------------------

    @Test
    public void testGetArrayAdapterForSpinner()
    {
        ArrayAdapter<Usuario> adapter = viewer.getArrayAdapterForSpinner(activity);
        assertThat(adapter, notNullValue());
        assertThat(adapter.getCount(), is(0));
    }

    @Test
    public void testGetSelectedItemId()
    {
        viewer.setSelectedItemId(111L);
        assertThat(viewer.getSelectedItemId(), is(111L));
    }

    @Test
    public void test_getSelectedPositionFromItemId()
    {
        final List<Usuario> usuarios = asList(
                new Usuario.UsuarioBuilder().copyUsuario(user_crodrigo).uId(111L).build(),
                new Usuario.UsuarioBuilder().copyUsuario(USER_DROID).uId(222L).build(),
                new Usuario.UsuarioBuilder().copyUsuario(USER_JUAN).uId(333L).build());

        viewer.setSelectedItemId(333L);
        activity.runOnUiThread(() -> {
            viewer.onSuccessLoadItemList(usuarios);
            // Exec and check.
            assertThat(viewer.getSelectedPositionFromItemId(viewer.getBeanIdFunction()), is(2));   // id 333
        });
    }

    @Test
    public void test_OnSuccessLoadItemList() throws Exception
    {
        final List<Usuario> usuarios = asList(
                new Usuario.UsuarioBuilder().copyUsuario(user_crodrigo).uId(111L).build(),
                new Usuario.UsuarioBuilder().copyUsuario(USER_DROID).uId(222L).build(),
                new Usuario.UsuarioBuilder().copyUsuario(USER_JUAN).uId(333L).build());

        viewer.setSelectedItemId(222L);
        int itemIdPosition = 1;

        // Exec and check.
        final AtomicBoolean isExec = new AtomicBoolean(false);
        activity.runOnUiThread(() -> {
            viewer.onSuccessLoadItemList(usuarios);
            isExec.compareAndSet(false, true);
        });
        waitAtMost(4, SECONDS).untilTrue(isExec);
        assertThat(viewer.getViewInViewer().getAdapter().getCount(), is(usuarios.size()));

        // ListView.getSelectedItemId() and ListView.getSelectedItemPosition() return position.
        assertThat(viewer.getSelectedPositionFromItemId(viewer.getBeanIdFunction()), allOf(
                is((int) viewer.getViewInViewer().getSelectedItemId()),
                is(viewer.getViewInViewer().getSelectedItemPosition())
        ));
        // To get the id of the object in a certain position:
        assertThat(viewer.getBeanIdFunction()
                        .apply((Usuario) viewer.getViewInViewer().getItemAtPosition(itemIdPosition)),
                is(222L));
    }
}