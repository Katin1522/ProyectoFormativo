package katherine.fabiola.formativoproyecto

import Modelo.ClaseConexion
import android.content.Intent
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

class agregar_medicamentos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_medicamentos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnHome = findViewById<ImageView>(R.id.btnHome)
        btnHome.setOnClickListener {
            val panListadoEnfermos = Intent(this, listado_pa_enfermos::class.java)
            startActivity(panListadoEnfermos)
        }

        val btnEnfermos = findViewById<Button>(R.id.btnEnfermos)
        btnEnfermos.setOnClickListener {
            val panAgregarMedicamentos = Intent(this, agregar_pa_enfermos::class.java)
            startActivity(panAgregarMedicamentos)
        }

        val btnDarMed = findViewById<Button>(R.id.btnMedDar)
        btnDarMed.setOnClickListener {
            val panDarMed = Intent(this, dar_medicamentos::class.java)
            startActivity(panDarMed)
        }

        val txtMedNombre = findViewById<EditText>(R.id.txtMedNombre)
        val txtMedDescripcion = findViewById<EditText>(R.id.txtMedDescripcion)
        val btnAgregarMed = findViewById<Button>(R.id.btnAgregarMed)

        btnAgregarMed.setOnClickListener {

            val nombreMed = txtMedNombre.text.toString()
            val descripcionMed = txtMedDescripcion.text.toString()

            if(nombreMed.isEmpty() || descripcionMed.isEmpty()){
                Toast.makeText(
                    this,
                    "No se puede guardar los medicamentos agregados",
                    Toast.LENGTH_SHORT
                ).show()
            }else {
                CoroutineScope(Dispatchers.IO).launch {
                    val objConexion = ClaseConexion().cadenaConexion()
                    val addMed =
                        objConexion?.prepareStatement("INSERT INTO medicamentos (medicamentos_nombre, descripcion_med) VALUES (?, ?)")!!
                    addMed.setString(1, txtMedNombre.text.toString())
                    addMed.setString(2, txtMedDescripcion.text.toString())
                    addMed.executeQuery()

                    withContext(Dispatchers.Main) {
                        AlertDialog.Builder(this@agregar_medicamentos)
                            .setTitle("Se agrego el medicamento con exito")
                            .setMessage("Se a logrado guardar y agregar el medicamento de forma buena")
                            .setPositiveButton("Aceptar", null)
                            .show()
                        txtMedNombre.setText("")
                        txtMedDescripcion.setText("")
                    }
                }
            }
        }

    }
}