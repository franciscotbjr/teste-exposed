package org.hexasilith.config

import com.typesafe.config.ConfigFactory

object AppConfig {
    private val config = ConfigFactory.load()
    val apiKey = config.getString("api.key")
}