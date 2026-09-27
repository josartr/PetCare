package cl.petcare

import cl.petcare.model.OwnerType
import cl.petcare.model.Patient
import cl.petcare.model.PatientType
import cl.petcare.service.PetCareSystem
import java.time.LocalDateTime
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val system = PetCareSystem()
    val admissionTime = LocalDateTime.now()
    val patients = listOf(
        Patient("CA12CD", "Max", "Golden Retriever", PatientType.CANINO, OwnerType.CONVENIO, admissionTime),
        Patient("CA99ZA", "Luna", "Labrador", PatientType.CANINO, OwnerType.PARTICULAR, admissionTime),
        Patient("FE22TO", "Misi", "Siamés", PatientType.FELINO, OwnerType.PARTICULAR, admissionTime),
        Patient("EX44RG", "Loro", "Amazónico", PatientType.EXOTICO, OwnerType.MUNICIPAL, admissionTime, true),
        Patient("EX77RG", "Iguana", "Verde", PatientType.EXOTICO, OwnerType.PARTICULAR, admissionTime, false)
    )

    for (patient in patients) {
        system.registerEntry(patient)
    }

    system.registerEntry(patients.first().copy(code = "123ABC"))

    listOf(
        "CA12CD" to 75L,
        "CA99ZA" to 180L,
        "FE22TO" to 18L,
        "EX44RG" to 120L,
        "EX77RG" to 45L
    ).forEach { (code, minutes) ->
        system.registerExit(code, minutes)
    }

    system.registerExit("ZZ00ZZ", 30)
    system.printClosingReport()
}
