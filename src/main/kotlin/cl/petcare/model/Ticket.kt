package cl.petcare.model

data class Ticket(
    val number: Int,
    val patient: Patient,
    val usageMinutes: Long,
    val amountPaid: Long
)
