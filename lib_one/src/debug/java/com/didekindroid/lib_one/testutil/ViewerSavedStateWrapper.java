package com.didekindroid.lib_one.testutil;

import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;

/**
 * User: pedro@didekin
 * Date: 16/09/17
 * Time: 15:00
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ViewerSavedStateWrapper {

    public final String AFTER_SaveState = "afterSaveStateMethod";
    public final AtomicReference<String> flagMethod = new AtomicReference<>(BEFORE_METHOD_EXEC);

    public int clearSubscriptions()
    {
        flagMethod.set("afterClearSubscriptions");
        return 0;
    }

    public void saveState()
    {
        flagMethod.set(AFTER_SaveState);
    }

    /*public void checkOnSaveInstanceState(final Viewer viewer)
    {
        viewer.getActivity().runOnUiThread(() -> getInstrumentation().callActivityOnSaveInstanceState(viewer.getActivity(), new Bundle(0)));
        waitAtMost(6, SECONDS).untilAtomic(flagMethod, is(AFTER_SaveState));
    }*/
}
