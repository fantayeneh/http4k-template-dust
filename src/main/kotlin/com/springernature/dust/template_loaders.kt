package com.springernature.dust

import java.io.File
import kotlin.reflect.KClass


fun loadFromFilesIn(baseDirectory: String): TemplateLoader =
    loadFromFilesIn(File(baseDirectory))


fun loadFromFilesIn(baseDirectory: File): TemplateLoader =
    fun(name: String) =
        File(baseDirectory, "$name.dust").readText()


fun loadFromResourcesIn(pkg: Package) =
    loadFromResourcesIn(pkg.name)


fun loadFromResourcesIn(baseClasspathPackage: String): TemplateLoader {
    val resourceRoot = baseClasspathPackage.replace('.', '/')
    return fun(templateName: String) =
        ClassLoader.getSystemClassLoader().getResource("$resourceRoot/$templateName.dust")?.readText()
            ?: throw IllegalArgumentException("template $templateName not found")
}


inline fun <reified T : Any> loadFromResourcesOf(): TemplateLoader =
    loadFromResourcesOf(T::class.java)


fun loadFromResourcesOf(cls: Class<*>): TemplateLoader =
    fun(templateName: String) =
        cls.getResource("$templateName.dust")?.readText()
            ?: throw IllegalArgumentException("template $templateName not found")
