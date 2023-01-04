package be.rm.secu.tp2.acs.card

interface ICardCodeProvider {
    fun hasCode(code: String): Boolean
}