package org.hexasilith.model

enum class Role {
    SYSTEM, USER, ASSISTANT;

    companion object {
        fun fromString(value: String): Role {
            return valueOf(value.uppercase())
        }
    }
}