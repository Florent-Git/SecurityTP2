package be.rm.secu.tp2.core.net

interface IRequestHandler<T, R> {
    fun handleRequest(request: T): R
}