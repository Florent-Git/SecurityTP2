package be.rm.secu.tp2.`acs-client`

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AcsClientViewModel(
    private val acsClient: AcsClient
) {
    val token = mutableStateOf("")

    fun sendToken() = runBlocking {
        launch(Dispatchers.IO) {
            acsClient.sendToken(token.value)
        }
    }
}
