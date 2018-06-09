package com.didekindroid.lib_one.comunidad.spinner;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.ActivityMock;
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
 * Date: 05/05/17
 * Time: 19:13
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerProvinciaSpinnerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    private CtrlerProvinciaSpinner controller;

    @Before
    public void setUp()
    {
        controller = new CtrlerProvinciaSpinner(new AuthTkCacher(activityRule.getActivity()));
    }

    @After
    public void tearDown()
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
    }

    @Test
    public void test_ProvinciasByComAutonoma()
    {
        controller.provinciasByComAutonoma((short) 11).test()
                .assertOf(listTestObserver -> {
                    assertThat(listTestObserver.values().size(), is(1)); // Single.
                    assertThat(listTestObserver.values().get(0).size(), is(2));
                    assertThat(listTestObserver.values().get(0).get(0).getNombre(), is("Badajoz"));
                });
    }

    @Test
    public void test_LoadItemsByEntitiyId()
    {
        checkSpinnerCtrlerLoadItems(controller, 11L);
    }
}