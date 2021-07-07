package com.hiczp.crc32crack.annotation.processor

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.jvm.jvmStatic

fun generateRainbowTableFile(packageName: String, className: String = "Crc32RainbowTable"): FileSpec {
    val crc32Polynomial = 0xedb88320.toInt()
    val crc32Table = IntArray(256) {
        var crc = it
        repeat(8) {
            crc = if (crc and 1 != 0) {
                crc ushr 1 xor crc32Polynomial
            } else {
                crc ushr 1
            }
        }
        crc
    }

    val rainbowTableHash = IntArray(100000)
    val rainbowTableValue = IntArray(100000)
    val shortHashBucketStarts: IntArray
    val fullHashCache = IntArray(100000)
    val shortHashBuckets = IntArray(65537)

    // initialize rainbow table
    fun update(crc: Int, code: Int) = crc ushr 8 xor crc32Table[crc xor code and 0xff]
    fun compute(input: Int): Int {
        var crc = 0
        input.toArray().forEach {
            crc = update(crc, it)
        }
        return crc
    }
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

    //generate class
    //should contain fields: crc32Table, rainbowTableHash, rainbowTableValue, shortHashBucketStarts
    return FileSpec.builder(packageName, className).addType(
        TypeSpec.classBuilder(className)
            .addModifiers(KModifier.INTERNAL)
            .addType(
                TypeSpec.companionObjectBuilder()
                    .addModifiers(KModifier.INTERNAL)
                    .addProperty(staticIntArrayProperty("crc32Table", crc32Table))
                    .addProperty(staticIntArrayProperty("rainbowTableHash", rainbowTableHash))
                    .addProperty(staticIntArrayProperty("rainbowTableValue", rainbowTableValue))
                    .addProperty(staticIntArrayProperty("shortHashBucketStarts", shortHashBucketStarts))
                    .build()
            ).build()
    ).build()
}

//do transform on original array
private inline fun IntArray.mapInPlace(transform: (Int) -> Int): IntArray {
    for (i in indices) {
        this[i] = transform(this[i])
    }
    return this
}

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

private fun staticIntArrayProperty(name: String, value: IntArray) = PropertySpec.builder(name, IntArray::class)
    .jvmStatic()
    .addModifiers(KModifier.INTERNAL)
    .initializer("intArrayOf(%L)", value.joinToString())
    .build()
