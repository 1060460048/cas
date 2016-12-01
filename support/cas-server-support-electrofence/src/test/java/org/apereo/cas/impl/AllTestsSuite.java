package org.apereo.cas.impl;

import org.apereo.cas.impl.calcs.DateTimeAuthenticationRequestRiskCalculatorTests;
import org.apereo.cas.impl.calcs.GeoLocationAuthenticationRequestRiskCalculatorTests;
import org.apereo.cas.impl.calcs.IpAddressAuthenticationRequestRiskCalculatorTests;
import org.apereo.cas.impl.calcs.UserAgentAuthenticationRequestRiskCalculatorTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({DateTimeAuthenticationRequestRiskCalculatorTests.class,
        GeoLocationAuthenticationRequestRiskCalculatorTests.class,
        IpAddressAuthenticationRequestRiskCalculatorTests.class,
        UserAgentAuthenticationRequestRiskCalculatorTests.class})
/**
 * Test suite that runs all test in a batch.
 * @author Misagh Moayyed
 * @since 5.1.0
 */
public class AllTestsSuite {
}
