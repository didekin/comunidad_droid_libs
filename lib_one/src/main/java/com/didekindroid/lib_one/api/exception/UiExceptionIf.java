package com.didekindroid.lib_one.api.exception;

import com.didekinlib.http.exception.ErrorBean;

import io.reactivex.functions.Consumer;

import static com.didekinlib.http.exception.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;

/**
 * User: pedro@didekin
 * Date: 16/11/16
 * Time: 14:07
 */

public interface UiExceptionIf {

    ErrorBean getErrorBean();

    String getErrorHtppMsg();

}
