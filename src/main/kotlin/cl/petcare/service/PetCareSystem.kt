package cl.petcare.service

import cl.petcare.model.Box
import cl.petcare.model.BoxState
import cl.petcare.model.OwnerType
import cl.petcare.model.Patient
import cl.petcare.model.PatientType
import cl.petcare.model.Ticket
import kotlinx.coroutines.delay

class PetCareSystem {
    private val boxes = MutableList(10) { Box(it + 1) }
    private val activePatients = mutableMapOf<String, Patient>()
    private val tickets = mutableListOf<Ticket>()
    private var ticketNumber = 1

    suspend fun registerEntry(patient: Patient) {
        try {
            PatientValidator.validate(patient)
            if (activePatients.containsKey(patient.code)) {
                throw Exception("Ya existe un paciente con ese código.")
            }
            val box = boxes.firstOrNull { it.state == BoxState.LIBRE }
                ?: throw Exception("No hay boxes disponibles.")

            box.state = BoxState.EN_PROCESO
            box.reason = "Registrando entrada"
            delay(3_000)
            box.state = BoxState.EN_ATENCION
            box.patientCode = patient.code
            activePatients[patient.code] = patient
            println("Entrada registrada en box ${box.number}.")
        } catch (error: Exception) {
            println("Error de entrada: ${error.message}")
        }
    }

    suspend fun registerExit(code: String, minutes: Long) {
        try {
            val box = boxes.firstOrNull {
                it.state == BoxState.EN_ATENCION && it.patientCode == code
            } ?: throw Exception("Paciente no encontrado.")
            val patient = activePatients[code] ?: throw Exception("Paciente no encontrado.")

            box.state = BoxState.EN_PROCESO
            box.reason = "Calculando tarifa"
            delay(6_500)

            val amount = TariffCalculator.calculate(patient, minutes)
            if (amount <= 0) throw Exception("La tarifa calculada no es válida.")

            val ticket = Ticket(ticketNumber, patient, minutes, amount)
            ticketNumber++
            tickets.add(ticket)
            activePatients.remove(code)
            box.state = BoxState.LIBRE
            box.patientCode = null
            box.reason = ""
            println("Salida registrada. Ticket #${ticket.number}.")
        } catch (error: Exception) {
            println("Error de salida: ${error.message}")
            for (box in boxes) {
                val patientCode = box.patientCode
                if (box.state == BoxState.EN_PROCESO && patientCode != null && activePatients.containsKey(patientCode)) {
                    box.state = BoxState.EN_ATENCION
                    box.reason = ""
                }
            }
        }
    }

    fun availableBoxes(): Int = boxes.count { it.state == BoxState.LIBRE }

    fun agreementPatients(): List<Patient> =
        tickets.map { it.patient }.filter { it.ownerType == OwnerType.CONVENIO }

    fun averageIncome(): Double =
        if (tickets.isEmpty()) 0.0 else tickets.map { it.amountPaid }.average()

    fun finishedPatientCodes(): List<String> = tickets.map { it.patient.code }

    fun patientWithLongestUsage(): Patient? =
        tickets.maxByOrNull { it.usageMinutes }?.patient

    fun printClosingReport() {
        println("\n===== REPORTE DE CIERRE =====")
        for (ticket in tickets) {
            println(
                "Ticket #${ticket.number}: ${ticket.patient.type}, " +
                    "${ticket.patient.code}, ${ticket.usageMinutes} minutos, " +
                    "\$${ticket.amountPaid}"
            )
        }

        val total = tickets.sumOf { it.amountPaid }
        val incomeByType = PatientType.entries.associateWith { type ->
            tickets.filter { it.patient.type == type }.sumOf { it.amountPaid }
        }
        val topType = incomeByType.maxByOrNull { it.value }?.key?.name ?: "Ninguno"

        println("Total recaudado: \$$total")
        println("Pacientes atendidos: ${tickets.size}")
        println("Ingreso promedio: \$${"%.2f".format(averageIncome())}")
        println("Tipo con más ingresos: $topType")
        println("Boxes disponibles: ${availableBoxes()}")
    }
}
