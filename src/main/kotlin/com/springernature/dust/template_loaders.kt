package com.springernature.dust

import java.io.File


fun loadFromFilesIn(baseDirectory: String): TemplateLoader =
    loadFromFilesIn(File(baseDirectory))

fun loadFromFilesIn(baseDirectory: File): TemplateLoader =
    fun(name: String) =
        File(baseDirectory, "$name.dust").readText()


fun loadFromResourcesIn(baseClasspathPackage: String): TemplateLoader {
    val resourceRoot = "/" + baseClasspathPackage.replace('.', '/')
    return fun(templateName: String) =
        ClassLoader.getSystemClassLoader().getResource("$resourceRoot/$templateName.dust")?.readText()
            ?:throw IllegalArgumentException("template $templateName not found")
}
