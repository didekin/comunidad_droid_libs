package com.didekindroid.lib_one.security;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.KeyStore;
import java.security.Provider;
import java.util.Arrays;

import static java.security.Security.getProviders;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 06/04/16
 * Time: 20:15
 */
@RunWith(AndroidJUnit4.class)
public class JceTests {

    @Test
    public void testProviders_2()
    {
        Provider[] providers = getProviders();
        String[] providerNames = new String[providers.length];
        for (int i = 0; i < providerNames.length; ++i) {
            providerNames[i] = providers[i].getName();
        }
        Arrays.sort(providerNames);
        assertThat(Arrays.binarySearch(providerNames, "BC") >= 0, is(true));
    }

    @Test
    public void testKeyStore()
    {
        String keyStoreType = KeyStore.getDefaultType();
        assertThat(keyStoreType, is("BKS"));
    }
}
