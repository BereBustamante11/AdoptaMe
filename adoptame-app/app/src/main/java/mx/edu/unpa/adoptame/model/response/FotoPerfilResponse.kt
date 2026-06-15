package mx.edu.unpa.adoptame.model.response
import com.google.gson.annotations.SerializedName

 data class FotoPerfilResponse(
     @SerializedName("urlFotoPerfil") val urlFotoPerfil: String
 )