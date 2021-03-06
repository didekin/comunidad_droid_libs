package com.didekindroid.lib_one.usuario;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.ComunidadAutonoma;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

import static com.didekindroid.lib_one.FirebaseInitializer.firebaseInitializer;
import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.usuario.UserMockDao.usuarioMockDao;
import static com.didekindroid.lib_one.usuario.dao.UsuarioDao.usuarioDaoRemote;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static com.didekinlib.model.usuario.http.TkValidaPatterns.tkEncrypted_direct_symmetricKey_REGEX;
import static com.didekinlib.model.usuariocomunidad.Rol.PROPIETARIO;

/**
 * User: pedro@didekin
 * Date: 12/02/2018
 * Time: 14:04
 */

@SuppressWarnings("WeakerAccess")
public final class UserTestData {

    public static final Usuario user_crodrigo = new Usuario.UsuarioBuilder()
            .userName("crodrigo@didekin.es")
            .alias("alias_rodrigo")
            .password("psw_rodrigo")
            .build();

    public static final Usuario USER_DROID = new Usuario.UsuarioBuilder()
            .userName("didekindroid@didekin.es")
            .alias("didekindroid")
            .password("psw_droid")
            .build();

    public static final Usuario USER_JUAN = new Usuario.UsuarioBuilder()
            .userName("crodrigo@didekin.es")
            .alias("alias_juan")
            .password("psw_juan")
            .build();

    public static final Usuario USER_JUAN2 = new Usuario.UsuarioBuilder()
            .userName("juan2@didekin.es")
            .alias("alias_juan2")
            .password("pswd01")
            .build();

    public static final Usuario USER_PEPE = new Usuario.UsuarioBuilder()
            .userName("pedro@didekin.es")
            .alias("alias_pepe")
            .password("psw_pepe")
            .build();

    // Municipio: Algueña  Provincia: Alicante/Alacant
    public static final Comunidad comu_real = new Comunidad.ComunidadBuilder()
            .tipoVia("Calle")
            .nombreVia("Real")
            .numero((short) 5)
            .sufijoNumero("Bis")
            .municipio(new Municipio((short) 13, "Algueña", new Provincia(new ComunidadAutonoma((short) 10), (short) 3, "Alicante/Alacant")))
            .build();

    public static final UsuarioComunidad comu_real_rodrigo = new UsuarioComunidad.UserComuBuilder(comu_real, user_crodrigo)
            .portal("portal")
            .escalera("esc")
            .planta("plantaX")
            .puerta("door12")
            .roles(PROPIETARIO.function).build();

    public static final String authTokenExample =
                    "eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2In0" +
                            "." +
                            "._L86WbOFHY-3g0E2EXejJg" +
                            ".UB1tHZZq0TYFTZKPVZXY83GRxHz770Aq7BuMCEbNnaSC5cVNOLEOgBQrOQVJmVL-9Ke9KRSwuq7MmVcA2EB_0xRBr_YbzmMWbpUcTQUFtE5OZOFiCsxL5Yn0gA_DDLZboivpoSqndQRP-44mWVkM1A" +
                            ".RIvTWRrsyoJ1mpl8vUhQDQ";

    private UserTestData()
    {
    }

    public static final Consumer<String> updateAuthCacheNewUser = newAuthTk -> secInitializer.get().getTkCacher().updateAuthToken(newAuthTk);

    public static final BiFunction<UsuarioComunidad, String, UsuarioComunidad> getUserComuWithTk =
            (userComuIn, gcmToken) -> new UsuarioComunidad.UserComuBuilder
                    (
                            userComuIn.getComunidad(),
                            new Usuario.UsuarioBuilder()
                                    .copyUsuario(userComuIn.getUsuario())
                                    .gcmToken(gcmToken)  // We need this token in the server.
                                    .build()
                    )
                    .userComuRest(userComuIn)
                    .build();

    // =========================  Register methods =========================

    public static String regComuUserUserComuGetAuthTk(UsuarioComunidad userComuIn) throws Exception
    {
        String appIdToken = firebaseInitializer.get().getSingleAppIdTokenForTest().blockingGet();
        return regComuUserUserComuMock(userComuIn, appIdToken);
    }

    public static String regComuUserUserComuMock(UsuarioComunidad userComuIn, String gcmToken) throws Exception
    {
        return usuarioMockDao.regComuAndUserAndUserComu(getUserComuWithTk.apply(userComuIn, gcmToken))
                .map(response -> httpInitializer.get().getResponseBody(response))
                .doOnSuccess(updateAuthCacheNewUser)
                .blockingGet();
    }

    public static Usuario regComuUserUserComuGetUser(UsuarioComunidad userComuIn) throws Exception
    {
        assertTrue(tkEncrypted_direct_symmetricKey_REGEX.isPatternOk(regComuUserUserComuGetAuthTk(userComuIn)), "authToken not null");
        return usuarioDaoRemote.getUserData().blockingGet();

    }

    // =========================  Cleaning methods =========================

    public static void cleanOneUser(String userName)
    {
        assertTrue(
                usuarioMockDao.deleteUser(userName)
                        .map(response -> httpInitializer.get().getResponseBody(response))
                        .blockingGet(),
                "OK deletion");
        cleanWithTkhandler();
    }

    public static void cleanTwoUsers(String userNameOne, String userNameTwo)
    {
        cleanOneUser(userNameOne);
        cleanOneUser(userNameTwo);
    }

    public static void cleanWithTkhandler()
    {
        try {
            secInitializer.get().getTkCacher().updateAuthToken(null);
        } catch (UiException e) {
            Timber.e(" =========== ERROR: %s", e.getErrorHtppMsg());
        }
    }

    public static void cleanOptions(CleanUserEnum whatClean)
    {
        switch (whatClean) {
            case CLEAN_TK_HANDLER:
                cleanWithTkhandler();
                break;
            case CLEAN_JUAN:
                cleanOneUser(USER_JUAN.getUserName());
                break;
            case CLEAN_PEPE:
                cleanOneUser(USER_PEPE.getUserName());
                break;
            case CLEAN_JUAN2:
                cleanOneUser(USER_JUAN2.getUserName());
                break;
            case CLEAN_DROID:
                cleanOneUser(USER_DROID.getUserName());
                break;
            case CLEAN_RODRIGO:
                cleanOneUser(user_crodrigo.getUserName());
                break;
            case CLEAN_JUAN_AND_PEPE:
                cleanTwoUsers(USER_JUAN.getUserName(), USER_PEPE.getUserName());
                break;
            case CLEAN_JUAN2_AND_PEPE:
                cleanTwoUsers(USER_JUAN2.getUserName(), USER_PEPE.getUserName());
                break;
            case CLEAN_NOTHING:
                break;
            default:
                throw new IllegalStateException("Wrong cleanUp");
        }
    }

    // =========================  Inner classes =========================

    public enum CleanUserEnum {

        CLEAN_JUAN,
        CLEAN_PEPE,
        CLEAN_JUAN_AND_PEPE,
        CLEAN_JUAN2_AND_PEPE,
        CLEAN_TK_HANDLER,
        CLEAN_NOTHING,
        CLEAN_JUAN2,
        CLEAN_DROID,
        CLEAN_RODRIGO,;
    }
}
