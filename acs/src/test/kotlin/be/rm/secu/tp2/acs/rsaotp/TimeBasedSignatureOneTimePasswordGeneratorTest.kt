package be.rm.secu.tp2.acs.rsaotp

import org.junit.Before
import org.junit.Test
import java.security.KeyPairGenerator
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

class TimeBasedSignatureOneTimePasswordGeneratorTest {
    private lateinit var signatureOneTimePasswordGenerator: TimeBasedSignatureOneTimePasswordGenerator
    private val keyPair by lazy {
        KeyPairGenerator.getInstance("RSA").apply {
            initialize(2048)
        }.generateKeyPair()
    }

    @Before
    fun setUp() {
        signatureOneTimePasswordGenerator = TimeBasedSignatureOneTimePasswordGenerator(
            keyPair.private,
            TimeBasedSignatureOneTimePasswordConfig(
                3L,
                TimeUnit.SECONDS,
                6,
                SignatureAlgorithm.SHA256
            )
        )
    }

    @Test
    fun `Given a private key, when the code is generated with the same counter, then the code is valid`() {
        val code = signatureOneTimePasswordGenerator.generate()

        assertTrue(signatureOneTimePasswordGenerator.isValid(code))
    }

    @Test
    fun `Given a private key, when the code is generated without the same counter, then the code is invalid`() {
        val code = signatureOneTimePasswordGenerator.generate()

        Thread.sleep(4000)

        assertTrue(!signatureOneTimePasswordGenerator.isValid(code))
    }
}