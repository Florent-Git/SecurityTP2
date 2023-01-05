package be.rm.secu.tp2.acs.rsaotp

import org.junit.Before
import org.junit.Test
import java.security.KeyPairGenerator
import kotlin.test.assertTrue

class SignatureOneTimePasswordGeneratorTest {
    private lateinit var signatureOneTimePasswordGenerator: SignatureOneTimePasswordGenerator
    private val keyPair by lazy {
        KeyPairGenerator.getInstance("RSA").apply {
            initialize(2048)
        }.generateKeyPair()
    }

    @Before
    fun setUp() {
        signatureOneTimePasswordGenerator = SignatureOneTimePasswordGenerator(
            keyPair.private,
            SignatureOneTimePasswordConfig(
                6,
                SignatureAlgorithm.SHA256
            )
        )
    }

    @Test
    fun `Given a private key, when the code is generated with the same counter, then the code is valid`() {
        val code = signatureOneTimePasswordGenerator.generate(1)

        assertTrue(signatureOneTimePasswordGenerator.isValid(code, 1))
    }

    @Test
    fun `Given a private key, when the code is generated without the same counter, then the code is invalid`() {
        val code = signatureOneTimePasswordGenerator.generate(1)

        assertTrue(!signatureOneTimePasswordGenerator.isValid(code, 2))
    }
}