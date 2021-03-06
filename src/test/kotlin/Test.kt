import com.hiczp.crc32crack.Crc32Cracker

val hashes = arrayOf(
    "766bd575",
    "63ccd15d",
    "fd2df0b3",
    "ce0115d5",
    "793a61e3",
    "d743d139",
    "cf8dd9bf",
    "16b79618",
    "8d45d74d",
    "3f530220",
    "3f530220",
    "3ec02d3",
    "abca552c",
    "5f62afc0",
    "fdbec380",
    "29fa3a8b",
    "f9f021dd",
    "78c04697",
    "92e03335",
    "9d6ccb4c",
    "160c0e39",
    "8b6c54fc",
    "ce4e8ab1",
    "bd90c906",
    "bd90c906",
    "a749a95",
    "f677602",
    "51c279fc",
    "7497f23d",
    "9edda172",
    "4d3b048f",
    "191762ef",
    "e5590223",
    "dee851ea",
    "b5220f3a",
    "aabda2b1",
    "58113f15",
    "35fe3ccb",
    "bff9f700",
    "31782e70",
    "28259456",
    "966a14a3",
    "383bde1e",
    "7dc9e702",
    "2a85a104",
    "b179927c",
    "311191ac",
    "7f7593b6",
    "987e8257",
    "d1865cd4",
    "f972be3c",
    "1bdd0417",
    "6c061ea8",
    "aa0ad363",
    "e6788939",
    "e6788939",
    "e6788939",
    "2647628e",
    "125b7a85",
    "be3f4749",
    "be3f4749",
    "3acca374",
    "346ef7f6",
    "f06e8386",
    "4bf0df6",
    "992ef625",
    "793a61e3",
    "153c8193",
    "153c8193",
    "e7ac7686",
    "a1340625",
    "bd90c906",
    "bd90c906",
    "9342605b",
    "6f7be8c0",
    "6f7be8c0",
    "ee7bcf3e",
    "26a6e3b3",
    "26a6e3b3",
    "506420ee",
    "c8d4ec03",
    "bee1b11e",
    "d3f57589",
    "99d99038",
    "99d99038",
    "d4302f21",
    "af81240a",
    "b2b247ab",
    "69adaf89",
    "de32df62",
    "fb6db529",
    "fb6db529",
    "fb6db529",
    "fb6db529",
    "eecede7f",
    "ac733982",
    "f5faf79e",
    "9281297"
)

//test on i7-8700
fun main() {
    //310 ms
    timer("Init") {
        Crc32Cracker
    }

    //80 ms
    timer("${hashes.size} hashes") {
        hashes.forEach {
            println(Crc32Cracker.crack(it))
        }
    }
}

private inline fun timer(string: String = "", block: () -> Unit) {
    val start = System.currentTimeMillis()
    block()
    val end = System.currentTimeMillis()
    println("$string finish in ${end - start} ms")
}
