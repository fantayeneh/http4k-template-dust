package com.springernature.http4k.dust

import java.io.File

fun main(args: Array<String>) {
    val dust = Dust(cacheTemplates = false) { name ->
        File("src/test/templates/${name}.dust").readText()
    }
    val template = dust["email"]
    
    while(true) {
        println(template(mapOf(
            "reviewerName" to "Alice",
            "manuscriptTitle" to "What I Did on my Holidays",
            "editorName" to "Ed"
        )))
        Thread.sleep(1000)
    }
}
