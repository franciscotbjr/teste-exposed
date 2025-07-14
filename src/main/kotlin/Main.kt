package org.hexasilith

import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.types.int
import org.hexasilith.config.DatabaseConfig

class Hello : CliktCommand() {
    val count: Int by option().int().default(1).help("Number of greetings")
    val name: String by option().prompt("Your name").help("The person to greet")

    override fun run() {
        DatabaseConfig.database
        repeat(count) {
            echo("Hello $name!")
        }
    }
}

fun main(args: Array<String>) = Hello().main(args)