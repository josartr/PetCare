package cl.petcare.service

import cl.petcare.model.OwnerType
import cl.petcare.model.Patient

object PatientValidator {
    private val codePattern = Regex("^[A-Z]{2}\\d{2}[A-Z]{2}$")

    fun validate(patient: Patient) {
        require(codePattern.matches(patient.code)) {
            "El código de atención debe tener el formato AA00AA."
        }
        require(patient.name.isNotBlank()) { "El nombre de la mascota no puede estar vacío." }
        require(patient.species.isNotBlank()) { "La especie no puede estar vacía." }
        require(patient.ownerType in OwnerType.entries) { "El tipo de dueño no es válido." }
    }
}
