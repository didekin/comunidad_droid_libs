package com.didekindroid.lib_one.incidencia.spinner;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.security.AuthTkCacher;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.lib_one.incidencia.IncidenciaDataDb.AmbitoIncidencia.AMBITO_INCID_COUNT;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.testutil.UiTestUtil.checkSpinnerCtrlerLoadItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 30/03/17
 * Time: 16:31
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerAmbitoIncidSpinnerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    private CtrlerAmbitoIncidSpinner controller;

    @Before
    public void setUp()
    {
        controller = new CtrlerAmbitoIncidSpinner(new AuthTkCacher(activityRule.getActivity()));
    }

    @After
    public void clear()
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
    }

    @Test
    public void testAmbitoIncidList()
    {
        controller.ambitoIncidList().test().assertOf(listTestObserver -> {
            assertThat(listTestObserver.values().get(0).size(), is(AMBITO_INCID_COUNT));
            assertThat(listTestObserver.values().size(), is(1));
        });
    }

    @Test
    public void test_LoadItemsByEntitiyId() throws Exception
    {
        checkSpinnerCtrlerLoadItems(controller);
    }
}