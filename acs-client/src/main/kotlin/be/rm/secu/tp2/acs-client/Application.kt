package be.rm.secu.tp2.`acs-client`

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import be.rm.secu.tp2.core.io.IO


object Application

fun main() = application {
    val certificate = Application::class.java.getResourceAsStream("/ca.crt").use { inputStream ->
        inputStream?.let { IO.readCertificate(it) } ?: throw Exception("Could not load CA certificate")
    }

    val acsClient = AcsClient(
        "acs.tp2.secu.rm.be",
        27998,
        certificate
    )

    val acsClientViewModel = AcsClientViewModel(acsClient)
    val acsClientView = AcsClientView(acsClientViewModel)
    Window(onCloseRequest = ::exitApplication) {
        acsClientView.content()
    }
}
