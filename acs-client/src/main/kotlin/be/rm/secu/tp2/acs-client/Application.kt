package be.rm.secu.tp2.`acs-client`

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application


object Application

fun main() = application {
    val acsClient = AcsClient()
    val acsClientViewModel = AcsClientViewModel(acsClient)
    val acsClientView = AcsClientView(acsClientViewModel)
    Window(onCloseRequest = ::exitApplication) {
        acsClientView.content()
    }
}
