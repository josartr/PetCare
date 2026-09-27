package cl.petcare.service

import cl.petcare.model.OwnerType
import cl.petcare.model.Patient
import cl.petcare.model.PatientType
import kotlin.math.roundToLong

object TariffCalculator {
    private const val VAT_RATE = 0.19
    private const val AGREEMENT_DISCOUNT = 0.80
    private const val MUNICIPAL_DISCOUNT = 0.50
    private const val WILD_SURCHARGE = 1.30

    fun calculate(patient: Patient, usageMinutes: Long): Long {
        require(usageMinutes >= 0) { "El tiempo de atención no puede ser negativo." }

        val hours = usageMinutes / 60.0
        var amountBeforeVat = when (patient.type) {
            PatientType.CANINO -> {
                val agreementFactor =
                    if (patient.ownerType == OwnerType.CONVENIO) AGREEMENT_DISCOUNT else 1.0
                patient.type.baseRatePerHour * hours * agreementFactor
            }
            PatientType.FELINO -> {
                if (usageMinutes < 20) 0.0 else patient.type.baseRatePerHour * hours
            }
            PatientType.EXOTICO -> {
                val wildFactor = if (patient.isWild) WILD_SURCHARGE else 1.0
                patient.type.baseRatePerHour * hours * wildFactor
            }
        }

        if (amountBeforeVat <= 0.0) {
            return 0
        }

        var total = amountBeforeVat * (1 + VAT_RATE)
        if (patient.ownerType == OwnerType.MUNICIPAL) {
            total *= MUNICIPAL_DISCOUNT
        }
        return total.roundToLong()
    }
}
