package com.didekindroid.lib_one.comunidad.spinner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Spinner;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.SpinnerTextMockFr;
import com.didekindroid.lib_one.security.AuthTkCacher;
import com.didekindroid.lib_one.security.MySecInitializerMock;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.comunidad.spinner.ComunidadSpinnerKey.MUNICIPIO_SPINNER_EVENT;
import static com.didekindroid.lib_one.comunidad.spinner.ViewerMunicipioSpinner.newViewerMunicipioSpinner;
import static com.didekindroid.lib_one.comunidad.spinner.ViewerMunicipioSpinner.spinnerEvent_default;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_EXEC_A;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.cleanInitialSec;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initRouterAll;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 07/05/17
 * Time: 13:47
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class ViewerMunicipioSpinnerTest {

    private final AtomicReference<String> flagLocalExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    private ViewerMunicipioSpinner viewer;
    private ActivityMock activity;
    private Spinner spinner;

    @BeforeClass
    public static void setMore()
    {
        initRouterAll();
        secInitializer.set(new MySecInitializerMock(getTargetContext(), new AuthTkCacher(getTargetContext())));
    }

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        activity.runOnUiThread(() -> {
            activity.getSupportFragmentManager().beginTransaction()
                    .add(R.id.mock_ac_layout, new SpinnerTextMockFr(), null)
                    .commitNow();
            spinner = activity.findViewById(R.id.municipio_spinner);
            viewer = newViewerMunicipioSpinner(spinner, activity, null);
        });
        waitAtMost(4, SECONDS).until(() -> viewer != null);
    }

    @AfterClass
    public static void cleanUp()
    {
        cleanInitialSec();
    }

    // ---------------------------------------------------------------------------------------------------

    @Test
    public void test_NewViewerMunicipioSpinner()
    {
        assertThat(newViewerMunicipioSpinner(spinner, activity, null).getController(), notNullValue());
    }

    @Test
    public void test_InitSelectedItemId()
    {
        Bundle bundle = new Bundle();

        // Case 0: no previous initialization
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(0L));
        // Case 1: initialization in savedState
        bundle.putSerializable(MUNICIPIO_SPINNER_EVENT.key, new MunicipioSpinnerEventItemSelect(new Municipio((short) 22, new Provincia((short) 1))));
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(22L));
        // Case 2: initialization in both savedState and municipioIn
        viewer.spinnerEvent = new MunicipioSpinnerEventItemSelect(new Municipio((short) 33, new Provincia((short) 2)));
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(22L));
        // Case 3: initialization only in municipioIn
        viewer.initSelectedItemId(null);
        assertThat(viewer.getSelectedItemId(), is(33L));
    }

    @Test
    public void test_GetSelectedPositionFromItemId()
    {
        viewer.setSelectedItemId(33);
        activity.runOnUiThread(() -> {
            viewer.onSuccessLoadItemList(doListMunicipios());
            assertThat(viewer.getSelectedPositionFromItemId(viewer.getBeanIdFunction()), is(1));   // id 33
        });
    }

    @Test
    public void test_DoViewInViewer()
    {
        // State
        final String keyBundle = MUNICIPIO_SPINNER_EVENT.key;
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(keyBundle, new MunicipioSpinnerEventItemSelect(new Municipio((short) 11, new Provincia((short) 3))));

        viewer.setController(new CtrlerMunicipioSpinner() {
            @Override
            public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<Municipio>> observer, Long... entityId)
            {
                assertThat(flagLocalExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return false;
            }
        });
        viewer.doViewInViewer(bundle, null);

        assertThat(viewer.spinnerEvent, is(spinnerEvent_default));
        assertThat(viewer.getSelectedItemId(), is(11L));
        // Check NO call to controller.loadItemsByEntitiyId();
        assertThat(flagLocalExec.get(), is(BEFORE_METHOD_EXEC));
        // Check call to view.setOnItemSelectedListener().
        assertThat(viewer.getViewInViewer().getOnItemSelectedListener(),
                instanceOf(ViewerMunicipioSpinner.MunicipioSelectedListener.class));
    }

    @Test
    public void test_MunicipioSelectedListener()
    {
        viewer.setSelectedItemId(0);
        activity.runOnUiThread(() -> {
            viewer.onSuccessLoadItemList(doListMunicipios());
            assertThat(viewer.getViewInViewer().getAdapter().getCount(), is(3));
        });
        // Exec.
        ViewerMunicipioSpinner.MunicipioSelectedListener listener = viewer.new MunicipioSelectedListener();
        activity.runOnUiThread(() -> listener.onItemSelected(viewer.getViewInViewer(), null, 1, 33));
        // Check
        waitAtMost(4, SECONDS).until(() -> viewer.spinnerEvent.getMunicipio().equals(new Municipio((short) 33, new Provincia((short) 1))));
    }

    @Test
    public void test_SaveState()
    {
        Bundle bundle = new Bundle(1);
        viewer.saveState(bundle);
        assertThat(bundle.getLong(MUNICIPIO_SPINNER_EVENT.key), is(0L));

        viewer.spinnerEvent = new MunicipioSpinnerEventItemSelect(new Municipio((short) 11, new Provincia((short) 1)));
        viewer.saveState(bundle);
        assertThat(MunicipioSpinnerEventItemSelect.class.cast(bundle.getSerializable(MUNICIPIO_SPINNER_EVENT.key)), is(viewer.spinnerEvent));
    }

    // ======================================= HELPERS ===============================================

    @NonNull
    private List<Municipio> doListMunicipios()
    {
        return asList(
                new Municipio((short) 11, new Provincia((short) 3)),
                new Municipio((short) 33, new Provincia((short) 1)),
                new Municipio((short) 22, new Provincia((short) 2)));
    }
}