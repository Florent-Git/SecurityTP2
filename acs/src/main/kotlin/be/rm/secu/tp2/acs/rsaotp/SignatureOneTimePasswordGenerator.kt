package be.rm.secu.tp2.acs.rsaotp

import java.nio.ByteBuffer
import java.security.PrivateKey
import java.security.Signature
import kotlin.experimental.and
import kotlin.math.pow

/**
 * Code inspired by dev.turingcomplete.kotlinonetimepassword
 * (https://github.com/marcelkliemannel/kotlin-onetimepassword)
 */
class SignatureOneTimePasswordGenerator(
    private val privateKey: PrivateKey,
    private val config: SignatureOneTimePasswordConfig
) {
    fun generate(counter: Long): String {
        if (config.codeDigits <= 0) {
            return ""
        }

        // The counter value is the input parameter 'message' to the HMAC algorithm.
        // It must be  represented by a byte array with the length of a long (8 byte).
        val message: ByteBuffer = ByteBuffer.allocate(8).putLong(0, counter)

        // Compute the HMAC hash with the algorithm, 'secret' and 'message' as input parameter.
        val hash = Signature.getInstance("SHA256withRSA").run {
            initSign(privateKey)
            update(message)
            sign()
        }

        // The value of the offset is the lower 4 bits of the last byte of the hash
        // (0x0F = 0000 1111).
        val offset = hash.last().and(0x0F).toInt()

        // The first step for extracting the binary value is to collect the next four
        // bytes from the hash, starting at the index of the offset.
        val binary = ByteBuffer.allocate(4).apply {
            for (i in 0..3) {
                put(i, hash[i + offset])
            }
        }

        // The second step is to drop the most significant bit (MSB) from the first
        // step binary value (0x7F = 0111 1111).
        binary.put(0, binary.get(0).and(0x7F))

        // The resulting integer value of the code must have at most the required code
        // digits. Therefore, the binary value is reduced by calculating the modulo
        // 10 ^ codeDigits.
        val codeInt = binary.int.rem(10.0.pow(config.codeDigits).toInt())

        // The integer code variable may contain a value with fewer digits than the
        // required code digits. Therefore, the final code value is filled with zeros
        // on the left, till the code digits requirement is fulfilled.
        return codeInt.toString().padStart(config.codeDigits, '0')
    }

    fun isValid(code: String, counter: Long): Boolean {
        return code == generate(counter)
    }
}