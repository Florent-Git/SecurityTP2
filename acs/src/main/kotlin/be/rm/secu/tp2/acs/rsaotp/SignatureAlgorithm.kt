package be.rm.secu.tp2.acs.rsaotp

enum class SignatureAlgorithm(
    val algorithm: String,
    val hashBytes: Int
) {
    /**
     * SHA1 HMAC with a hash of 20-bytes
     */
    SHA1("SHA1withRSA", 20),
    /**
     * SHA256 HMAC with a hash of 32-bytes
     */
    SHA256("SHA256withRSA", 32),
    /**
     * SHA512 HMAC with a hash of 64-bytes
     */
    SHA512("SHA512withRSA", 64)
}