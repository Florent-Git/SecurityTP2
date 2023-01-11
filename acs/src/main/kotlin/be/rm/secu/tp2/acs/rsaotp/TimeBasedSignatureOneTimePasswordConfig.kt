package be.rm.secu.tp2.acs.rsaotp

import java.util.concurrent.TimeUnit

class TimeBasedSignatureOneTimePasswordConfig(
    val timeStep: Long,
    val timeStepUnit: TimeUnit,
    codeDigits: Int,
    signatureAlgorithm: SignatureAlgorithm,
): SignatureOneTimePasswordConfig(codeDigits, signatureAlgorithm) {
    init {
        require(timeStep >= 0) { "Time step must be positive" }
    }
}
