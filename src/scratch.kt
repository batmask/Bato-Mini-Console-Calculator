package calculator

// Free scratch file for small testing
class Version(private val major: Int, private val minor: Int): Comparable<Version> {
    override fun compareTo(other: Version): Int {
        return if (this.major != other.major) {
            this.major - other.major
        } else if (this.minor != other.minor) {
            this.minor - other.minor
        } else 0
    }

}

fun main(args: Array<String>){
    println(Version(1, 2) > Version(1, 3))
    println(Version(2, 0) > Version(1, 5))
}
