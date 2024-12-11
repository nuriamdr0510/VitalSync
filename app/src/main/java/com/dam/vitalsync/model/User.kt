package com.dam.vitalsync.model

data class User(
    val id: String?,
    val userId: String,
    val nombre: String,
    val edad: Int?,
    val correo: String,
    val password: String?,
    val tipoEntrenador: Boolean
) {
    fun toMap(): MutableMap<Any, Any?> {
        return mutableMapOf(
            "user_Id" to this.userId,
            "nombre" to this.nombre,
            "edad" to this.edad.toString(),
            "correo" to this.correo,
            "password" to this.password,
            "tipoEntrenador" to this.tipoEntrenador
        )
    }
    init {
        require(edad == null || edad >= 0) { "Edad no puede ser negativa" }
    }

}