package katherine.fabiola.formativoproyecto

import Modelo.ClaseConexion
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class agregar_pa_enfermos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_pa_enfermos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnMedicamento = findViewById<Button>(R.id.btnMedicamento)
        btnMedicamento.setOnClickListener {
            val panAgregarMed = Intent(this, agregar_medicamentos::class.java)
            startActivity(panAgregarMed)
        }

        val btnHome = findViewById<ImageView>(R.id.btnHome)
        btnHome.setOnClickListener {
            val panListadoEnfermos = Intent(this, listado_pa_enfermos::class.java)
            startActivity(panListadoEnfermos)
        }

        val txtEnfermoNombre = findViewById<EditText>(R.id.txtEnfermoNombre)
        val txtEnfermoApellido = findViewById<EditText>(R.id.txtEnfermoApellido)
        val txtEnfermoEdad = findViewById<EditText>(R.id.txtEnfermoEdad)
        val txtEEnfermedad = findViewById<EditText>(R.id.txtEnfermoEnfermedad)
        val txtNumeroHab = findViewById<EditText>(R.id.txtNumHab)
        val txtCama = findViewById<EditText>(R.id.txtCama)
        val txtFechaI = findViewById<EditText>(R.id.txtFechaI)

        txtFechaI.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { view, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
                    val calendarioSeleccionado = Calendar.getInstance()
                    calendarioSeleccionado.set(anioSeleccionado, mesSeleccionado, diaSeleccionado)
                    val fechaSeleccionada = "$diaSeleccionado/${mesSeleccionado + 1}/$anioSeleccionado"
                    txtFechaI.setText(fechaSeleccionada)
                },
                anio, mes, dia
            )

            datePickerDialog.show()
        }

        val btnAgregarEnfermo = findViewById<Button>(R.id.btnEnfermoAgregar)

        btnAgregarEnfermo.setOnClickListener {

            val nombre = txtEnfermoNombre.text.toString()
            val apellido = txtEnfermoApellido.text.toString()
            val edad = txtEnfermoEdad.text.toString()
            val enfermedad = txtEEnfermedad.text.toString()
            val hab = txtNumeroHab.text.toString()
            val cama = txtCama.text.toString()
            val fechai = txtFechaI.text.toString()

            if(nombre.isEmpty() || apellido.isEmpty() || edad.isEmpty() || enfermedad.isEmpty() || hab.isEmpty() || cama.isEmpty() || fechai.isEmpty()){
                Toast.makeText(
                    this,
                    "Llenar todos los apartados porfavor",
                    Toast.LENGTH_SHORT
                ).show()
            }else {
                CoroutineScope(Dispatchers.IO).launch {
                    val objConexion = ClaseConexion().cadenaConexion()
                    val addEnfermo =
                        objConexion?.prepareStatement("INSERT INTO pa_Enfermos(nombre_enfermo, apellido_enfermo, edad, enfermo_enfermedad, numero_hab, cama_numero, fecha) VALUES (?, ?, ?, ?, ?, ?, ?)")!!
                    addEnfermo.setString(1, txtEnfermoNombre.text.toString())
                    addEnfermo.setString(2, txtEnfermoApellido.text.toString())
                    addEnfermo.setInt(3, txtEnfermoEdad.text.toString().toInt())
                    addEnfermo.setString(4, txtEEnfermedad.text.toString())
                    addEnfermo.setInt(5, txtNumeroHab.text.toString().toInt())
                    addEnfermo.setInt(6, txtCama.text.toString().toInt())
                    addEnfermo.setString(7, txtFechaI.text.toString())
                    addEnfermo.executeQuery()

                    withContext(Dispatchers.Main) {
                        AlertDialog.Builder(this@agregar_pa_enfermos)
                            .setTitle("Paciente Registrado")
                            .setMessage("Ahora para continuar debes agregar un medicamento")
                            .setPositiveButton("Aceptar", null)
                            .show()
                        txtEnfermoNombre.setText("")
                        txtEnfermoApellido.setText("")
                        txtEnfermoEdad.setText("")
                        txtEEnfermedad.setText("")
                        txtNumeroHab.setText("")
                        txtCama.setText("")
                        txtFechaI.setText("")
                    }
                }
            }
        }



    }
}