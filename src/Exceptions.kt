package calculator

import java.io.PrintWriter
import java.io.StringWriter

class InvalidExpressionException: Exception {
    constructor(): super("Invalid expression")
    constructor(message: String): super("Invalid expression: $message")
}

class UnknownCommandException: Exception {
    constructor(): super("Unknown command")
    constructor(message: String): super("Unknown command: $message")
}

class UnknownVariableException: Exception {
    constructor(): super("Unknown variable")
    constructor(message: String): super("Unknown variable: $message")
}

class InvalidIdentifierException: Exception{
    constructor(): super("Invalid identifier")
    constructor(message: String): super("Invalid identifier: $message")
}

class InvalidAssignmentException: Exception{
    constructor(): super("Invalid assignment")
    constructor(message: String): super("Invalid assignment: $message")
}

val Exception.stackTraceString: String
    get(){
        val stringWriter = StringWriter()
        this.printStackTrace(PrintWriter(stringWriter))
        return stringWriter.toString()
    }