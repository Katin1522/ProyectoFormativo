package RecyclerViewHelper

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import katherine.fabiola.formativoproyecto.R

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val txtEnfermoNombre = view.findViewById<TextView>(R.id.txtNomEnfermo)
    val txtEnfermoApellido = view.findViewById<TextView>(R.id.txtApellidoEnfermo)
    val btnDeleteEnfermo = view.findViewById<ImageView>(R.id.btnEnfermoDelete)
    val btnEditarEnfermo = view.findViewById<ImageView>(R.id.btnEnfermoEdit)

}
