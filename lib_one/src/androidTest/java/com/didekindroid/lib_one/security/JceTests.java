package com.didekindroid.lib_one.security;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.Provider;

import timber.log.Timber;

import static java.security.KeyStore.getDefaultType;
import static java.security.Security.getProviders;
import static java.util.Arrays.binarySearch;
import static java.util.Arrays.sort;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 06/04/16
 * Time: 20:15
 */
@RunWith(AndroidJUnit4.class)
public class JceTests {

    @Before
    public void setUp(){
        Timber.plant(new Timber.DebugTree());
    }

    @Test
    public void testProviders_1()
    {
        Provider[] providers = getProviders();
        String[] providerNames = new String[providers.length];
        for (int i = 0; i < providerNames.length; ++i) {
            providerNames[i] = providers[i].getName();
            Timber.d("=============%s%n", providerNames[i]);
        }
        sort(providerNames);
        assertThat(binarySearch(providerNames, "BC") >= 0, is(true));
    }

    @Test
    public void testProviders_2()
    {
        Provider[] providers = getProviders();
        for (Provider provider : providers) {
            if (provider.getName().equals("BC")) {
                Timber.d("=============%s%n", provider.getServices().toString());
            }
        }
    }

    @Test
    public void testKeyStore()
    {
        String keyStoreType = getDefaultType();
        assertThat(keyStoreType, is("BKS"));
    }
}
