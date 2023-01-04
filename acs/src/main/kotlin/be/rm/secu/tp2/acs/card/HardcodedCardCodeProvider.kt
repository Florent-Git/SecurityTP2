package be.rm.secu.tp2.acs.card

object HardcodedCardCodeProvider: ICardCodeProvider {
    private val codes = listOf("1234", "5678", "9012")

    override fun hasCode(code: String): Boolean {
        return codes.contains(code)
    }
}