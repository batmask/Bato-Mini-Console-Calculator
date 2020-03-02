package calculator

interface StackImplement<T> {
    fun count(): Int
    fun pop(): T
    fun peek(): T
    fun push(item: T)
    fun isEmpty(): Boolean
    fun isNotEmpty(): Boolean
    fun clear(): Unit
}

class Stack<T>: StackImplement<T> {
    private var list = mutableListOf<T>()

    constructor() {
        //super()
    }

    constructor(initialList: List<T>): this() {
        list = initialList.toMutableList()
    }

    override fun count(): Int {
        return list.size
    }

    override fun pop(): T {
        return list.removeAt(list.size - 1)
    }

    override fun peek(): T {
        return list[list.size - 1]
    }

    override fun push(item: T) {
        list.add(item)
    }

    override fun isEmpty(): Boolean {
        return list.size == 0
    }

    override fun isNotEmpty(): Boolean {
        return list.isNotEmpty()
    }

    override fun clear(): Unit {
        list.clear()
    }
}