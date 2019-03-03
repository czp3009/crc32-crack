package com.hiczp.crc32crack

import java.util.*

/**
 * https://github.com/zacyu/bilibili-helper/blob/master/src/js/libs/crc32.js
 */
object Crc32Cracker {
    private const val CRC32_POLYNOMIAL = 0xedb88320.toInt()

    private val CRC_TABLE = IntArray(256) {
        var crc = it
        repeat(8) {
            crc = if (crc and 1 != 0) {
                crc ushr 1 xor CRC32_POLYNOMIAL
            } else {
                crc ushr 1
            }
        }
        crc
    }

    private val rainbowTableHash = IntArray(100000)
    private val rainbowTableValue = IntArray(100000)
    private val shortHashBucketStarts: IntArray

    init {
        val fullHashCache = IntArray(100000)
        val shortHashBuckets = IntArray(65537)
        // Initialize the rainbow Table
        repeat(100000) {
            val hash = compute(it)
            fullHashCache[it] = hash
            shortHashBuckets[hash ushr 16]++
        }
        var runningSum = 0
        shortHashBucketStarts = shortHashBuckets.mapInPlace {
            runningSum += it
            runningSum
        }
        repeat(100000) {
            val index = --shortHashBucketStarts[fullHashCache[it] ushr 16]
            rainbowTableHash[index] = fullHashCache[it]
            rainbowTableValue[index] = it
        }
    }

    private fun compute(input: Int, addPadding: Boolean = false): Int {
        var crc = 0
        input.toArray().forEach {
            crc = update(crc, it)
        }
        if (addPadding) {
            repeat(5) {
                crc = update(crc, 0)
            }
        }
        return crc
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun update(crc: Int, code: Int) =
        crc ushr 8 xor CRC_TABLE[crc xor code and 0xff]

    private fun lookup(hash: Int): List<Int> {
        val candidates = LinkedList<Int>()
        val shortHash = hash ushr 16
        for (i in shortHashBucketStarts[shortHash] until shortHashBucketStarts[shortHash + 1]) {
            if (rainbowTableHash[i] == hash) {
                candidates.add(rainbowTableValue[i])
            }
        }
        return candidates
    }

    /**
     * crack a crc32 checksum
     *
     * @param hash hash in Integer
     */
    @JvmStatic
    fun crack(hash: Int): List<Int> {
        val candidates = LinkedList<Int>()
        val hashValue = hash.inv()
        var baseHash = 0xffffffff.toInt()

        for (digitCount in 1 until 10) {
            baseHash = update(baseHash, 0x30)
            if (digitCount < 6) {
                // Direct lookup
                candidates.addAll(lookup(hashValue xor baseHash))
            } else {
                // Lookup with prefix
                val startPrefix = pow10(digitCount - 6)
                val endPrefix = pow10(digitCount - 5)

                for (prefix in startPrefix until endPrefix) {
                    for (postfix in lookup(hashValue xor baseHash xor compute(prefix, true))) {
                        candidates.add(prefix * 100000 + postfix)
                    }
                }
            }
        }

        return candidates
    }

    /**
     * crack a crc32 checksum
     *
     * @param hash hash in String
     */
    @UseExperimental(ExperimentalUnsignedTypes::class)
    @JvmStatic
    fun crack(hash: String) = Crc32Cracker.crack(hash.toUInt(16).toInt())
}

//do transform on original array
private inline fun IntArray.mapInPlace(transform: (Int) -> Int): IntArray {
    for (i in indices) {
        this[i] = transform(this[i])
    }
    return this
}

//java.lang.Integer
private val sizeTable = intArrayOf(9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Int.MAX_VALUE)

//optimized int to array
@Suppress("NOTHING_TO_INLINE")
private inline fun Int.toArray(): IntArray {
    var length = 0
    while (true) {
        if (this <= sizeTable[length]) {
            length++
            break
        }
        length++
    }

    val array = IntArray(length)
    var number = this
    for (i in array.lastIndex downTo 0) {
        array[i] = number % 10
        number /= 10
    }

    return array
}

//optimized 10.pow(n)
private val powTable = intArrayOf(1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000)

@Suppress("NOTHING_TO_INLINE")
private inline fun pow10(n: Int) = powTable[n]
