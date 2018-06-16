package com.didekindroid.lib_one;

import com.didekindroid.lib_one.accesorio.ConfidencialidadAc;
import com.didekindroid.lib_one.accesorio.ConfidencialidadAcTest;
import com.didekindroid.lib_one.api.ControllerTest;
import com.didekindroid.lib_one.api.ObserverSingleListTest;
import com.didekindroid.lib_one.api.ObserverSingleSelectItemTest;
import com.didekindroid.lib_one.api.ObserverSingleSelectListTest;
import com.didekindroid.lib_one.api.ParentViewerTest;
import com.didekindroid.lib_one.api.ViewerSelectListTest;
import com.didekindroid.lib_one.api.ViewerTest;
import com.didekindroid.lib_one.api.router.FragmentInitiatorTest;
import com.didekindroid.lib_one.api.router.RouterActionIfTest;
import com.didekindroid.lib_one.comunidad.repository.ComunidadDbHelperTest;
import com.didekindroid.lib_one.comunidad.spinner.CtrlerComAutonomaSpinnerTest;
import com.didekindroid.lib_one.comunidad.spinner.CtrlerMunicipioSpinnerTest;
import com.didekindroid.lib_one.comunidad.spinner.CtrlerProvinciaSpinnerTest;
import com.didekindroid.lib_one.comunidad.spinner.CtrlerTipoViaSpinnerTest;
import com.didekindroid.lib_one.comunidad.spinner.ViewerComuAutonomaSpinnerTest;
import com.didekindroid.lib_one.comunidad.spinner.ViewerMunicipioSpinnerTest;
import com.didekindroid.lib_one.comunidad.spinner.ViewerProvinciaSpinnerTest;
import com.didekindroid.lib_one.comunidad.spinner.ViewerTipoViaSpinnerTest;
import com.didekindroid.lib_one.incidencia.IncidenciaBeanTest;
import com.didekindroid.lib_one.incidencia.IncidenciaDataDbHelperTest;
import com.didekindroid.lib_one.incidencia.spinner.CtrlerAmbitoIncidSpinnerTest;
import com.didekindroid.lib_one.incidencia.spinner.ViewerAmbitoIncidSpinnerTest;
import com.didekindroid.lib_one.security.SecuritySuite;
import com.didekindroid.lib_one.usuario.UsuarioSuite;
import com.didekindroid.lib_one.util.DeviceTest;
import com.didekindroid.lib_one.util.IoHelperTest;
import com.didekindroid.lib_one.util.UiUtilTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 18/02/17
 * Time: 12:33
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AppContextTest.class,
        // accesorio
        ConfidencialidadAcTest.class,
        // api.router.
        FragmentInitiatorTest.class,
        RouterActionIfTest.class,
        // api.
        ControllerTest.class,
        ObserverSingleListTest.class,
        ObserverSingleSelectItemTest.class,
        ObserverSingleSelectListTest.class,
        ParentViewerTest.class,
        ViewerSelectListTest.class,
        ViewerTest.class,
        // comunidad.respository.
        ComunidadDbHelperTest.class,
        // comunidad.spinner.
        CtrlerComAutonomaSpinnerTest.class,
        CtrlerMunicipioSpinnerTest.class,
        CtrlerProvinciaSpinnerTest.class,
        CtrlerTipoViaSpinnerTest.class,
        ViewerComuAutonomaSpinnerTest.class,
        ViewerMunicipioSpinnerTest.class,
        ViewerProvinciaSpinnerTest.class,
        ViewerTipoViaSpinnerTest.class,
        // incidencia.spinner.
        CtrlerAmbitoIncidSpinnerTest.class,
        ViewerAmbitoIncidSpinnerTest.class,
        IncidenciaBeanTest.class,
        IncidenciaDataDbHelperTest.class,
        // security.
        SecuritySuite.class,
        // usuario.
        UsuarioSuite.class,
        // util.
        DeviceTest.class,
        IoHelperTest.class,
        UiUtilTest.class,
})
public class LibOneSuite {
}
