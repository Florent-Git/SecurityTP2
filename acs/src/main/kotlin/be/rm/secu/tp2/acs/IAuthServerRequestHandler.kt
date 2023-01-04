package be.rm.secu.tp2.acs

interface IAuthServerRequestHandler {
    fun handleRequest(request: String): String
}