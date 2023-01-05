package be.rm.secu.tp2.acs.rsaotp

open class SignatureOneTimePasswordConfig(
    val codeDigits: Int = 6,
    val signatureAlgorithm: SignatureAlgorithm
) {
    init {
        require(codeDigits in 6..10) { "Code digits must be between 6 and 10" }
    }
}
