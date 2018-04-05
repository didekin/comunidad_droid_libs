package com.didekindroid.lib_one.usuario.testutil;

import com.didekindroid.lib_one.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 12:21
 */

@SuppressWarnings("unused")
public final class UserEspressoTestUtil {

    private UserEspressoTestUtil()
    {
    }

    public static void typeUserNameAlias(String email, String alias)
    {
        onView(withId(R.id.reg_usuario_email_editT)).perform(scrollTo(), replaceText(email));
        onView(withId(R.id.reg_usuario_alias_ediT)).perform(scrollTo(), replaceText(alias), closeSoftKeyboard());
    }

    public static void typeUserNamePswd(String userName, String password)
    {
        onView(withId(R.id.reg_usuario_email_editT)).perform(replaceText(userName));
        onView(withId(R.id.password_validation_ediT)).perform(replaceText(password), closeSoftKeyboard());
    }

    public static void typeUserNameAliasPswd(String userName, String alias, String password)
    {
        onView(withId(R.id.reg_usuario_email_editT)).perform(replaceText(userName));
        onView(withId(R.id.reg_usuario_alias_ediT)).perform(replaceText(alias));
        onView(withId(R.id.password_validation_ediT)).perform(replaceText(password), closeSoftKeyboard());
    }

    public static void typePswdConfirmPswd(String password, String confirmation)
    {
        onView(withId(R.id.reg_usuario_password_ediT)).perform(replaceText(password));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(replaceText(confirmation), closeSoftKeyboard());
    }

    public static void typePswdWithPswdValidation(String password, String confirmation, String currentPassword)
    {
        onView(withId(R.id.reg_usuario_password_ediT)).perform(replaceText(password));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(replaceText(confirmation));
        onView(withId(R.id.password_validation_ediT)).perform(replaceText(currentPassword), closeSoftKeyboard());
    }

    public static void typeLoginData(String userName, String password)
    {
        onView(withId(R.id.reg_usuario_email_editT)).perform(replaceText(userName));
        if (password != null) {
            onView(withId(R.id.reg_usuario_password_ediT)).perform(typeText(password));
        }
    }
}
