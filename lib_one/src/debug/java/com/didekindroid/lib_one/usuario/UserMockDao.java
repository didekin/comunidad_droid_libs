package com.didekindroid.lib_one.usuario;

import com.didekinlib.http.HttpHandler;
import com.didekinlib.http.usuario.UserMockEndPoints;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Response;

import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;

/**
 * User: pedro@didekin
 * Date: 21/11/16
 * Time: 19:15
 */
public class UserMockDao implements UserMockEndPoints {

    public static final UserMockDao usuarioMockDao = new UserMockDao(httpInitializer.get().getHttpHandler());
    private final UserMockEndPoints endPoint;

    private UserMockDao(HttpHandler httpHandlerIn)
    {
        endPoint = httpHandlerIn.getService(UserMockEndPoints.class);
    }

    @Override
    public Single<Response<Boolean>> deleteUser(String userName)
    {
        return endPoint.deleteUser(userName);
    }

    @Override
    public Single<Response<String>> regComuAndUserAndUserComu(UsuarioComunidad usuarioCom)
    {
        return endPoint.regComuAndUserAndUserComu(usuarioCom);
    }

    @Override
    public Single<Response<String>> regUserAndUserComu(UsuarioComunidad usuarioComunidad)
    {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    @Override
    public Call<String> tryTokenInterceptor(String s, String s1, String s2)
    {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }
}
