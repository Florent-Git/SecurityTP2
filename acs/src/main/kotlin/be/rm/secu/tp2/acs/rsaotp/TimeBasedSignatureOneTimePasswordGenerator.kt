package be.rm.secu.tp2.acs.rsaotp

import java.security.PrivateKey
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.floor

class TimeBasedSignatureOneTimePasswordGenerator(
    privateKey: PrivateKey,
    private val config: TimeBasedSignatureOneTimePasswordConfig
) {
    private val signatureOneTimePasswordGenerator = SignatureOneTimePasswordGenerator(privateKey, config)

    fun counter(timestamp: Long = System.currentTimeMillis()): Long {
        if (config.timeStep == 0L) {
            return 0
        }

        return floor(timestamp.toDouble().div(TimeUnit.MILLISECONDS.convert(config.timeStep, config.timeStepUnit))).toLong()
    }

    fun counter(date: Date): Long = counter(date.time)

    fun timeslotStart(counter: Long): Long {
        val timeStepMillis = TimeUnit.MILLISECONDS.convert(config.timeStep, config.timeStepUnit)
        return (counter * timeStepMillis)
    }

    fun generate(timestamp: Long = System.currentTimeMillis()): String =
        signatureOneTimePasswordGenerator.generate(counter(timestamp))

    fun generate(instant: Instant): String = generate(instant.toEpochMilli())

    fun isValid(code: String, timestamp: Long = System.currentTimeMillis()): Boolean =
        signatureOneTimePasswordGenerator.isValid(code, counter(timestamp))

    fun isValid(code: String, instant: Instant): Boolean = isValid(code, instant.toEpochMilli())

    fun isValid(code: String, date: Date): Boolean = isValid(code, date.time)
}