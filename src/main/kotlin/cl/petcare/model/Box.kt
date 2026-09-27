package cl.petcare.model

data class Box(
    val number: Int,
    var state: BoxState = BoxState.LIBRE,
    var patientCode: String? = null,
    var reason: String = ""
)
