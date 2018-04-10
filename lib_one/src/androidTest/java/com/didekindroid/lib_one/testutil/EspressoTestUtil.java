package com.didekindroid.lib_one.testutil;

import android.app.Activity;
import android.content.res.Resources;
import android.support.test.espresso.NoMatchingRootException;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.RootMatchers;
import android.support.test.runner.lifecycle.Stage;
import android.view.View;

import com.didekindroid.lib_one.R;
import com.didekindroid.lib_one.api.ControllerIf;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

import timber.log.Timber;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance;
import static com.didekindroid.lib_one.security.TokenIdentityCacher.refresh_token_filename;
import static com.didekindroid.lib_one.testutil.UiTestUtil.addSubscription;
import static com.didekindroid.lib_one.util.IoHelper.writeFileFromString;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 11/02/2018
 * Time: 15:22
 */
@SuppressWarnings({"WeakerAccess", "unused", "EmptyCatchBlock"})
public class EspressoTestUtil {

    public static Callable<Boolean> isResourceIdDisplayed(final Integer... resourceIds)
    {
        return () -> {
            try {
                for (int resourceId : resourceIds) {
                    onView(withId(resourceId)).check(matches(isDisplayed()));
                }
                return true;
            } catch (NoMatchingViewException ne) {
                return false;
            }
        };
    }

    public static Callable<Boolean> isViewDisplayed(final Matcher<View> viewMatcher)
    {
        return () -> {
            try {
                onView(viewMatcher).check(matches(isDisplayed()));
                return true;
            } catch (NoMatchingViewException ne) {
                return false;
            }
        };
    }

    public static Callable<Boolean> isViewDisplayedAndPerform(final Matcher<View> viewMatcher, final ViewAction... viewActions)
    {
        return () -> {
            try {
                onView(viewMatcher).check(matches(isDisplayed())).perform(viewActions);
                return true;
            } catch (NoMatchingViewException ne) {
                return false;
            }
        };
    }

    // ============================  Controllers  ============================

    public static void checkSubscriptionsOnStop(final Activity activity, final ControllerIf... controllers)
    {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        for (ControllerIf controller : controllers) {
            atomicInteger.addAndGet(addSubscription(controller).size());
        }
        assertThat(atomicInteger.get() >= controllers.length, is(true));

        activity.runOnUiThread(() -> {
            getInstrumentation().callActivityOnStop(activity);
            atomicInteger.set(0);
            for (ControllerIf controller : controllers) {
                atomicInteger.addAndGet(controller.getSubscriptions().size());
            }
        });

        waitAtMost(6, SECONDS).untilAtomic(atomicInteger, is(0));
    }

    // ============================  Dialogs  ============================

    public static void checkTextsInDialog(int... textsDialogs)
    {
        for (int textsDialog : textsDialogs) {
            waitAtMost(6, SECONDS).until(() -> {
                try {
                    onView(withText(textsDialog)).inRoot(isDialog()).check(matches(isDisplayed()));
                    return true;
                } catch (NoMatchingViewException ne) {
                    return false;
                }
            });
        }
    }

    // ============================  Menu  ============================

    public static void checkItemMnExists(Activity activity, int menuResourceId, int nextLayoutId)
    {
        onView(withText(menuResourceId)).check(doesNotExist());
        openActionBarOverflowOrOptionsMenu(activity);
        waitAtMost(4, SECONDS).until(() -> {
            try {
                onView(withText(menuResourceId)).check(matches(isDisplayed())).perform(click());
                return true;
            } catch (NoMatchingViewException ne) {
                return false;
            }
        });
        onView(withId(nextLayoutId)).check(matches(isDisplayed()));
    }

    public static void checkItemMnNotExists(Activity activity, int menuResourceId)
    {
        onView(withText(menuResourceId)).check(doesNotExist());
        try {
            openActionBarOverflowOrOptionsMenu(activity);
        } catch (NoMatchingViewException e) {
        }
        waitAtMost(4, SECONDS).until(() -> {
            try {
                for (int resourceId : new Integer[]{menuResourceId}) {
                    onView(withText(resourceId)).check(doesNotExist());
                }
                return true;
            } catch (NoMatchingViewException ne) {
                return false;
            }
        });
    }

    // ============================  Navigation  ============================

    public static void checkUp(Integer... activityLayoutIds)
    {
        clickNavigateUp();
        for (Integer layout : activityLayoutIds) {
            try {
                waitAtMost(6, SECONDS).until(isResourceIdDisplayed(layout));
            } catch (Exception e) {
                fail();
            }
        }
    }

    public static void clickNavigateUp()
    {
        onView(allOf(
                withContentDescription(R.string.navigate_up_txt),
                isClickable())
        ).check(matches(isDisplayed())).perform(click());
    }

    public static void checkBack(ViewInteraction viewInteraction, Integer... activityLayoutIds)
    {
        viewInteraction.perform(closeSoftKeyboard()).perform(ViewActions.pressBack());
        for (Integer layout : activityLayoutIds) {
            try {
                waitAtMost(6, SECONDS).until(isResourceIdDisplayed(layout));
            } catch (Exception e) {
                fail();
            }
        }
    }

    public static Collection<Activity> getActivitesInTaskByStage(final Stage stage) throws ExecutionException, InterruptedException
    {
        Timber.d("============= getActivitesInTaskByStage() =================");

        final FutureTask<Collection<Activity>> taskGetActivities = new FutureTask<>(() -> getInstance().getActivitiesInStage(stage));
        getInstrumentation().runOnMainSync(taskGetActivities);
        return taskGetActivities.get();
    }

    public static Stage getStageByActivity(final Activity activity) throws ExecutionException, InterruptedException
    {
        Timber.d("============= getStageByActivity() =================");

        final FutureTask<Stage> taskGetActivities = new FutureTask<>(() -> getInstance().getLifecycleStageOf(activity));
        getInstrumentation().runOnMainSync(taskGetActivities);
        return taskGetActivities.get();
    }

    //    ============================ Security ============================

    public static void writeFile(String stringToFile)
    {
        File refreshTkFile = new File(getTargetContext().getFilesDir(), refresh_token_filename);
        writeFileFromString(stringToFile, refreshTkFile);
    }

    //    ============================ TOASTS ============================

    public static void checkToastInTest(int resourceId, Activity activity, int... resourceFieldsErrorId)
    {
        Resources resources = activity.getResources();

        ViewInteraction toast = onView(
                withText(Matchers.containsString(resources.getText(resourceId).toString())))
                .inRoot(RootMatchers.withDecorView(CoreMatchers.not(activity.getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        if (resourceFieldsErrorId != null) {
            for (int field : resourceFieldsErrorId) {
                toast.check(matches(withText(Matchers.containsString(resources.getText(field).toString()))));
            }
        }
    }

    public static Callable<Boolean> isToastInView(final int resourceStringId, final Activity activity, final int... resorceErrorId)
    {
        return () -> {
            try {
                checkToastInTest(resourceStringId, activity, resorceErrorId);
                return true;
            } catch (NoMatchingViewException | NoMatchingRootException ne) {
                return false;
            }
        };
    }
}
