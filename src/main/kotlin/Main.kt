package org.hexasilith

import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.types.int
import org.hexasilith.config.DatabaseConfig
import org.hexasilith.util.ConsolePrinter

class Chat : CliktCommand() {

    override fun run() {

        DatabaseConfig.database

        ConsolePrinter().printWelcome()

    }
}

fun main(args: Array<String>) = Chat().main(args)