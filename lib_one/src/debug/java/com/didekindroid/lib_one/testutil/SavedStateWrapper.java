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
    private final AtomicReference<String> flagMethod = new AtomicReference<>(BEFORE_METHOD_EXEC);

    public void saveState()
    {
        flagMethod.set(AFTER_SaveState);
    }

    public String getFlagMethod()
    {
        return flagMethod.get();
    }
}
