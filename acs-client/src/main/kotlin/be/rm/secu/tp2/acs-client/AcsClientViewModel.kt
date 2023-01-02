package be.rm.secu.tp2.`acs-client`

import androidx.compose.runtime.mutableStateOf

class AcsClientViewModel(acsClient: AcsClient) {
    val token = mutableStateOf("")

    fun sendToken() {
        println("Sending token: ${token.value}")
        // acsClient.sendToken(token.value)
    }
}
