# CRC32 Cracker
If original content is a Integer(not unsigned int) so we can crack it's CRC32 checksum in very limited time.

# gradle
```groovy
compile group: 'com.hiczp', name: 'crc32-crack', version: '1.1'
```

# usage
Kotlin:

```kotlin
val originals = Crc32Cracker.crack("fb6db529")
println(originals)
```

```
[20293030]
```

```kotlin
val originals = Crc32Cracker.crack("b2b247ab")
println(originals)
```

```
[37890226, 95228767]
```

note that original number may not unique.

`Crc32Crack` need to generate crc32 rainbow table before crack, this operation cost some time.

If want to init `Crc32Cracker` eager, call the Object first:

```kotlin
Crc32Cracker
println(Crc32Cracker.crack("fb6db529"))
```

Init cost about 310ms and one hash cost less than 1ms(test on Intel i7-8700)

# License

GPL V3
