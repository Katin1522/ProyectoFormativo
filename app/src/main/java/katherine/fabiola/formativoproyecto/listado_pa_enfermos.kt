package katherine.fabiola.formativoproyecto

import Modelo.ClaseConexion
import Modelo.pa_enfermos
import RecyclerViewHelper.Adaptador
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class listado_pa_enfermos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listado_pa_enfermos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnAgregar = findViewById<ImageView>(R.id.btnAgregar)
        btnAgregar.setOnClickListener {
            val panAgregarPEnfermos = Intent(this, agregar_pa_enfermos::class.java)
            startActivity(panAgregarPEnfermos)
        }

        val rcvPaEnfermos = findViewById<RecyclerView>(R.id.rcvPaEnfermos)

        rcvPaEnfermos.layoutManager = LinearLayoutManager(this)

        fun obtenerPaEnfermos(): List<pa_enfermos> {
            val listaPaEnfermos = mutableListOf<pa_enfermos>()
            val objConexion = ClaseConexion().cadenaConexion()
            objConexion?.use { conn ->
                val statement = conn.createStatement()
                val resultSet = statement?.executeQuery("SELECT * FROM pa_Enfermos")!!
                while (resultSet.next()) {
                    val id_pa_enfermos = resultSet.getInt("id_pa_enfermos")
                    val nombre_enfermo = resultSet.getString("nombre_enfermo")
                    val apellido_enfermo = resultSet.getString("apellido_enfermo")
                    val edad = resultSet.getInt("edad")
                    val enfermo_enfermedad = resultSet.getString("enfermo_enfermedad")
                    val numero_hab = resultSet.getInt("numero_hab")
                    val cama_numero = resultSet.getInt("cama_numero")
                    val fecha = resultSet.getString("fecha")
                    val valoresJuntos = pa_enfermos(
                        id_pa_enfermos,
                        nombre_enfermo,
                        apellido_enfermo,
                        edad,
                        enfermo_enfermedad,
                        numero_hab,
                        cama_numero,
                        fecha.toString()
                    )
                    listaPaEnfermos.add(valoresJuntos)
                }
            }
            return listaPaEnfermos
        }

        CoroutineScope(Dispatchers.IO).launch {
            val nuevoPaEnfermos = obtenerPaEnfermos()
            withContext(Dispatchers.IO){
                (rcvPaEnfermos.adapter as? Adaptador)?.ActuLista(nuevoPaEnfermos)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            val EnfermosBaseDatos = obtenerPaEnfermos()
            withContext(Dispatchers.Main){
                val adapter = Adaptador(EnfermosBaseDatos)
                rcvPaEnfermos.adapter = adapter
            }
        }

    }
}