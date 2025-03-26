
import java.io.File

const val BALAO_VERMELHO = "\u001b[31m\u03D9\u001b[0m"
const val BALAO_AZUL = "\u001b[34m\u03D9\u001b[0m"
const val NOME_COMPUTADOR = "Computador"

fun contaBaloesLinha(tabuleiro: Array<Array<String?>>, linha: Int): Int {
    val numColunas = tabuleiro[0].size
    var baloes = 0
    for (coluna in 0 until numColunas - 1) {
        if (tabuleiro[linha][coluna] != null) baloes++
    }
    return baloes

}

fun contaBaloesColuna(tabuleiro: Array<Array<String?>>, coluna: Int): Int {
    val numLinhas = tabuleiro.size
    var baloes = 0
    for (linha in 0 until numLinhas) {
        if (tabuleiro[linha][coluna] != null) baloes++
    }
    return baloes

}

fun colocaBalao(tabuleiro: Array<Array<String?>>, coluna: Int, humano: Boolean): Boolean {
    val numLinhas = tabuleiro.size
    val jogador = if (humano) BALAO_VERMELHO else BALAO_AZUL
    for (linha in 0 until numLinhas) {
        if (tabuleiro[linha][coluna] == null) {
            tabuleiro[linha][coluna] = jogador
            return true
        }
    }
    return false
}

fun jogadaNormalComputador(tabuleiro: Array<Array<String?>>): Int {
    val numLinhas = tabuleiro.size
    val numColunas = tabuleiro[0].size

    for (linha in 0 until numLinhas) {
        for (coluna in 0 until numColunas) {
            if (tabuleiro[linha][coluna] == null) {
                tabuleiro[linha][coluna] = BALAO_AZUL
                return coluna
            }
        }
    }
    return -1
}

fun eVitoriaHorizontal(tabuleiro: Array<Array<String?>> ):Boolean {
    val numLinhas = tabuleiro.size
    val numColunas = tabuleiro[0].size

    for (linha in 0 until numLinhas) {
        var contador = 0
        var balaoAnterior: String? = null

        // Verifica as colunas para encontrar quatro balões consecutivos
        for (coluna in 0 until numColunas) {
            val balaoAtual = tabuleiro[linha][coluna]

            if (balaoAtual != null && balaoAtual == balaoAnterior) {
                contador++
            } else {
                contador = 1
            }

            if (contador == 4) {
                return true
            }
            balaoAnterior = balaoAtual
        }
    }

    return false
}

fun eVitoriaVertical(tabuleiro: Array<Array<String?>> ):Boolean{
    val numLinhas = tabuleiro.size
    val numColunas = tabuleiro[0].size
    for (coluna in 0 until numColunas) {
        var contador = 0
        var balaoAnterior: String? = null
        // Verifica as linhas para encontrar quatro balões consecutivos na vertical
        for (linha in 0 until numLinhas) {
            val balaoAtual = tabuleiro[linha][coluna]
            if (balaoAtual != null && balaoAtual == balaoAnterior) {
                contador++
            } else {
                contador = 1
            }
            if (contador == 4) {
                return true
            }
            balaoAnterior = balaoAtual
        }
    }
    return false
}

fun eVitoriaDiagonal(tabuleiro: Array<Array<String?>>):Boolean{
    val numLinhas = tabuleiro.size
    val numColunas = tabuleiro[0].size
    // Diagonais principais (\)
    for (linha in 0 until numLinhas - 3) {
        for (coluna in 0 until numColunas - 3) {
            val balao = tabuleiro[linha][coluna]
            if (balao != null &&
                balao == tabuleiro[linha + 1][coluna + 1] &&
                balao == tabuleiro[linha + 2][coluna + 2] &&
                balao == tabuleiro[linha + 3][coluna + 3]
            ) {
                return true
            }
        }
    }
    // Diagonais secundárias (/)
    for (linha in 0 until numLinhas - 3) {
        for (coluna in 3 until numColunas) {
            val balao = tabuleiro[linha][coluna]
            if (balao != null &&
                balao == tabuleiro[linha + 1][coluna - 1] &&
                balao == tabuleiro[linha + 2][coluna - 2] &&
                balao == tabuleiro[linha + 3][coluna - 3]
            ) {
                return true
            }
        }
    }
    return false
}

fun criaTabuleiroVazio(numLinhas: Int,numColunas: Int): Array<Array<String?>>{
    if (numLinhas < 1 || numColunas < 1 || numLinhas > 26|| numColunas > 26){
        return arrayOf(arrayOf(null))
    }
    return Array(numLinhas) { Array(numColunas) { null } }
}

fun ganhouJogo(tabuleiro: Array<Array<String?>> ):Boolean{
    return eVitoriaDiagonal(tabuleiro) ||
            eVitoriaVertical(tabuleiro) ||
            eVitoriaHorizontal(tabuleiro)
}

fun eEmpate(tabuleiro: Array<Array<String?>>):Boolean{
    // Verificar se todas as posições estão ocupadas com balões (não null)
    for (linha in tabuleiro) {
        for (celula in linha) {
            if (celula == null) {
                return false // Se encontrar uma célula vazia, não é empate
            }
        }
    }
    // Se ninguém ganhou e todas as posições estão ocupadas com balões, é empate
    return true
}

fun explodeBalao(tabuleiro: Array<Array<String?>>, coordenadas: Pair<Int, Int>): Boolean {
    val (linha, coluna) = coordenadas

    if (linha in 0 until tabuleiro.size && coluna in 0 until tabuleiro[0].size) {
        if (tabuleiro[linha][coluna] != null) {
            // Remove o balão da posição
            tabuleiro[linha][coluna] = null

            // Ajusta as células acima para "cair" para a posição correta
            for (linhaAtual in linha until tabuleiro.size - 1) {
                tabuleiro[linhaAtual][coluna] = tabuleiro[linhaAtual + 1][coluna]
            }

            // A última célula na coluna deve ser definida como nula
            tabuleiro[tabuleiro.size - 1][coluna] = null

            return true
        }
    }
    return false
}

fun jogadaExplodirComputador(tabuleiro: Array<Array<String?>>): Pair<Int, Int> {
    val coordenadasTresSeguidos = verificaTresSeguidos(tabuleiro)
    if (coordenadasTresSeguidos != null) {
        return coordenadasTresSeguidos
    }

    return escolheColunaComMenosBaloes(tabuleiro)
}

fun escolheColunaComMenosBaloes(tabuleiro: Array<Array<String?>>): Pair<Int, Int> {
    val linhas = tabuleiro.size
    val colunas = tabuleiro[0].size

    var colunaComMenosBaloes = 0
    var menorQuantidade = Int.MAX_VALUE

    for (coluna in 0 until colunas) {
        var quantidade = 0
        var primeiraLinhaComVermelho = -1

        for (linha in 0 until linhas) {
            if (tabuleiro[linha][coluna] == BALAO_VERMELHO) {
                quantidade++
                if (primeiraLinhaComVermelho == -1) {
                    primeiraLinhaComVermelho = linha
                }
            }
        }

        if (quantidade < menorQuantidade && primeiraLinhaComVermelho != -1) {
            menorQuantidade = quantidade
            colunaComMenosBaloes = coluna
        }
    }

    // Retornar a coordenada do primeiro balão vermelho da coluna escolhida
    for (linha in 0 until linhas) {
        if (tabuleiro[linha][colunaComMenosBaloes] == BALAO_VERMELHO) {
            return Pair(linha, colunaComMenosBaloes)
        }
    }

    // Caso não haja balões vermelhos
    return Pair(0, colunaComMenosBaloes)
}


fun verificaTresSeguidos(tabuleiro: Array<Array<String?>>): Pair<Int, Int>? {
    val linhas = tabuleiro.size
    val colunas = tabuleiro[0].size

    // Verificar horizontalmente
    for (i in 0 until linhas) {
        for (j in 0 until colunas - 2) {
            if (
                tabuleiro[i][j] == BALAO_VERMELHO &&
                tabuleiro[i][j + 1] == BALAO_VERMELHO &&
                tabuleiro[i][j + 2] == BALAO_VERMELHO
            ) {
                return Pair(i, j)
            }
        }
    }

    // Verificar verticalmente
    for (j in 0 until colunas) {
        for (i in 0 until linhas - 2) {
            if (
                tabuleiro[i][j] == BALAO_VERMELHO &&
                tabuleiro[i + 1][j] == BALAO_VERMELHO &&
                tabuleiro[i + 2][j] == BALAO_VERMELHO
            ) {
                return Pair(i, j)
            }
        }
    }

    return null
}

fun escolhePrimeiraColunaDisponivel(tabuleiro: Array<Array<String?>>): Pair<Int, Int> {
    val linhas = tabuleiro.size
    val colunas = tabuleiro[0].size

    for (j in 0 until colunas) {
        for (i in 0 until linhas) {
            if (tabuleiro[i][j] == BALAO_VERMELHO) {
                return Pair(i, j)
            }
        }
    }

    return Pair(0, 0) // Fallback
}



fun leJogo(nomedoficheiro:String):Pair<String, Array<Array<String?>>>{

    val linhas = File(nomedoficheiro).readLines()
    if (linhas.isEmpty()) {
        println("O ficheiro está vazio!")
        return Pair("", arrayOf())
    }
    val nomedojogador = linhas[0]
    val tabuleiro = Array(linhas.size - 1) { linhaIndex ->
        val valores = linhas[linhaIndex + 1].split(",")
        Array(valores.size) { colunaIndex ->
            when (valores[colunaIndex]) {
                "C" -> BALAO_AZUL
                "H" -> BALAO_VERMELHO
                else -> null
            }
        }
    }
    return Pair(nomedojogador, tabuleiro)
}

fun gravaJogo(nomedoficheiro: String, tabuleiro: Array<Array<String?>> ,nomedojogador:String){
    var conteudo = "$nomedojogador\n" // Começa com o nome do jogador

    for (linha in tabuleiro) {
        for (index in 0 until linha.size) {
            val simbolo = when (linha[index]) {
                BALAO_AZUL -> "C"
                BALAO_VERMELHO -> "H"
                else -> ""
            }
            // Adiciona símbolo seguido de vírgula ou nova linha
            conteudo += if (index < linha.size - 1) {
                "$simbolo,"
            } else {
                "$simbolo\n"
            }
        }
    }
    // Escreve o conteúdo no arquivo
    File(nomedoficheiro).writeText(conteudo)
}

fun validaTabuleiro(numLinhas: Int, numColunas: Int): Boolean {
    return if (numLinhas == 5 && numColunas == 6) {
        true
    } else if (numLinhas == 6 && numColunas == 7) {
        true
    } else if (numLinhas == 7 && numColunas == 8) {
        true
    } else {
        false
    }
}

fun processaColuna(numColunas: Int, coluna: String?):Int? {
    if (coluna != null){
        if (coluna.length == 1 && coluna[0] in 'A'..'Z'
            && coluna[0] - 'A' < numColunas) {
            return coluna[0] - 'A'
        }
    }
    return null
}

fun nomeValido(nome: String): Boolean {
    return nome.length in 3..12 && ' ' !in nome
}

fun criaTopoTabuleiro(numColunas: Int): String {
    if (numColunas < 1 || numColunas > 26) return ""
    var linha = ""
    var contadorDeColunas = 0
    val linhaHorizontal = "\u2550"
    while (contadorDeColunas < numColunas) {
        linha += (linhaHorizontal + linhaHorizontal+ linhaHorizontal)
        if(contadorDeColunas<numColunas-1){
            linha+=linhaHorizontal
        }
        contadorDeColunas++
    }

    return "\u2554$linha\u2557"
}


fun criaLegendaHorizontal(numColunas: Int): String {
    var resultado = " "
    if (numColunas in 1..26) {
        val colunas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        var count = 0
        while (count < numColunas) {
            resultado += " "
            resultado += colunas[count]
            resultado += " "
            if (count < numColunas - 1) {
                resultado += "|"
            }
            count++
        }
        resultado += " "
    }
    return resultado
}

fun criaTabuleiro(tabuleiroReal: Array<Array<String?>>, mostraLegenda:Boolean = true): String {

    val numLinhas = tabuleiroReal.size
    val numColunas = tabuleiroReal[0].size

    if (numLinhas < 1 || numColunas < 1 || numLinhas>26|| numColunas>26) return ""

    val linhaVertical = "\u2551"
    var tabuleiro = criaTopoTabuleiro(numColunas)+"\n"
    var contadorColunas = 0
    var contadorLinhas = 0
    while (contadorLinhas < numLinhas) {
        tabuleiro += linhaVertical
        while(contadorColunas<numColunas){

            if(tabuleiroReal[contadorLinhas][contadorColunas] != null){
                if(tabuleiroReal[contadorLinhas][contadorColunas] == BALAO_AZUL){
                    tabuleiro+= " $BALAO_AZUL "
                }
                if(tabuleiroReal[contadorLinhas][contadorColunas] == BALAO_VERMELHO){
                    tabuleiro+= " $BALAO_VERMELHO "
                }
            }else{
                tabuleiro+="   "
            }
            if(contadorColunas<numColunas-1){
                tabuleiro+="|"
            }
            contadorColunas++
        }
        tabuleiro += linhaVertical
        contadorColunas=0
        if(contadorLinhas<numLinhas-1){
            tabuleiro+="\n"
        }
        contadorLinhas++
    }
    if(mostraLegenda){
        tabuleiro+="\n"+criaLegendaHorizontal(numColunas)
    }
    return tabuleiro
}


fun obterTamanhoTabuleiro(): Pair<Int, Int> {
    var linhas: Int? = null
    var colunas: Int? = null
    var tabuleiroValido = false

    while (!tabuleiroValido) {
        linhas = solicitarNumero("Numero de linhas:")
        colunas = solicitarNumero("Numero de colunas:")

        tabuleiroValido = validaTabuleiro(linhas!!, colunas!!)
        if (!tabuleiroValido) {
            println("Tamanho do tabuleiro invalido")
        }
    }

    return Pair(linhas!!, colunas!!)
}

fun solicitarNumero(prompt: String): Int {
    while (true) {
        println(prompt)
        val entrada = readln()
        val numero = entrada.toIntOrNull()

        if (numero != null && numero > 0) {
            return numero
        }
        println("Numero invalido")
    }
}

fun obterNomeJogador(): String {
    var nomeValido = false
    var nomeJogador: String? = null

    while (!nomeValido) {
        println("Nome do jogador 1:")
        nomeJogador = readlnOrNull()
        if (nomeJogador != null && nomeValido(nomeJogador)) {
            nomeValido = true
        } else {
            println("Nome de jogador invalido")
        }
    }
    return nomeJogador!!
}

fun jogar(tabuleiroReal: Array<Array<String?>>, nomeJogadorReal: String, novoJogo: Boolean = true): Pair<String, Array<Array<String?>>> {
    var nomeJogador = nomeJogadorReal
    var tabuleiro = tabuleiroReal

    // Configuracao de um novo jogo
    if (novoJogo) {
        val (linhas, colunas) = obterTamanhoTabuleiro()
        nomeJogador = obterNomeJogador()
        tabuleiro = criaTabuleiroVazio(linhas, colunas)
    }

    while (true) {
        println(criaTabuleiro(tabuleiro))
        println("\n$nomeJogador: $BALAO_VERMELHO\nTabuleiro ${tabuleiro.size}X${tabuleiro[0].size}")

        // Jogada do humano
        val colunaEscolha = solicitarColuna(tabuleiro[0].size)
        if (processarJogadaHumana(colunaEscolha, tabuleiro, nomeJogador)) {
            return Pair(nomeJogador, tabuleiro)
        }

        // Verificar condições de vitória ou empate após a jogada do humano
        if (verificarFimDeJogo(tabuleiro, nomeJogador)) {
            return Pair(nomeJogador, tabuleiro)
        }

        // Jogada do computador
        realizarJogadaComputador(tabuleiro)

        if (verificarFimDeJogo(tabuleiro, NOME_COMPUTADOR)) {  // Usando a constante NOME_COMPUTADOR
            return Pair(NOME_COMPUTADOR, tabuleiro)  // Usando a constante NOME_COMPUTADOR
        }
    }
}

fun verificarExplodirDisponivel(tabuleiro: Array<Array<String?>>): Boolean {
    for (linha in tabuleiro) {
        for (celula in linha) {
            if (celula != null) {
                return true
            }
        }
    }
    return false
}

fun processarJogadaHumana(colunaEscolha: String, tabuleiro: Array<Array<String?>>, nomeJogador: String): Boolean {
    if (colunaEscolha.startsWith("Explodir")) {
        val indiceColuna = colunaEscolha.last() - 'A'
        if (explodeBalao(tabuleiro, Pair(0, indiceColuna))) {
            println("Balao ${colunaEscolha.last()} explodido!")
            println(criaTabuleiro(tabuleiro))
            processarExplosaoComputador(tabuleiro)
        } else {
            println("Funcionalidade Explodir nao esta disponivel")
        }
    } else {
        when (colunaEscolha) {
            "0" -> {
                menuPrincipal()
                return true
            }
            "-1" -> {
                menuGravaJogo(tabuleiro, nomeJogador)
                return true
            }
            else -> {
                val indiceColuna = colunaEscolha[0] - 'A'
                if (colocaBalao(tabuleiro, indiceColuna, true)) {
                    println("Coluna escolhida: ${colunaEscolha[0]}")
                    println(criaTabuleiro(tabuleiro))
                }
            }
        }
    }

    return false
}

 fun verificarFimDeJogo(tabuleiro: Array<Array<String?>>, nomeJogador: String): Boolean {
    // Verificar se o jogador ou computador ganhou
    if (ganhouJogo(tabuleiro)) {
        if (nomeJogador != "Computador") {
            println("\nParabens $nomeJogador! Ganhou!")
        } else {
            println(criaTabuleiro(tabuleiro))
            println("\nPerdeu! Ganhou o Computador.")

        }
        menuPrincipal()  // Volta para o menu principal ou encerra o jogo
        return true
    }

    // Verificar se houve empate
    if (eEmpate(tabuleiro)) {
        println("\nEmpate!")
        menuPrincipal()
        return true
    }

    return false
}

 fun realizarJogadaComputador(tabuleiro: Array<Array<String?>>) {
    val jogadaComputador = jogadaNormalComputador(tabuleiro)
    if (jogadaComputador != -1) {
        println("\nComputador: $BALAO_AZUL\nTabuleiro ${tabuleiro.size}X${tabuleiro[0].size}")
        println("Coluna escolhida: ${'A' + jogadaComputador}")
    }

}



 fun processarExplosaoComputador(tabuleiro: Array<Array<String?>>) {
    println()
    println("Prima Enter para continuar. O Computador ira explodir um dos seus baloes.")
    readln()
    val coordenadasComputador = jogadaExplodirComputador(tabuleiro)
    if (explodeBalao(tabuleiro, coordenadasComputador)) {
        println("Balao ${'A' + coordenadasComputador.second % 26},${coordenadasComputador.first + 1} explodido pelo Computador!")
        println(criaTabuleiro(tabuleiro))
    } else {
        println("Coluna vazia")
    }
}

fun solicitarColuna(colunas: Int, isTest: Boolean = false): String {
    while (true) {
        if (!isTest) {

            println("Coluna? (A..${'A' + colunas - 1}):")
        }
        val colunaEscolha = readlnOrNull()

        // Verifica comandos especiais
        if (colunaEscolha != null) {
            if (colunaEscolha.startsWith("Explodir")){

                if(colunaEscolha.last() in 'A'..('A' + colunas - 1)){
                    return colunaEscolha
                }
            }
            if (colunaEscolha == "Sair") {
                return "0"
            }
            if (colunaEscolha == "Gravar") {
                return "-1"
            }
        }


        // Valida entrada
        if (colunaEscolha != null && colunaEscolha.length == 1 && colunaEscolha[0] in 'A'..('A' + colunas - 1)) {
            return colunaEscolha
        }
        // Mensagem para entradas inválidas
        println("Coluna invalida")
    }
}

fun verJogoIniciado(tabuleiro: Array<Array<String?>>): Boolean {
    for (linha in tabuleiro) {
        for (celula in linha) {
            if (celula != null) { // Verifica se há algum balão no tabuleiro
                return true
            }
        }
    }
    return false
}



fun menuPrincipal(){
    println()
    println("1. Novo Jogo")
    println("2. Gravar Jogo")
    println("3. Ler Jogo")
    println("0. Sair")
    println()
}
fun menuGravaJogo(tabuleiro: Array<Array<String?>>, nomedojogador: String) {
    // Verifica se há um jogo em andamento (tabuleiro não nulo e não vazio)
    if (verJogoIniciado(tabuleiro)) {
        println("Introduza o nome do ficheiro (ex: jogo.txt)")
        val nomeDoFicheiro = readlnOrNull()?.trim()

        if (nomeDoFicheiro.isNullOrEmpty()) {
            println("Nome do ficheiro invalido")
        } else {
            gravaJogo(nomeDoFicheiro, tabuleiro, nomedojogador)
            println("Tabuleiro ${tabuleiro.size}x${tabuleiro[0].size} gravado com sucesso")
        }
    } else {
        println("Funcionalidade Gravar nao esta disponivel")
    }
}

fun menuLerJogo(): Pair<String, Array<Array<String?>>> {
    var nomeFicheiro = ""
    do{
        println("Introduza o nome do ficheiro (ex: jogo.txt)")
        nomeFicheiro = readln()

    } while (nomeFicheiro == "" && (nomeFicheiro != "Sair" || nomeFicheiro != "0"))

    if (nomeFicheiro == "Sair" || nomeFicheiro == "0"){
        menu()
        val tabuleiro = Array(1) { Array<String?>(1) { null } }
        return Pair("", tabuleiro)
    }else{
        val informacao = leJogo(nomeFicheiro)
        val nomeJogador = informacao.first
        var tabuleiro = informacao.second

        println("Tabuleiro ${tabuleiro.size}x${tabuleiro[0].size} lido com sucesso!")

        var partida =  jogar(tabuleiro, nomeJogador, false)
        return Pair(partida.first, partida.second)
    }

}


fun menu(mostrarCabelho: Boolean = true){

    var tabuleiro: Array<Array<String?>> = Array(1){Array<String?>(1) { null }}
    var nomeJogador: String = ""

    if(mostrarCabelho) {
        println()
        println("Bem-vindo ao jogo \"4 Baloes em Linha\"!")
    }

    menuPrincipal()

    var numero: Int?
    do {
        numero = readlnOrNull()?.toIntOrNull()

        when (numero) {
            0 -> println("A sair...")
            1 -> {
                var partida = jogar(tabuleiro, nomeJogador)
                nomeJogador = partida.first
                tabuleiro = partida.second
            }
            2 -> {
                menuGravaJogo(tabuleiro, nomeJogador)
            }
            3 -> {
                var partida =  menuLerJogo()
                nomeJogador = partida.first
                tabuleiro = partida.second
            }
            else -> println("Opcao invalida. Por favor, tente novamente.")
        }
    } while (numero != 0)
}

fun main() {
    menu()
}