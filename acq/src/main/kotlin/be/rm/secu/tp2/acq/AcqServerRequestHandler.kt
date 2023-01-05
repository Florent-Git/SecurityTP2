package be.rm.secu.tp2.acq

import be.rm.secu.tp2.core.net.BasicClient
import be.rm.secu.tp2.core.net.IRequestHandler
import kotlinx.coroutines.runBlocking

class AcqServerRequestHandler(
    private val client: BasicClient
) : IRequestHandler<String, String> {
    override fun handleRequest(request: String): String {
        return runBlocking {
            client.sendRequest(request)
        }
    }
}
