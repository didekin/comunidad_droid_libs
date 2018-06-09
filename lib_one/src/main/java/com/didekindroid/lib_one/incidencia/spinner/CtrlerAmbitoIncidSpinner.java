package com.didekindroid.lib_one.incidencia.spinner;

import com.didekindroid.lib_one.api.CtrlerSelectList;
import com.didekindroid.lib_one.incidencia.IncidenciaDataDbHelper;
import com.didekindroid.lib_one.security.AuthTkCacherIf;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 30/03/17
 * Time: 13:20
 */
public class CtrlerAmbitoIncidSpinner extends CtrlerSelectList<AmbitoIncidValueObj> {

    public CtrlerAmbitoIncidSpinner()
    {
        super();
    }

    public CtrlerAmbitoIncidSpinner(AuthTkCacherIf identityCacher)
    {
        super(identityCacher);
    }

    // .................................... OBSERVABLE .......................................

    Single<List<AmbitoIncidValueObj>> ambitoIncidList()
    {

        Timber.d("ambitoIncidList()");
        return Single.fromCallable(() -> {
            IncidenciaDataDbHelper dbHelper = new IncidenciaDataDbHelper(getTkCacher().getContext());
            List<AmbitoIncidValueObj> list = dbHelper.getAmbitoIncidList();
            dbHelper.close();
            return list;
        });
    }

    // .................................... INSTANCE METHODS .....................................

    @Override
    public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<AmbitoIncidValueObj>> observer, Long... entityId)
    {
        Timber.d("loadItemsByEntitiyId()");
        return getSubscriptions().add(ambitoIncidList()
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(observer)
        );
    }
}
