package RecyclerViewHelper

import Modelo.ClaseConexion
import Modelo.detalles_pa_enfermos
import Modelo.pa_enfermos
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import katherine.fabiola.formativoproyecto.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.ResultSet
import java.sql.SQLException
import java.util.Calendar


class Adaptador(private var Datos: List<pa_enfermos>) : RecyclerView.Adapter<ViewHolder>() {

    fun ActuLista(newLista: List<pa_enfermos>) {
        Datos = newLista
        notifyDataSetChanged()
    }

    fun actuPantalla(
        id_pa_enfermos: Int,
        newEdad: Int,
        newEnfermedad: String,
        newHab: Int,
        newCama: Int,
        newFecha: String
    ) {
        val index = Datos.indexOfFirst { it.id_pa_enfermos == id_pa_enfermos }
        if (index != -1) {
            Datos[index].edad = newEdad
            Datos[index].enfermo_enfermedad = newEnfermedad
            Datos[index].numero_hab = newHab
            Datos[index].cama_numero = newCama
            Datos[index].fecha = newFecha
            notifyItemChanged(index)
        }
    }

    fun borarD(context: Context, id_pa_enfermos: Int, position: Int) {
        val dLista = Datos.toMutableList()
        dLista.removeAt(position)

        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()
            if(objConexion != null) {
                try {
                    objConexion.autoCommit = false

                    val borrarDarM = objConexion.prepareStatement("DELETE FROM asignacion_Medicamentos WHERE id_pa_enfermos = ?")!!
                    borrarDarM.setInt(1, id_pa_enfermos)
                    val darMEliminado = borrarDarM.executeUpdate()
                    Log.d("borrarD", "Asignación eliminado: $darMEliminado")

                    val borrarEnfermo =
                        objConexion.prepareStatement("delete from pa_Enfermos where id_pa_enfermos = ?")!!
                    borrarEnfermo.setInt(1, id_pa_enfermos)
                    val enfermoEliminado = borrarEnfermo.executeUpdate()
                    Log.d("borrarD", "Enfermo eliminado: $enfermoEliminado")

                    objConexion.commit()
                    Log.d("borrarD", "commit hecho")

                    withContext(Dispatchers.Main) {
                        Datos = dLista.toList()
                        notifyItemRemoved(position)
                        notifyDataSetChanged()
                        Toast.makeText(
                            context,
                            "Enfermo a sido borrado",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } catch (e: SQLException) {
                    Log.e("dLista", "Error al ejecutar operación SQL", e)
                    try {
                        objConexion.rollback()
                        Log.d("dLista", "Rollback hecho")
                    }catch (rollbackEx: SQLException){
                        Log.e("dLista", "Error al hacer la accion", rollbackEx)
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error al eliminar al enfermo: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: Exception){
                    Log.e("dLista", "Error inesperado", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error desconocido: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    try {
                        objConexion.close()
                        Log.d("borrarD", "Conexión cerrada exitosamente")
                    }catch (closeEx: SQLException){
                        Log.e("borrarD", "Error al cerrar la conexión", closeEx)
                    }
                }
            }else{
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error en la conexión a la base de datos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun updateData(context: Context, newEdad: Int, newEnfermedad: String, newHab: Int, newCama: Int, newFecha: String, id_pa_enfermeros: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()
            if(objConexion != null) {
                try {
                    val actuEnfermo =
                        objConexion.prepareStatement("update pa_Enfermos set edad = ?, enfermo_enfermedad = ?, numero_hab = ?, cama_numero = ?, fecha = ? where id_pa_enfermos = ?")!!
                    actuEnfermo.setInt(1, newEdad)
                    actuEnfermo.setString(2, newEnfermedad)
                    actuEnfermo.setInt(3, newHab)
                    actuEnfermo.setInt(4, newCama)
                    actuEnfermo.setString(5, newFecha)
                    actuEnfermo.setInt(6, id_pa_enfermeros)
                    actuEnfermo.executeUpdate()

                    withContext(Dispatchers.Main) {
                        actuPantalla(
                            id_pa_enfermeros,
                            newEdad,
                            newEnfermedad,
                            newHab,
                            newCama,
                            newFecha
                        )
                        Toast.makeText(
                            context,
                            "Enfermo actualizado",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Falla al actualizar, intentar de nuevo", Toast.LENGTH_SHORT)
                            .show()
                    }
                    e.printStackTrace()
                }finally {
                    objConexion.close()
                }
            }else{
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error en la conexión a la base de datos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_card_pa_enfermos, parent, false)
        return ViewHolder(vista)
    }
    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = Datos[position]
        holder.txtEnfermoNombre.text = item.nombre_enfermo
        holder.txtEnfermoApellido.text = item.apellido_enfermo

        holder.btnDeleteEnfermo.setOnClickListener {
            val context = holder.itemView.context

            val builder = androidx.appcompat.app.AlertDialog.Builder(context)
            builder.setTitle("Eliminar")
            builder.setMessage("Quieres realmente borrar el Enfermo?")

            builder.setPositiveButton("Si"){dialog, which ->
                borarD(context, item.id_pa_enfermos, position)
            }

            builder.setNeutralButton("No"){dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()

        }

        holder.btnEditarEnfermo.setOnClickListener {
            val context = holder.itemView.context

            val dialogView =
                LayoutInflater.from(context).inflate(R.layout.activity_editar_pa_enfermos, null)
            val editEdad = dialogView.findViewById<EditText>(R.id.txtEEdadEd)
            val editEnfermedad = dialogView.findViewById<EditText>(R.id.txtEEnfermedadesEd)
            val editHab = dialogView.findViewById<EditText>(R.id.txtNumHabEd)
            val editCama = dialogView.findViewById<EditText>(R.id.txtCamaEd)
            val editFecha = dialogView.findViewById<EditText>(R.id.txtFechaIEd)

            editFecha.setOnClickListener {
                val calendario = Calendar.getInstance()
                val anio = calendario.get(Calendar.YEAR)
                val mes = calendario.get(Calendar.MONTH)
                val dia = calendario.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(
                    context,
                    { view, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
                        val calendarioSeleccionado = Calendar.getInstance()
                        calendarioSeleccionado.set(
                            anioSeleccionado,
                            mesSeleccionado,
                            diaSeleccionado
                        )
                        val fechaSeleccionada =
                            "$diaSeleccionado/${mesSeleccionado + 1}/$anioSeleccionado"
                        editFecha.setText(fechaSeleccionada)
                    },
                    anio, mes, dia
                )
                datePickerDialog.show()
            }

            editEdad.setHint(item.edad.toString())
            editEnfermedad.setHint(item.enfermo_enfermedad)
            editHab.setHint(item.numero_hab.toString())
            editCama.setHint(item.cama_numero.toString())
            editFecha.setHint(item.fecha)

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Actualizar al Enfermo")
            builder.setView(dialogView)
            builder.setPositiveButton("Actualizar"){dialog, _ ->
                val nuevaEdad = editEdad.text.toString().toIntOrNull() ?: item.edad
                val nuevaEnfermedad = editEnfermedad.text.toString()
                val nuevaHab = editHab.text.toString().toIntOrNull() ?: item.numero_hab
                val nuevaCama = editCama.text.toString().toIntOrNull() ?: item.cama_numero
                val nuevaFecha = editFecha.text.toString()

                updateData(context, nuevaEdad, nuevaEnfermedad, nuevaHab, nuevaCama, nuevaFecha, item.id_pa_enfermos)

                dialog.dismiss()
            }

            builder.setNegativeButton("Atras") {dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }

        holder.itemView.setOnClickListener {
            mostrarDialog(holder.itemView.context, item.id_pa_enfermos)
        }
    }

    private fun mostrarDialog(context: Context, id_pa_enfermos: Int) {
        val builder = AlertDialog.Builder(context)
        val dialogLayout =
            LayoutInflater.from(context).inflate(R.layout.activity_detalles_pa_enfermos, null)
        builder.setView(dialogLayout)

        val alertDialog = builder.create()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val enfermosD = obtenerDetallesEnfermo(id_pa_enfermos)

                withContext(Dispatchers.Main) {
                    dialogLayout.findViewById<TextView>(R.id.txtEnfermoNomD)?.text =
                        enfermosD.nombre_enfermo
                    dialogLayout.findViewById<TextView>(R.id.txtEnfermoApellidoD)?.text =
                        enfermosD.apellido_enfermo
                    dialogLayout.findViewById<TextView>(R.id.txtEnfermoEdadD)?.text =
                        enfermosD.edad.toString()
                    dialogLayout.findViewById<TextView>(R.id.txtEnfermoEnferD)?.text =
                        enfermosD.enfermo_enfermedad
                    dialogLayout.findViewById<TextView>(R.id.txtNumHabD)?.text =
                        enfermosD.numero_hab.toString()
                    dialogLayout.findViewById<TextView>(R.id.txtCamaD)?.text =
                        enfermosD.cama_numero.toString()
                    dialogLayout.findViewById<TextView>(R.id.txtFechaID)?.text =
                        enfermosD.fecha
                    dialogLayout.findViewById<TextView>(R.id.txtMedD)?.text =
                        enfermosD.medicamentos_nombre
                    dialogLayout.findViewById<TextView>(R.id.txtAsigHoraD)?.text =
                        enfermosD.asignacion_hora

                    alertDialog.show()
                }

            } catch (e: Exception) {
                Log.e("AdaptadorPacientes", "No se puede mostrar el dialog", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Debes asignar un medicamento para mostrarlos",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

        private fun obtenerDetallesEnfermo(id_pa_enfermos: Int): detalles_pa_enfermos {
            val objConexion = ClaseConexion().cadenaConexion()
            if (objConexion == null) {
                throw IllegalStateException("La conexión a la base de datos no es posible")
            }

            val statement = objConexion.createStatement()
            val query = """
            SELECT
    pe.nombre_enfermo AS nombre,
    pe.apellido_enfermo AS apellido,
    pe.edad AS edad,
    pe.enfermo_enfermedad AS enfermedad,
    pe.numero_hab AS habitacion,
    pe.cama_numero AS cama,
    pe.fecha AS fecha,
    m.medicamentos_nombre AS medicamentos,
    ah.asignacion_hora
FROM
         asignacion_Medicamentos ah
    INNER JOIN pa_Enfermos pe ON ah.id_pa_enfermos = pe.id_pa_enfermos
    INNER JOIN medicamentos m ON ah.id_medicamentos = m.id_medicamentos;
    WHERE pe.id_pa_enfermos = $id_pa_enfermos
        """.trimIndent()

            val resultSet: ResultSet = statement.executeQuery(query)
            if (!resultSet.next()) {
                throw IllegalStateException("No se encontraron detalles para el enfermo")
            }

            val detalles_pa_enfermos = detalles_pa_enfermos(
                nombre_enfermo = resultSet.getString("nombre"),
                apellido_enfermo = resultSet.getString("apellido"),
                edad = resultSet.getInt("edad"),
                enfermo_enfermedad = resultSet.getString("enfermedad"),
                numero_hab = resultSet.getInt("habitacion"),
                cama_numero = resultSet.getInt("cama"),
                fecha = resultSet.getString("fecha"),
                medicamentos_nombre = resultSet.getString("medicamentos"),
                asignacion_hora = resultSet.getString("asignacion_hora")
            )

            resultSet.close()
            statement.close()
            objConexion.close()

            return detalles_pa_enfermos
        }
    }
