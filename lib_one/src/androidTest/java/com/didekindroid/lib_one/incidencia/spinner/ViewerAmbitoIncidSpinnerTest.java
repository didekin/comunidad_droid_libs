package com.didekindroid.lib_one.incidencia.spinner;

import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Spinner;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.SpinnerTextMockFr;
import com.didekindroid.lib_one.api.ViewerMock;
import com.didekindroid.lib_one.incidencia.IncidenciaBean;
import com.didekindroid.lib_one.security.AuthTkCacher;
import com.didekindroid.lib_one.security.MySecInitializerMock;

import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;

import static com.didekindroid.lib_one.incidencia.spinner.IncidenciaSpinnerKey.AMBITO_INCIDENCIA_POSITION;
import static com.didekindroid.lib_one.incidencia.spinner.ViewerAmbitoIncidSpinner.newViewerAmbitoIncidSpinner;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_EXEC_A;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.cleanInitialSec;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initRouterAll;
import static com.didekindroid.lib_one.testutil.UiTestUtil.checkSavedStateWithItemSelected;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 30/03/17
 * Time: 17:01
 */
@RunWith(AndroidJUnit4.class)
public class ViewerAmbitoIncidSpinnerTest {

    private final AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    private ViewerAmbitoIncidSpinner viewer;
    private ActivityMock activity;
    private Spinner spinner;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        secInitializer.set(new MySecInitializerMock(activity, new AuthTkCacher(activity)));
        initRouterAll();

        activity.runOnUiThread(() -> {
            activity.getSupportFragmentManager().beginTransaction()
                    .add(R.id.mock_ac_layout, new SpinnerTextMockFr(), null)
                    .commitNow();
            spinner = activity.findViewById(R.id.ambito_spinner);
            viewer = newViewerAmbitoIncidSpinner(spinner, new ViewerMock(activity));
        });
        waitAtMost(2, SECONDS).until(() -> viewer != null);
    }

    @AfterClass
    public static void cleanUp()
    {
        cleanInitialSec();
    }

    // ==================================== TESTS ====================================

    @Test
    public void testNewViewerAmbitoIncidSpinner()
    {
        assertThat(newViewerAmbitoIncidSpinner(spinner, new ViewerMock(activity)).getController(), notNullValue());
    }

    @Test
    public void test_getSelectedPositionFromItemId()
    {
        final List<AmbitoIncidValueObj> ambitos = new ArrayList<>(3);
        ambitos.add(new AmbitoIncidValueObj((short) 33, "ambito0"));
        ambitos.add(new AmbitoIncidValueObj((short) 22, "ambito1"));
        ambitos.add(new AmbitoIncidValueObj((short) 11, "ambito2"));

        viewer.setSelectedItemId(33L);
        activity.runOnUiThread(() -> {
            viewer.onSuccessLoadItemList(ambitos);
            // Exec and check.
            assertThat(viewer.getSelectedPositionFromItemId(viewer.getBeanIdFunction()), is(0));   // id 333
        });
    }

    @Test
    public void testInitSelectedItemId()
    {

        viewer.incidenciaBean = new IncidenciaBean();
        Bundle bundle = new Bundle();

        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(0L));

        viewer.incidenciaBean.setCodAmbitoIncid((short) 13);
        viewer.initSelectedItemId(null);
        assertThat(viewer.getSelectedItemId(), is(13L));

        bundle = new Bundle();
        bundle.putLong(AMBITO_INCIDENCIA_POSITION.key, 91);
        assertThat(viewer.incidenciaBean.getCodAmbitoIncid(), is((short) 13));
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(91L));
    }

    @Test
    public void testSaveState()
    {
        checkSavedStateWithItemSelected(viewer, AMBITO_INCIDENCIA_POSITION);
    }

    @Test
    public void testDoViewInViewer_1()
    {
        final String keyBundle = AMBITO_INCIDENCIA_POSITION.key;
        IncidenciaBean incidenciaBean = new IncidenciaBean();
        Bundle bundle = new Bundle();
        bundle.putLong(keyBundle, 111);

        viewer.setController(new CtrlerAmbitoIncidSpinner() {
            @Override
            public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<AmbitoIncidValueObj>> observer, Long... entityId)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), CoreMatchers.is(BEFORE_METHOD_EXEC));
                return false;
            }
        });

        viewer.doViewInViewer(bundle, incidenciaBean);

        // Check call to initSelectedItemId().
        assertThat(viewer.getSelectedItemId(), allOf(
                is(111L),
                is(bundle.getLong(keyBundle))
        ));
        // Check call to controller.loadDataInSpinner();
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
        // Check call to view.setOnItemSelectedListener().
        assertThat(viewer.getViewInViewer().getOnItemSelectedListener(), instanceOf(ViewerAmbitoIncidSpinner.AmbitoIncidSelectedListener.class));
    }

    @Test
    public void testDoViewInViewer_2()
    {
        IncidenciaBean incidenciaBean = new IncidenciaBean();
        incidenciaBean.setCodAmbitoIncid((short) 13);
        viewer.doViewInViewer(null, incidenciaBean);
        assertThat(viewer.getSelectedItemId(), is(13L));
    }
}