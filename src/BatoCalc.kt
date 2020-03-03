package calculator

class BatoCalc {
    private var inputType = InputType.NONE
    private val variablesMap = mutableMapOf<String, Int>()
    private var isExit = false

    fun calcLoop() {
        println("======================================")
        println("          Bato mini calculator        ")
        println("======================================")
        do {
            val line = readLine()
            val trimmed = BatoCalcParser.trim(line) ?: continue
            inputType = BatoCalcParser.checkInputType(trimmed)
            try {
                when (inputType) {
                    InputType.COMMAND -> {
                        doCommand(trimmed)
                    }

                    InputType.MATH_EXPRESSION -> {
                        processExpression(trimmed)
                    }

                    InputType.NONE -> {

                    }
                }
            }catch(e: Exception){
                println(e.message)
                //println(e.stackTraceString)
            }


        }while(!isExit)
    }

    private fun doCommand(trimmed: String){
        when (trimmed) {
            "/help" -> {
                println("The program calculates +, -, *, / operations.")
                println(" - /help : help message.")
                println(" - /exit : exit calculator.")
                println(" - [variable] = [int number|variable] : define variable or reassign to existing variable.")
                println(" - /var : view list of variables")
                println(" - Math expression : You can only use +/-, () and defined variable.")
                println("                     usage) 3 + (5-7) + age ")
            }
            "/exit" -> {
                println("Bye!")
                isExit = true
            }
            "/var" -> {
                if(variablesMap.isEmpty()) println("Empty")
                else variablesMap.forEach{ (k, v) -> println("$k = $v")}
            }
            else -> throw UnknownCommandException()
        }
    }

    private fun processExpression(trimmed: String){
        // 1. check assginment and divide it.

        // 2. evaluate expression
        val intValue = eval(trimmed)
        if(intValue != null) println(intValue.toString())
    }

    private fun eval(trimmed: String): Int?{
        // 1. tokenize
        val tokens = BatoCalcParser.tokenize(trimmed)

        // 2. infix to postfix
        val tokensPostfix = BatoCalcParser.infix2Postfix(tokens)
        //println(tokensPostfix.joinToString(","))

        // 3. evaluate using stack
        return doCalc(tokensPostfix)
    }

    private fun doCalc(tokensPostfix: List<CalcToken>): Int? {
        var isAssignment: Boolean = false
        val valueStack = Stack<CalcToken>()
        //val valueStack = Stack<Int>()

        for(token in tokensPostfix){
            when(token.tokenType) {
                TokenType.OPERATOR -> {
                    when(token.subType) {
                        SubType.PLUS_UNARY -> {
                            // Do nothing
                        }
                        SubType.MINUS_UNARY -> {
                            val opr1 = valueStack.pop()
                            valueStack.push(CalcToken(TokenType.OPERAND, (-1 * getTokenValue(opr1)).toString(), SubType.NUMBER))
                        }
                        SubType.PLUS -> {
                            val opr2 = valueStack.pop()
                            val opr1 = valueStack.pop()
                            valueStack.push(CalcToken(TokenType.OPERAND, (getTokenValue(opr1) + getTokenValue(opr2)).toString(), SubType.NUMBER))
                        }
                        SubType.MINUS -> {
                            val opr2 = valueStack.pop()
                            val opr1 = valueStack.pop()
                            valueStack.push(CalcToken(TokenType.OPERAND, (getTokenValue(opr1) - getTokenValue(opr2)).toString(), SubType.NUMBER))
                        }
                        SubType.MULTIPLY -> {
                            val opr2 = valueStack.pop()
                            val opr1 = valueStack.pop()
                            valueStack.push(CalcToken(TokenType.OPERAND, (getTokenValue(opr1) * getTokenValue(opr2)).toString(), SubType.NUMBER))
                        }
                        SubType.DIVIDE -> {
                            val opr2 = valueStack.pop()
                            val opr1 = valueStack.pop()
                            valueStack.push(CalcToken(TokenType.OPERAND, (getTokenValue(opr1) / getTokenValue(opr2)).toString(), SubType.NUMBER))
                        }
                        SubType.ASSIGN -> {
                            val opr2 = valueStack.pop()
                            val opr1 = valueStack.pop()

                            if(opr1.subType != SubType.VARIABLE) throw InvalidAssignmentException()
                            val rExpressionValue = getTokenValue(opr2)
                            variablesMap[opr1.value] = rExpressionValue
                            //println("${opr1.value} = $rExpressionValue")

                            valueStack.push(CalcToken(TokenType.OPERAND, rExpressionValue.toString(), SubType.NUMBER))
                            isAssignment = true
                        }
                        else -> throw InvalidExpressionException()
                    }
                }

                TokenType.OPERAND -> {
                    valueStack.push(token)
                }
                else -> {
                    throw InvalidExpressionException()
                }
            }
        }

        //return valueStack.pop().value
        val ret = valueStack.pop()
        if(valueStack.count() > 0) throw InvalidExpressionException()

        return if(isAssignment) null
        else getTokenValue(ret)
        //else ret.value.toInt()
    }

    private fun getTokenValue(token: CalcToken): Int {
        return when(token.subType) {
            SubType.NUMBER -> {
                token.value.toInt()
            }
            SubType.VARIABLE -> {
                if(variablesMap.contains(token.value)){
                    return variablesMap[token.value]!!
                }else throw InvalidIdentifierException()
            }
            else -> throw InvalidIdentifierException()
        }
    }
}