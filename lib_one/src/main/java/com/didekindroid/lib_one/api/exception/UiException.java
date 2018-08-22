package com.didekindroid.lib_one.api.exception;

import com.didekinlib.http.exception.ErrorBean;

import io.reactivex.functions.Consumer;

import static com.didekinlib.http.exception.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;

/**
 * User: pedro@didekin
 * Date: 17/11/16
 * Time: 17:41
 */
public class UiException extends Exception implements UiExceptionIf {

    public static final Consumer<Throwable> uiExceptionConsumer = exception -> {
        if (!UiException.class.isInstance(exception)) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    };

    private final ErrorBean errorBean;

    public UiException(ErrorBean errorBean)
    {
        this.errorBean = errorBean;
    }

    @Override
    public ErrorBean getErrorBean()
    {
        return errorBean;
    }

    @Override
    public String getErrorHtppMsg()
    {
        return errorBean.getMessage();
    }

}
