package cl.petcare.model

import java.time.LocalDateTime

data class Patient(
    val code: String,
    val name: String,
    val species: String,
    val type: PatientType,
    val ownerType: OwnerType,
    val admissionAt: LocalDateTime,
    val isWild: Boolean = false
)
