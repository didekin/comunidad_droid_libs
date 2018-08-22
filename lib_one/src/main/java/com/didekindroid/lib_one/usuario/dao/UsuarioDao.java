package com.didekindroid.lib_one.usuario.dao;

import com.didekindroid.lib_one.api.HttpInitializerIf;
import com.didekindroid.lib_one.security.AuthTkCacherIf;
import com.didekindroid.lib_one.security.SecInitializerIf;
import com.didekinlib.http.usuario.UsuarioEndPoints;
import com.didekinlib.model.usuario.Usuario;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekindroid.lib_one.api.exception.UiException.uiExceptionConsumer;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.usuario.dao.AppIdHelper.appIdSingle;
import static com.didekindroid.lib_one.util.Device.getDeviceLanguage;
import static com.didekindroid.lib_one.util.RxJavaUtil.getResponseSingleFunction;
import static io.reactivex.Single.just;
import static java.lang.Thread.currentThread;


/**
 * User: pedro@didekin
 * Date: 07/06/15
 * Time: 15:06
 */
public final class UsuarioDao implements UsuarioEndPoints {

    public static final UsuarioDao usuarioDaoRemote = new UsuarioDao(secInitializer.get(), httpInitializer.get());
    private final AuthTkCacherIf tkCacher;
    private final UsuarioEndPoints endPoint;
    private final AppIdHelper idHelper;

    private UsuarioDao(SecInitializerIf secInitializerIn, HttpInitializerIf httpInitializerIn)
    {
        endPoint = httpInitializerIn.getHttpHandler().getService(UsuarioEndPoints.class);
        tkCacher = secInitializerIn.getTkCacher();
        idHelper = appIdSingle;
    }

    //  ================================== UsuarioEndPoints implementation ============================

    @Override
    public Single<Response<Boolean>> deleteUser(String authHeader)
    {
        return endPoint.deleteUser(authHeader);
    }

    @Override
    public Single<Response<Usuario>> getUserData(String authHeader)
    {
        return endPoint.getUserData(authHeader);
    }

    @Override
    public Single<Response<String>> login(String userName, String password, String appID)
    {
        return endPoint.login(userName, password, appID);
    }

    @Override
    public Single<Response<String>> modifyGcmToken(String authHeader, String gcmToken)
    {
        return endPoint.modifyGcmToken(authHeader, gcmToken);
    }

    @Override
    public Single<Response<Integer>> modifyUser(String deviceLanguage, String authHeader, Usuario usuario)
    {
        return endPoint.modifyUser(deviceLanguage, authHeader, usuario);
    }

    @Override
    public Single<Response<String>> passwordChange(String authHeader, String oldPswd, String newPassword)
    {
        return endPoint.passwordChange(authHeader, oldPswd, newPassword);
    }

    @Override
    public Single<Response<Boolean>> passwordSend(String deviceLanguage, String userName)
    {
        Timber.d("passwordSend()");
        return endPoint.passwordSend(deviceLanguage, userName);
    }

//  =============================================================================
//                          CONVENIENCE METHODS
//  =============================================================================

    public Completable deleteUser()
    {
        Timber.d("deleteUser(), Thread: %s", currentThread().getName());
        return just(true)
                .flatMap(booleanIn -> deleteUser(tkCacher.doAuthHeaderStr()))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer)
                .doOnSuccess(isDeleted -> {
                    if (isDeleted) {
                        tkCacher.updateIsRegistered(false);
                    }
                })
                .ignoreElement();
    }

    public Single<String> getGcmToken()
    {
        Timber.d("getGcmToken(), Thread: %s", currentThread().getName());
        return just(true)
                .flatMap(emptyStr -> getUserData(tkCacher.doAuthHeaderStr()))
                .flatMap(getResponseSingleFunction())
                .map(Usuario::getGcmToken)
                .doOnError(uiExceptionConsumer);
    }

    //
    public Single<Usuario> getUserData()
    {
        Timber.d("getUserData(), Thread: %s", currentThread().getName());
        return just(true)
                .flatMap(booleanIn -> getUserData(tkCacher.doAuthHeaderStr()))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer);
    }

    /**
     *  This method should be called asynchronously.
     */
    public Completable login(String userName, String password)
    {
        Timber.d("login(), Thread: %s", currentThread().getName());
        return just(true)
                .flatMap(booleanIn -> login(userName, password, idHelper.getTokenSingle().blockingGet()))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer)
                .doOnSuccess(newAuthTk -> tkCacher.updateAuthToken(newAuthTk).updateIsGcmTokenSentServer(true))
                .ignoreElement();
    }

    public Completable modifyGcmToken(String gcmToken)
    {
        Timber.d("modifyGcmToken(), Thread: %s", currentThread().getName());
        return just(gcmToken)
                .flatMap(gcmTokenIn -> modifyGcmToken(tkCacher.doAuthHeaderStr(), gcmTokenIn))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer)
                .doOnSuccess(newAuthTk -> tkCacher.updateAuthToken(newAuthTk).updateIsGcmTokenSentServer(true))
                .ignoreElement();
    }

    public Single<Boolean> modifyUserName(Usuario usuario)
    {
        Timber.d("modifyUserName(), Thread: %s", currentThread().getName());
        return just(usuario)
                .flatMap(usuarioIn -> modifyUser(getDeviceLanguage(), tkCacher.doAuthHeaderStr(), usuarioIn))
                .flatMap(getResponseSingleFunction())
                .map(response -> response > 0)
                .doOnError(uiExceptionConsumer);
    }

    public Single<Boolean> modifyUserAlias(Usuario usuario)
    {
        Timber.d("modifyUseAlias(), Thread: %s", currentThread().getName());
        return just(usuario)
                .flatMap(usuarioIn -> modifyUser(getDeviceLanguage(), tkCacher.doAuthHeaderStr(), usuarioIn))
                .flatMap(getResponseSingleFunction())
                .map(response -> response > 0)
                .doOnError(uiExceptionConsumer);
    }

    public Completable passwordChange(String oldPswd, String newPassword)
    {
        Timber.d("passwordChange(), Thread: %s", currentThread().getName());
        return just(true)
                .flatMap(booleanIn -> passwordChange(tkCacher.doAuthHeaderStr(), oldPswd, newPassword))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer)
                .doOnSuccess(tkCacher::updateAuthToken)
                .ignoreElement();
    }

    public Completable passwordSend(String userName)
    {
        Timber.d("passwordSend(), Thread: %s", currentThread().getName());
        return just(userName)
                .flatMap(userNameIn -> passwordSend(getDeviceLanguage(), userNameIn))
                .flatMap(getResponseSingleFunction())
                .doOnSuccess(isSent -> tkCacher.updateAuthToken(null))
                .doOnError(uiExceptionConsumer)
                .ignoreElement();
    }
}