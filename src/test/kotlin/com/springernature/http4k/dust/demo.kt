package com.springernature.http4k.dust

import com.springernature.dust.Dust
import com.springernature.dust.loadFromFilesIn
import java.io.File

fun main(args: Array<String>) {
    val dust = Dust(cacheTemplates = false, loader = loadFromFilesIn(File("src/test/templates")))
    
    dust.openTemplates().use { templates ->
        val template = templates["email"]
        
        while (true) {
            println(template(mapOf(
                "reviewerName" to "Alice",
                "manuscriptTitle" to "What I Did on my Holidays",
                "editorName" to "Ed"
            )))
            Thread.sleep(1000)
        }
    }
}