package be.rm.secu.tp2.`acs-client`

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

class AcsClientView(private val acsClientViewModel: AcsClientViewModel) {
    @Composable
    fun content() {
        MaterialTheme {
            Row(
                Modifier.fillMaxSize(),
                Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    Modifier.fillMaxSize(),
                    Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = acsClientViewModel.cardNumber.value,
                        onValueChange = { acsClientViewModel.cardNumber.value = it },
                        label = { Text("Card number") },
                        placeholder = { Text("Enter your card number") }
                    )
                    Button(onClick = {
                        runBlocking {
                            coroutineScope {
                                acsClientViewModel.sendToken()
                            }
                        }
                    }) {
                        Text("Send card number")
                    }
                    if (acsClientViewModel.response.value != "") {
                        Text("Response: ")
                        Text(acsClientViewModel.response.value)
                        Text("Valid from: ")
                        Text(acsClientViewModel.startTime.value.toString())
                        Text("Valid until: ")
                        Text(acsClientViewModel.endTime.value.toString())
                        Button(onClick = { acsClientViewModel.copyToClipboard() }) {
                            Text("Copy")
                        }
                    }

                }
            }
        }
    }
}
