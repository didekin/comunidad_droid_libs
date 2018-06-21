package com.didekindroid.lib_one.api;

import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initRouterAll;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
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
    private ViewerSelectList<Spinner, CtrlerSelectList<String>, String> viewer;
    private ViewerSelectList<Spinner, CtrlerSelectList<Usuario>, Usuario> viewerUser;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        initRouterAll();

        activity.runOnUiThread(() -> viewer = new ViewerSelectList<Spinner, CtrlerSelectList<String>, String>(new Spinner(activity), activity, null) {
            @Override
            public void initSelectedItemId(Bundle savedState)
            {
            }
        });
        waitAtMost(2, SECONDS).until(() -> viewer != null);
    }

    @Test
    public void testGetArrayAdapterForSpinner()
    {
        ArrayAdapter<String> adapter = viewer.getArrayAdapterForSpinner(activity);
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
    public void testGetSelectedViewFromItemId()
    {
        viewer.setSelectedItemId(111L);
        assertThat(viewer.getSelectedPositionFromItemId(111L), is(111));
    }

    @Test
    public void test_OnSuccessLoadItemList_1()
    {
        final List<String> stringList = asList("string22", "string11", "string44", "string33");
        long itemSelected = 1;
        viewer.setSelectedItemId(itemSelected);

        final AtomicBoolean isExec = new AtomicBoolean(false);
        activity.runOnUiThread(() -> {
            viewer.onSuccessLoadItemList(stringList);
            isExec.compareAndSet(false, true);
        });
        waitAtMost(4, SECONDS).untilTrue(isExec);
        assertThat(viewer.getViewInViewer().getAdapter().getCount(), is(stringList.size()));
        assertThat(viewer.getViewInViewer().getSelectedItemId(), is(itemSelected));
        assertThat(viewer.getViewInViewer().getSelectedItemPosition(), is((int) itemSelected));
    }

    @Test
    public void test_OnSuccessLoadItemList_2()
    {
        activity.runOnUiThread(() ->
                viewerUser = new ViewerSelectList<Spinner, CtrlerSelectList<Usuario>, Usuario>(new Spinner(activity), activity, null) {
                    @Override
                    public void initSelectedItemId(Bundle savedState)
                    {
                    }
                });
        waitAtMost(2, SECONDS).until(() -> viewerUser != null);


        final List<Usuario> usuarios = asList(
                new Usuario.UsuarioBuilder().copyUsuario(user_crodrigo).uId(111L).build(),
                new Usuario.UsuarioBuilder().copyUsuario(USER_DROID).uId(222L).build(),
                new Usuario.UsuarioBuilder().copyUsuario(USER_JUAN).uId(333L).build());

        long itemSelected = 2;
        viewerUser.setSelectedItemId(itemSelected);

        final AtomicBoolean isExec = new AtomicBoolean(false);
        activity.runOnUiThread(() -> {
            viewerUser.onSuccessLoadItemList(usuarios);
            isExec.compareAndSet(false, true);
        });
        waitAtMost(4, SECONDS).untilTrue(isExec);

        assertThat(viewerUser.getViewInViewer().getAdapter().getCount(), is(usuarios.size()));
        // ListView.getSelectedItemId() returns the same as ListView.getSelectedItemPosition(), not the pk if such a field exists in the object.
        assertThat(viewerUser.getViewInViewer().getSelectedItemId(), is(itemSelected));
        assertThat(viewerUser.getViewInViewer().getSelectedItemPosition(), is((int) itemSelected));
        // To get the itemId:
        assertThat(Usuario.class.cast(viewerUser.getViewInViewer().getItemAtPosition((int) itemSelected)).getuId(),
                is(333L));
    }
}