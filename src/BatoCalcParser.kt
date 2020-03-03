package calculator

enum class InputType{COMMAND, MATH_EXPRESSION, NONE}
enum class TokenType{OPERAND, OPERATOR, BRACKET, NONE}
enum class SubType(val priority: Int){
    PLUS_UNARY(10), MINUS_UNARY(10),
    PLUS(1), MINUS(1), DIVIDE(2), MULTIPLY(2),
    ASSIGN(0),
    NUMBER(-1), VARIABLE(-1),
    BRACKET_OPEN(-1), BRACKET_CLOSE(-1),
    NONE(-1)

}

data class CalcToken(val tokenType: TokenType = TokenType.NONE, var value: String = "", val subType: SubType){
    constructor(tokenType:TokenType, subType: SubType): this(tokenType, "", subType)

    override fun toString(): String {
        val ret: String
        when(tokenType) {
            TokenType.OPERAND -> {
                ret = value
            }

            TokenType.OPERATOR -> {
                ret = when(subType) {
                    SubType.PLUS_UNARY -> "+(u)"
                    SubType.MINUS_UNARY -> "-(u)"
                    SubType.PLUS -> "+"
                    SubType.MINUS -> "-"
                    SubType.MULTIPLY -> "*"
                    SubType.DIVIDE -> "/"
                    SubType.ASSIGN -> "="
                    else -> "Unknown"
                }
            }

            TokenType.BRACKET -> {
                ret = when(subType) {
                    SubType.BRACKET_OPEN -> "("
                    SubType.BRACKET_CLOSE -> ")"
                    else -> "Unknown"
                }
            }

            else -> {
                ret = "Unknown"
            }
        }

        return ret
    }
}

class BatoCalcParser{

    companion object{
        private val VALID_COMMAND: Char = '/'
        private val VALID_OPERATOR_ARITHMETIC: Set<Char> = setOf<Char>('-', '+', '*', '/', '=')
        private val VALID_OPERATOR_BRACKET: Set<Char> = setOf<Char>('(', ')')
        //private val VALID_OPERATOR_ASSIGN: Set<Char> = setOf<Char>('=')
        private val VALID_OPERAND_NUMBER: Set<Char> = setOf<Char>('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        private val VALID_OPERAND_SMALL_LATIN: Set<Char> = setOf<Char>('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z')
        private val VALID_OPERAND_BIG_LATIN: Set<Char> = setOf<Char>('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
            'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')

        private const val OP_PLUS: Char = '+'
        private const val OP_MINUS: Char = '-'
        private const val OP_MULTIPLY: Char = '*'
        private const val OP_DIVIDE: Char = '/'
        private const val OP_ASSIGN: Char = '='
        private const val BRACKET_OPEN: Char = '('
        private const val BRACKET_CLOSE: Char = ')'

        fun checkInputType(expression: String?): InputType{
            val trimmed = trim(expression) ?: return InputType.NONE

            return when(trimmed.first()){
                VALID_COMMAND                   -> InputType.COMMAND
                in VALID_OPERATOR_ARITHMETIC    -> InputType.MATH_EXPRESSION
                in VALID_OPERATOR_BRACKET       -> InputType.MATH_EXPRESSION
                //in VALID_OPERATOR_ASSIGN      -> InputType.MATH_EXPRESSION
                in VALID_OPERAND_NUMBER         -> InputType.MATH_EXPRESSION
                in VALID_OPERAND_SMALL_LATIN    -> InputType.MATH_EXPRESSION
                in VALID_OPERAND_BIG_LATIN      -> InputType.MATH_EXPRESSION
                else                            -> InputType.NONE
            }
        }

        fun trim(expression: String?): String? {
            val trimmed = expression?.trim()
            return if(trimmed == null || trimmed.isEmpty()) null
            else trimmed
        }

        fun tokenize(trimmed: String): List<CalcToken> {
            val ret = mutableListOf<CalcToken>()

            var idx = 0
            while(idx < trimmed.length){
                val startIdx = idx
                var endIdx = startIdx
                val token = mutableListOf<Char>()

                when{
                    trimmed[startIdx] in VALID_OPERATOR_ARITHMETIC -> {
                        token.add(trimmed[endIdx])

                        when(trimmed[endIdx]) {
                            OP_PLUS -> {
                                // unary operator
                                if(ret.isEmpty() || ret.last().tokenType == TokenType.OPERATOR || ret.last().subType == SubType.BRACKET_OPEN){
                                    ret.add(CalcToken(TokenType.OPERATOR, SubType.PLUS_UNARY))
                                }else {
                                    ret.add(CalcToken(TokenType.OPERATOR, SubType.PLUS))
                                }
                            }
                            OP_MINUS -> {
                                if(ret.isEmpty() || ret.last().tokenType == TokenType.OPERATOR || ret.last().subType == SubType.BRACKET_OPEN){
                                    ret.add(CalcToken(TokenType.OPERATOR, SubType.MINUS_UNARY))
                                }else{
                                    ret.add(CalcToken(TokenType.OPERATOR, SubType.MINUS))
                                }
                            }
                            OP_MULTIPLY -> {
                                if(ret.isEmpty() || ret.last().tokenType == TokenType.OPERATOR || ret.last().subType == SubType.BRACKET_OPEN){
                                    throw InvalidExpressionException()
                                }

                                ret.add(CalcToken(TokenType.OPERATOR, SubType.MULTIPLY))
                            }
                            OP_DIVIDE -> {
                                if(ret.isEmpty() || ret.last().tokenType == TokenType.OPERATOR || ret.last().subType == SubType.BRACKET_OPEN){
                                    throw InvalidExpressionException()
                                }

                                ret.add(CalcToken(TokenType.OPERATOR, SubType.DIVIDE))
                            }
                            OP_ASSIGN -> {
                                ret.add(CalcToken(TokenType.OPERATOR, SubType.ASSIGN))
                            }
                        }
                    }

//                    trimmed[startIdx] in VALID_OPERATOR_ASSIGN -> {
//                        token.add(trimmed[endIdx])
//                        ret.add(CalcToken(TokenType.OPERATOR, SubType.ASSIGN))
//                    }

                    trimmed[startIdx] in VALID_OPERATOR_BRACKET -> {
                        token.add(trimmed[endIdx])

                        when(trimmed[endIdx]) {
                            BRACKET_OPEN -> {
                                ret.add(CalcToken(TokenType.BRACKET, SubType.BRACKET_OPEN))
                            }
                            BRACKET_CLOSE -> {
                                ret.add(CalcToken(TokenType.BRACKET, SubType.BRACKET_CLOSE))
                            }
                        }
                    }

                    trimmed[startIdx] in VALID_OPERAND_NUMBER -> {
                        do{
                            token.add(trimmed[endIdx])
                            endIdx++
                        }while(endIdx < trimmed.length && trimmed[endIdx] in VALID_OPERAND_NUMBER)

                        ret.add(CalcToken(TokenType.OPERAND, token.joinToString(""), SubType.NUMBER))
                    }

                    trimmed[startIdx] in VALID_OPERAND_SMALL_LATIN ||
                            trimmed[startIdx] in VALID_OPERAND_BIG_LATIN -> {
                        do{
                            token.add(trimmed[endIdx])
                            endIdx++
                        }while(endIdx < trimmed.length &&
                            (trimmed[endIdx] in VALID_OPERAND_SMALL_LATIN ||
                                    trimmed[endIdx] in VALID_OPERAND_BIG_LATIN ||
                                    trimmed[endIdx] in VALID_OPERAND_NUMBER))

                        ret.add(CalcToken(TokenType.OPERAND, token.joinToString(""), SubType.VARIABLE))
                    }

                    trimmed[startIdx].isWhitespace() -> {
                        // pass spaces
                        do{
                            token.add(trimmed[endIdx])
                            endIdx++
                        }while(endIdx < trimmed.length && trimmed[endIdx].isWhitespace())

                    }

                    else -> {
                        throw InvalidExpressionException()
                    }
                }

                idx += token.count()
            }

            if(ret.isEmpty()) throw InvalidExpressionException()
            else return ret
        }

        fun infix2Postfix(tokens: List<CalcToken>): List<CalcToken>{
            val result = mutableListOf<CalcToken>()
            val opStack: Stack<CalcToken> = Stack<CalcToken>()
            //var prevToken: CalcToken? = null

            for(token in tokens){
                when(token.tokenType) {
                    TokenType.OPERATOR -> {
                        while(opStack.isNotEmpty() &&
                            (opStack.peek().subType != SubType.BRACKET_OPEN &&
                                    opStack.peek().subType.priority >= token.subType.priority)) {
                            result.add(opStack.pop())
                        }
                        opStack.push(token)
                    }

                    TokenType.OPERAND -> {
                        result.add(token)
                    }

                    TokenType.BRACKET -> {
                        when(token.subType){
                            SubType.BRACKET_OPEN -> {
                                opStack.push(token)
                            }

                            SubType.BRACKET_CLOSE -> {
                                var done = false
                                do {
                                    // close parenthesis must pair with open parenthesis.
                                    if(opStack.isEmpty()) throw InvalidExpressionException()

                                    val op = opStack.pop()
                                    if(op.subType == SubType.BRACKET_OPEN){
                                        done = true
                                    } else {
                                        result.add(op)
                                    }

                                }while(!done)
                            }

                            else -> {
                                throw InvalidExpressionException()
                            }
                        }
                    }

                    else -> {
                        throw InvalidExpressionException()
                    }
                }
            }

            // pop all opstack
            while(opStack.isNotEmpty()){
                result.add(opStack.pop())
            }

            if(result.contains( CalcToken(TokenType.OPERATOR, SubType.ASSIGN))){
                if((result.last().tokenType != TokenType.OPERATOR || result.last().subType != SubType.ASSIGN) ||
                    (result.count{it.tokenType == TokenType.OPERATOR && it.subType == SubType.ASSIGN} != 1)){
                    throw InvalidAssignmentException(result.joinToString(","))
                }
            }

            return result
        }
    }
}