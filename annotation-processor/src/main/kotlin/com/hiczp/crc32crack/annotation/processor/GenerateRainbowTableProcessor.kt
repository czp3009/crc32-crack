package com.hiczp.crc32crack.annotation.processor

import com.google.auto.service.AutoService
import com.hiczp.crc32crack.annotation.GenerateRainbowTable
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@SupportedAnnotationTypes("com.hiczp.crc32crack.annotation.GenerateRainbowTable")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
class GenerateRainbowTableProcessor : AbstractProcessor() {
    override fun process(annotations: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        roundEnvironment.getElementsAnnotatedWith(GenerateRainbowTable::class.java)
            .filter { it.kind == ElementKind.CLASS }
            .map { it.enclosingElement as PackageElement }
            .firstOrNull()
            ?.let { generateRainbowTableFile(it.qualifiedName.toString()) }
            ?.writeTo(processingEnv.filer)
        return true
    }
}
