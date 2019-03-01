package com.hiczp.crc32crack

import java.util.*

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

    private val REVERSE_TABLE = IntArray(256) {
        CRC_TABLE.indexOfFirst { crcTableElement ->
            crcTableElement ushr 24 == it
        }
    }

    fun crack(hash: Int, start: Int = 1, end: Int = 2000000000): List<Long> {
        //get last 4 index
        var leftOperand = hash
        var rightOperand = 0xffffffff.toInt()
        val last4index = IntArray(4) {
            leftOperand = leftOperand xor rightOperand
            REVERSE_TABLE[leftOperand ushr (3 - it) * 8].also { index ->
                rightOperand = CRC_TABLE[index] ushr it * 8
            }
        }

        return Collections.emptyList()
    }
}
