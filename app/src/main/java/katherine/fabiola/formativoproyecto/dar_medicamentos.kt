package katherine.fabiola.formativoproyecto

import Modelo.ClaseConexion
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class dar_medicamentos : AppCompatActivity() {

    private lateinit var txtHoraAsignada: EditText
    private lateinit var spEnfermos: Spinner
    private lateinit var spMed: Spinner
    private lateinit var btnDarMed: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dar_medicamentos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnInicio = findViewById<ImageView>(R.id.btnInicio)
        btnInicio.setOnClickListener {
            val pantallaInicio = Intent(this, listado_pa_enfermos::class.java)
            startActivity(pantallaInicio)
        }

        val btnAtras = findViewById<Button>(R.id.btnAtras)
        btnAtras.setOnClickListener {
            val pantallaAgregarMed = Intent(this, agregar_medicamentos::class.java)
            startActivity(pantallaAgregarMed)
            overridePendingTransition(0, 0)
        }


        txtHoraAsignada = findViewById(R.id.txtHoraAsignada)
        spEnfermos = findViewById(R.id.spEnfermos)
        spMed = findViewById(R.id.spMed)
        btnDarMed = findViewById(R.id.btnDarMed)

        cargarEnfermos()
        cargarMed()

        btnDarMed.setOnClickListener {
            val horaAsignada = txtHoraAsignada.text.toString()
            val enfermosSelect = spEnfermos.selectedItem.toString()
            val medSelect = spMed.selectedItem.toString()

            CoroutineScope(Dispatchers.Main).launch {
                val idEnfermo = darIdEnfermo(enfermosSelect)
                val idMed = darIdMed(medSelect)

                if (idEnfermo != null && idMed != null) {
                    val result =
                        guardarMedSelect(horaAsignada, idEnfermo, idMed)
                    if (result) {
                        Toast.makeText(
                            this@dar_medicamentos,
                            "Se dio el medicamento correctamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        txtHoraAsignada.setText("")
                    } else {
                        Toast.makeText(
                            this@dar_medicamentos,
                            "No se puede dar el medicamento",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@dar_medicamentos,
                        "No se puede hayar los Enfermos/Medicamentos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun cargarEnfermos() {
        CoroutineScope(Dispatchers.IO).launch {
            val pa_enfermos = pa_enfermosBaseDatos()
            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    this@dar_medicamentos,
                    android.R.layout.simple_spinner_item,
                    pa_enfermos
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spEnfermos.adapter = adapter
            }
        }
    }

    private fun cargarMed() {
        CoroutineScope(Dispatchers.IO).launch {
            val med = medBaseDatos()
            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    this@dar_medicamentos,
                    android.R.layout.simple_spinner_item,
                    med
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spMed.adapter = adapter
            }
        }
    }

    private suspend fun pa_enfermosBaseDatos(): List<String> = withContext(Dispatchers.IO) {
        val enfermos = mutableListOf<String>()
        val queryPE = "SELECT nombre_enfermo, apellido_enfermo FROM pa_Enfermos"
        val objConexion = ClaseConexion().cadenaConexion()

        objConexion?.let {
            try {
                val statement = it.createStatement()
                val resultSet = statement.executeQuery(queryPE)

                while (resultSet.next()) {
                    val nombres = resultSet.getString("nombre_enfermo")
                    val apellidos = resultSet.getString("apellido_enfermo")
                    enfermos.add("$nombres $apellidos")
                }

                resultSet.close()
                statement.close()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                it.close()
            }
        }

        enfermos
    }

    private suspend fun medBaseDatos(): List<String> = withContext(Dispatchers.IO) {
        val med = mutableListOf<String>()
        val queryM = "SELECT medicamentos_nombre FROM medicamentos"
        val objConexion = ClaseConexion().cadenaConexion()

        objConexion?.let {
            try {
                val statement = it.createStatement()
                val resultSet = statement.executeQuery(queryM)

                while (resultSet.next()) {
                    val med_nombre = resultSet.getString("medicamentos_nombre")
                    med.add(med_nombre)
                }

                resultSet.close()
                statement.close()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                it.close()
            }
        }

        med
    }

    private suspend fun darIdEnfermo(nombreCom: String): Int? =
        withContext(Dispatchers.IO) {
            val queryDE =
                "SELECT id_pa_enfermos FROM pa_Enfermos WHERE (nombre_enfermo || ' ' || apellido_enfermo) =  ?"
            val objConexion = ClaseConexion().cadenaConexion()

            objConexion?.let {
                try {
                    val statement = it.prepareStatement(queryDE)
                    statement.setString(1, nombreCom)
                    val resultSet = statement.executeQuery()

                    if (resultSet.next()) {
                        val idPaEnfermo = resultSet.getInt("id_pa_enfermos")
                        resultSet.close()
                        return@withContext idPaEnfermo
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    it.close()
                }

            }
            null
        }

    private suspend fun darIdMed(nombreMed: String): Int? =
        withContext(Dispatchers.IO) {
            val queryDM = "SELECT id_medicamentos FROM Medicamentos WHERE medicamentos_nombre = ?"
            val objConexion = ClaseConexion().cadenaConexion()

            objConexion?.let {
                try {
                    val statement = it.prepareStatement(queryDM)
                    statement.setString(1, nombreMed)
                    val resultSet = statement.executeQuery()

                    if (resultSet.next()) {
                        val idMed = resultSet.getInt("id_medicamentos")
                        resultSet.close()
                        return@withContext idMed
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    it.close()
                }
            }

            null
        }

    private suspend fun guardarMedSelect(
        horaAsignada: String,
        idEnfermo: Int,
        idMed: Int
    ): Boolean = withContext(Dispatchers.IO) {
        val queryGMS =
            "INSERT INTO asignacion_Medicamentos (asignacion_hora, id_pa_enfermos, id_medicamentos) VALUES (?, ?, ?)"
        val objConexion = ClaseConexion().cadenaConexion()

        objConexion?.let {
            try {
                val statement = it.prepareStatement(queryGMS)
                statement.setString(1, horaAsignada)
                statement.setInt(2, idEnfermo)
                statement.setInt(3, idMed)
                statement.executeUpdate()
                statement.close()
                it.close()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            } finally {
                it.close()
            }
        } ?: false
    }

}