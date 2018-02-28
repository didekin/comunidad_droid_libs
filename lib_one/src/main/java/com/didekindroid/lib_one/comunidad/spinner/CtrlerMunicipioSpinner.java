package com.didekindroid.lib_one.comunidad.spinner;

import com.didekindroid.lib_one.api.CtrlerSelectList;
import com.didekindroid.lib_one.comunidad.repository.ComunidadDbHelper;
import com.didekindroid.lib_one.security.IdentityCacherIf;
import com.didekinlib.model.comunidad.Municipio;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 05/05/17
 * Time: 16:32
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class CtrlerMunicipioSpinner extends CtrlerSelectList<Municipio> {

    public CtrlerMunicipioSpinner()
    {
        super();
    }

    public CtrlerMunicipioSpinner(IdentityCacherIf identityCacher)
    {
        super(identityCacher);
    }

    // .................................... OBSERVABLE .......................................

    Single<List<Municipio>> municipiosByProvincia(final short provinciaId)
    {
        Timber.d("municipiosByProvincia()");
        return Single.fromCallable((() -> {
            ComunidadDbHelper dbHelper = new ComunidadDbHelper(getTkCacher().getContext());
            List<Municipio> municipios = dbHelper.getMunicipioByProvincia(provinciaId);
            dbHelper.close();
            return municipios;
        }));
    }

    @Override
    public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<Municipio>> observer, Long... entityId)
    {
        Timber.d("loadItemsByEntitiyId()");
        assertTrue(entityId.length > 0, "length should be greater than zero");
        return getSubscriptions().add(municipiosByProvincia(entityId[0].shortValue())
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(observer)
        );
    }
}
