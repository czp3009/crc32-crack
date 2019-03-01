import com.hiczp.crc32crack.Crc32Cracker

fun main() {
    val start = System.currentTimeMillis()

    println(Crc32Cracker.crack(0x822d8a7e.toInt()))

    val end = System.currentTimeMillis()
    println("Done in ${end - start} ms")
}
