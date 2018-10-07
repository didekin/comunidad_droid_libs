package com.didekindroid.lib_one.usuario.dao;

import com.didekindroid.lib_one.api.HttpInitializerIf;
import com.didekindroid.lib_one.security.AuthTkCacherIf;
import com.didekindroid.lib_one.security.SecInitializerIf;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuario.http.UsuarioEndPoints;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekindroid.lib_one.FirebaseInitializer.firebaseInitializer;
import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekindroid.lib_one.api.exception.UiException.uiExceptionConsumer;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
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

    private UsuarioDao(SecInitializerIf secInitializerIn, HttpInitializerIf httpInitializerIn)
    {
        endPoint = httpInitializerIn.getHttpHandler().getService(UsuarioEndPoints.class);
        tkCacher = secInitializerIn.getTkCacher();
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
        return firebaseInitializer.get().getSingleToken()
                .flatMap(gcmToken -> deleteUser(tkCacher.doAuthHeaderStr(gcmToken)))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer)
                .doOnSuccess(isDeleted -> {
                    if (isDeleted) {
                        tkCacher.updateAuthToken(null);
                    }
                })
                .ignoreElement();
    }

    public Single<String> getGcmToken()
    {
        Timber.d("getGcmToken(), Thread: %s", currentThread().getName());
        return firebaseInitializer.get().getSingleToken()
                .flatMap(gcmToken -> getUserData(tkCacher.doAuthHeaderStr(gcmToken)))
                .flatMap(getResponseSingleFunction())
                .map(Usuario::getGcmToken)
                .doOnError(uiExceptionConsumer);
    }

    //
    public Single<Usuario> getUserData()
    {
        Timber.d("getUserData(), Thread: %s", currentThread().getName());
        return firebaseInitializer.get().getSingleToken()
                .flatMap(gcmToken -> getUserData(tkCacher.doAuthHeaderStr(gcmToken)))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer);
    }

    /**
     * This method should be called asynchronously.
     */
    public Completable login(String userName, String password)
    {
        Timber.d("login(), Thread: %s", currentThread().getName());
        return firebaseInitializer.get().getSingleToken()
                .flatMap(gcmToken -> login(userName, password, gcmToken))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer)
                .doOnSuccess(tkCacher::updateAuthToken)
                .ignoreElement();
    }

    public Single<Boolean> modifyUserName(Usuario usuario)
    {
        Timber.d("modifyUserName(), Thread: %s", currentThread().getName());
        return firebaseInitializer.get().getSingleToken()
                .flatMap(gcmToken -> modifyUser(getDeviceLanguage(), tkCacher.doAuthHeaderStr(gcmToken), usuario))
                .flatMap(getResponseSingleFunction())
                .map(response -> response > 0)
                .doOnError(uiExceptionConsumer);
    }

    public Single<Boolean> modifyUserAlias(Usuario usuario)
    {
        Timber.d("modifyUseAlias(), Thread: %s", currentThread().getName());
        return firebaseInitializer.get().getSingleToken()
                .flatMap(gcmToken -> modifyUser(getDeviceLanguage(), tkCacher.doAuthHeaderStr(gcmToken), usuario))
                .flatMap(getResponseSingleFunction())
                .map(response -> response > 0)
                .doOnError(uiExceptionConsumer);
    }

    public Completable passwordChange(String oldPswd, String newPassword)
    {
        Timber.d("passwordChange(), Thread: %s", currentThread().getName());
        return firebaseInitializer.get().getSingleToken()
                .flatMap(gcmToken -> passwordChange(tkCacher.doAuthHeaderStr(gcmToken), oldPswd, newPassword))
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