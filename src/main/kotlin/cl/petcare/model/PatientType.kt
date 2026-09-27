package cl.petcare.model

enum class PatientType(val baseRatePerHour: Long) {
    CANINO(12_000),
    FELINO(9_000),
    EXOTICO(20_000)
}
