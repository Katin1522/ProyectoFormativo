package Modelo

data class detalles_pa_enfermos(
    var nombre_enfermo: String,
    var apellido_enfermo: String,
    var edad: Int,
    var enfermo_enfermedad: String,
    var numero_hab: Int,
    var cama_numero: Int,
    var fecha: String,
    val medicamentos_nombre: String,
    val asignacion_hora: String
)
