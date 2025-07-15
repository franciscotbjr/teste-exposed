package org.hexasilith.config

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import java.io.File

object DatabaseConfig {

    private val config = ConfigFactory.load()

    private val dbPath = config.getString("database.path").also {
        File(it).parentFile?.mkdirs()
    }

    private val hikariConfig by lazy {
        HikariConfig().apply {
            jdbcUrl = "jdbc:sqlite:${dbPath}"
            driverClassName = "org.sqlite.JDBC"
            maximumPoolSize = 1
            isAutoCommit = false
            validate()
        }
    }

    val dataSource by lazy {
        HikariDataSource(hikariConfig)
    }

    val database by lazy {
        Database.connect(dataSource).also {
            initializeDatabase()
        }
    }

    private fun initializeDatabase() {
        // Executar migrações do Flyway
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .load()
        
        flyway.migrate()
        
        // Todas as tabelas agora são gerenciadas pelo Flyway
        // Não é mais necessário criar tabelas com SchemaUtils
    }

}