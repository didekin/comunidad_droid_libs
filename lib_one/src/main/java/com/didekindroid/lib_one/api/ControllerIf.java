package com.didekindroid.lib_one.api;

import com.didekindroid.lib_one.security.AuthTkCacherIf;

import io.reactivex.disposables.CompositeDisposable;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 13:27
 */
public interface ControllerIf {

    CompositeDisposable getSubscriptions();

    int clearSubscriptions();

    boolean isRegisteredUser();

    AuthTkCacherIf getTkCacher();
}
