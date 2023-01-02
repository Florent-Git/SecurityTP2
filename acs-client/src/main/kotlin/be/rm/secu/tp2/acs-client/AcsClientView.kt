package be.rm.secu.tp2.`acs-client`

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class AcsClientView(private val acsClientViewModel: AcsClientViewModel) {
    @Composable
    fun content() {
        MaterialTheme {
            Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
                TextField(
                    value = acsClientViewModel.token.value,
                    onValueChange = { acsClientViewModel.token.value = it },
                    label = { Text("Token") },
                    placeholder = { Text("Enter your token") }
                )
                Button(onClick = { acsClientViewModel.sendToken() }) {
                    Text("Send token")
                }
            }
        }
    }
}
