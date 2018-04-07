package com.didekindroid.lib_one.util;


import com.didekindroid.lib_one.R;

import org.junit.Test;

import java.util.List;

import static android.support.test.InstrumentationRegistry.getContext;
import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekindroid.lib_one.util.IoHelper.doArrayFromFile;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 19/06/15
 * Time: 20:33
 */
public class IoHelperTest {

    private static final int TIPO_VIA_FILE_SIZE = 323;

    @Test
    public void testDoArrayFromFile()
    {
        List<String> tipos = doArrayFromFile(getContext(), R.raw.tipos_vias);
        assertThat(tipos.size(),is(TIPO_VIA_FILE_SIZE));
        assertThat(tipos,hasItems("Acces","Galeria","Zumardi"));
    }
}