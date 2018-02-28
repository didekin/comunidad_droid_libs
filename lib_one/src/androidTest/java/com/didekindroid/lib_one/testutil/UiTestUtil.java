package com.didekindroid.lib_one.testutil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.api.CtrlerSelectListIf;
import com.didekindroid.lib_one.api.ViewerSelectListIf;
import com.didekindroid.lib_one.util.BundleKey;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;

import java.io.Serializable;
import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 18/02/2018
 * Time: 13:28
 */

@SuppressWarnings("ConstantConditions")
public class UiTestUtil {

    public static <E extends Serializable> void checkSpinnerCtrlerLoadItems(CtrlerSelectListIf<E> controller, Long... entityId)
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.loadItemsByEntitiyId(new DisposableSingleObserver<List<E>>() {
                @Override
                public void onSuccess(List<E> es)
                {
                }

                @Override
                public void onError(Throwable e)
                {
                    fail();
                }
            }, entityId), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    public static void checkSavedStateWithItemSelected(ViewerSelectListIf viewer, BundleKey bundleKey)
    {
        viewer.setSelectedItemId(18L);
        Bundle bundle = new Bundle(1);
        viewer.saveState(bundle);
        Assert.assertThat(bundle.getLong(bundleKey.getKey()), CoreMatchers.is(18L));
    }

    public static EditText doEditTextView(int resourdeIdLayout, String description)
    {
        LayoutInflater inflater = (LayoutInflater) getTargetContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        EditText editText = inflater.inflate(resourdeIdLayout, null).findViewById(R.id.incid_reg_desc_ed);
        editText.setText(description);
        return editText;
    }

}
