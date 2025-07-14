package org.hexasilith.util

import java.util.Scanner

class InputReader {

    private val scanner = Scanner(System.`in`)

    fun readInput(): String {
        return scanner.nextLine()
    }
}