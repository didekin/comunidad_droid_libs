package com.didekindroid.lib_one.comunidad.spinner;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.security.AuthTkCacher;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.ComunidadAutonoma.NUMBER_RECORDS;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.cleanInitialSec;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSecurity;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.testutil.UiTestUtil.checkSpinnerCtrlerLoadItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 04/05/17
 * Time: 10:09
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerComAutonomaSpinnerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    private CtrlerComAutonomaSpinner controller;

    @BeforeClass
    public static void setMore(){
        initSecurity(getTargetContext());
    }

    @Before
    public void setUp()
    {
        controller = new CtrlerComAutonomaSpinner(new AuthTkCacher(activityRule.getActivity()));
    }

    @After
    public void tearDown()
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
    }

    @AfterClass
    public static void clearMore()
    {
        cleanInitialSec();
    }

    @Test
    public void test_ComunidadesAutonomasList()
    {
        controller.comunidadesAutonomasList().test().assertOf(listTestObserver -> {
            assertThat(listTestObserver.values().size(), is(1));
            assertThat(listTestObserver.values().get(0).size(), is(NUMBER_RECORDS));
        });
    }

    @Test
    public void test_LoadItemsByEntitiyId() throws Exception
    {
        checkSpinnerCtrlerLoadItems(controller);
    }
}