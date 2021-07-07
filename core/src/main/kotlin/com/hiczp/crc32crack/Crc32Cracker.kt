package com.hiczp.crc32crack

import com.hiczp.crc32crack.annotation.GenerateRainbowTable
import java.util.*

/**
 * https://github.com/zacyu/bilibili-helper/blob/master/src/js/libs/crc32.js
 */
@GenerateRainbowTable
class Crc32Cracker {
    companion object {
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
                        for (postfix in lookup(hashValue xor baseHash xor compute(prefix))) {
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
        @JvmStatic
        fun crack(hash: String) = crack(hash.toUInt(16).toInt())
    }
}

private val sizeTable = intArrayOf(9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Int.MAX_VALUE)
private val powTable = intArrayOf(1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000)

//optimized int to array
private fun Int.toArray(): IntArray {
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

private fun pow10(n: Int): Int = powTable[n]

private fun update(crc: Int, code: Int): Int = crc ushr 8 xor Crc32RainbowTable.crc32Table[crc xor code and 0xff]

private fun compute(input: Int): Int {
    var crc = 0
    input.toArray().forEach {
        crc = update(crc, it)
    }
    repeat(5) {
        crc = update(crc, 0)
    }
    return crc
}

private fun lookup(hash: Int): List<Int> {
    val candidates = LinkedList<Int>()
    val shortHash = hash ushr 16
    for (i in Crc32RainbowTable.shortHashBucketStarts[shortHash] until Crc32RainbowTable.shortHashBucketStarts[shortHash + 1]) {
        if (Crc32RainbowTable.rainbowTableHash[i] == hash) {
            candidates.add(Crc32RainbowTable.rainbowTableValue[i])
        }
    }
    return candidates
}
