package katherine.fabiola.formativoproyecto

import Modelo.ClaseConexion
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class entrar_doctores : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_entrar_doctores)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val txtCorreoDoctor = findViewById<EditText>(R.id.txtCorreoDoctor)
        val txtContrasenaDoctor = findViewById<EditText>(R.id.txtContrasenaDoctor)
        val btnIngresarDoctor = findViewById<Button>(R.id.btnIngresarDoctor)

        btnIngresarDoctor.setOnClickListener {

            val correoD = txtCorreoDoctor.text.toString()
            val contrasenaD = txtContrasenaDoctor.text.toString()

            if (correoD.isEmpty() || contrasenaD.isEmpty()) {
                Toast.makeText(
                    this,
                    "Agregar todas las casillas",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val pantallaPrincipal = Intent(this, listado_pa_enfermos::class.java)

                CoroutineScope(Dispatchers.IO).launch {
                    val objConexion = ClaseConexion().cadenaConexion()

                    val comprobarUsuario =
                        objConexion?.prepareStatement("SELECT * FROM doctores WHERE correo_doctores = ? AND contrasena_doctores = ?")!!
                    comprobarUsuario.setString(1, txtCorreoDoctor.text.toString())
                    comprobarUsuario.setString(2, txtContrasenaDoctor.text.toString())
                    val resultado = comprobarUsuario.executeQuery()

                    if (resultado.next()) {
                        startActivity(pantallaPrincipal)

                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@entrar_doctores,
                                "Creedenciales Invalidad",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }
                }
            }

        }
    }
}
