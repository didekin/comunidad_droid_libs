package com.didekindroid.lib_one.comunidad.spinner;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb;
import com.didekindroid.lib_one.security.AuthTkCacher;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.testutil.UiTestUtil.checkSpinnerCtrlerLoadItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 03/05/17
 * Time: 11:03
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerTipoViaSpinnerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    private CtrlerTipoViaSpinner controller;

    @Before
    public void setUp()
    {
        controller = new CtrlerTipoViaSpinner(new AuthTkCacher(activityRule.getActivity()));
    }

    @After
    public void tearDown()
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
    }

    @Test
    public void test_TipoViaList()
    {
        controller.tipoViaList().test()
                .assertOf(listTestObserver -> {
                    assertThat(listTestObserver.values().size(), is(1)); // Single.
                    assertThat(listTestObserver.values().get(0).size(), is(ComunidadDataDb.TipoVia.NUMBER_RECORDS));
                });
    }

    @Test
    public void test_LoadItemsByEntitiyId() throws Exception
    {
        checkSpinnerCtrlerLoadItems(controller);
    }
}