package org.hexasilith.util

import kotlinx.coroutines.*
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.ansi

class LoadingIndicator {
    private var job: Job? = null
    private val loadingStates = arrayOf("🤖      ", "🤖.     ", "🤖..    ", "🤖...   ", "🤖....  ", "🤖..... ", "🤖......", "🤖..... ", "🤖....  ", "🤖...   ", "🤖..    ", "🤖.     ")
    private var currentIndex = 0

    fun start(message: String = "Processando sua mensagem") {
        // Exibe a mensagem inicial uma única vez
        print("$message: ")

        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                // Move o cursor de volta para o início do indicador e sobrescreve
                print("\r$message: ${loadingStates[currentIndex]}")

                currentIndex = (currentIndex + 1) % loadingStates.size
                delay(200)
            }
        }
    }

    fun stop() {
        job?.cancel()
        // Limpa o indicador e quebra a linha para a próxima saída
        print("\r${" ".repeat(50)}\r")
    }
}
