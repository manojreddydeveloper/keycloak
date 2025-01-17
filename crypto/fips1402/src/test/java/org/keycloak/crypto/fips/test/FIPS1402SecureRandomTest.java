package org.keycloak.crypto.fips.test;

import java.security.SecureRandom;

import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.keycloak.common.crypto.CryptoIntegration;
import org.keycloak.common.util.Environment;
import org.keycloak.rule.CryptoInitRule;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class FIPS1402SecureRandomTest {

    @ClassRule
    public static CryptoInitRule cryptoInitRule = new CryptoInitRule();

    @Before
    public void before() {
        // Run this test just if java is in FIPS mode
        Assume.assumeTrue("Java is not in FIPS mode. Skipping the test.", Environment.isJavaInFipsMode());
    }

    protected static final Logger logger = Logger.getLogger(FIPS1402SecureRandomTest.class);

    @Test
    public void testSecureRandom() throws Exception {
        logger.info(CryptoIntegration.dumpJavaSecurityProviders());

        SecureRandom sc1 = new SecureRandom();
        logger.infof(dumpSecureRandom("new SecureRandom()", sc1));

        SecureRandom sc2 = SecureRandom.getInstance("DEFAULT", "BCFIPS");
        logger.infof(dumpSecureRandom("SecureRandom.getInstance(\"DEFAULT\", \"BCFIPS\")", sc2));
        Assert.assertEquals("DEFAULT", sc2.getAlgorithm());
        Assert.assertEquals("BCFIPS", sc2.getProvider().getName());

        SecureRandom sc3 = SecureRandom.getInstance("SHA1PRNG");
        logger.infof(dumpSecureRandom("SecureRandom.getInstance(\"SHA1PRNG\")", sc3));
        Assert.assertEquals("SHA1PRNG", sc3.getAlgorithm());
        Assert.assertEquals("BCFIPS", sc3.getProvider().getName());
    }


    private String dumpSecureRandom(String prefix, SecureRandom secureRandom) {
        StringBuilder builder = new StringBuilder(prefix + ": algorithm: " + secureRandom.getAlgorithm() + ", provider: " + secureRandom.getProvider() + ", random numbers: ");
        for (int i=0; i < 5; i++) {
            builder.append(secureRandom.nextInt(1000) + ", ");
        }
        return builder.toString();
    }
}
