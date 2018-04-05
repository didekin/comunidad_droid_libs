package com.didekindroid.lib_one.testutil;

import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;

/**
 * User: pedro@didekin
 * Date: 16/09/17
 * Time: 15:00
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class SavedStateWrapper {

    public static final String AFTER_SaveState = "afterSaveStateMethod";
    public static final String AFTER_ClearSubscriptions = "afterClearSubscriptions";
    public final AtomicReference<String> flagMethod = new AtomicReference<>(BEFORE_METHOD_EXEC);

    public void clearSubscription()
    {
        flagMethod.set(AFTER_ClearSubscriptions);
    }

    public void saveState()
    {
        flagMethod.set(AFTER_SaveState);
    }

    public AtomicReference<String> getFlagMethod()
    {
        return flagMethod;
    }

    /*public void checkOnSaveInstanceState(final Viewer viewer)
    {
        viewer.getActivity().runOnUiThread(() -> getInstrumentation().callActivityOnSaveInstanceState(viewer.getActivity(), new Bundle(0)));
        waitAtMost(6, SECONDS).untilAtomic(flagMethod, is(AFTER_SaveState));
    }*/
}
