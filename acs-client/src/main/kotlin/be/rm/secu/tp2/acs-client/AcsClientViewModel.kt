package be.rm.secu.tp2.`acs-client`

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class AcsClientViewModel(
    private val acsClient: AcsClient
) {
    val token = mutableStateOf("")
    val response = mutableStateOf("")

    fun sendToken() = runBlocking {
        launch(Dispatchers.IO) {
            response.value = acsClient.sendToken(token.value)
        }
    }

    fun copyToClipboard() {
        Toolkit.getDefaultToolkit()
            .systemClipboard
            .setContents(StringSelection(response.value), null)
    }
}
