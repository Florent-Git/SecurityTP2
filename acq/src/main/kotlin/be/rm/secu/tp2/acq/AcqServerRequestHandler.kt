package be.rm.secu.tp2.acq

import be.rm.secu.tp2.core.net.BasicClient
import be.rm.secu.tp2.core.net.IRequestHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class AcqServerRequestHandler(
    private val client: BasicClient
) : IRequestHandler<String, String> {
    override fun handleRequest(request: String): String {
        return runBlocking {
            withContext(Dispatchers.IO) {
                client.sendRequest(request)
            }
        }
    }
}
