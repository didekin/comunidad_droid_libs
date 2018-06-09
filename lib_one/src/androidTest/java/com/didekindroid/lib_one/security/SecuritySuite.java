package com.didekindroid.lib_one.security;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 18/02/17
 * Time: 12:33
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        JceTests.class,
        SecInitializerTest.class,
        AuthTkCacherTest.class,
})
public class SecuritySuite {
}
