package com.springernature.http4k.dust

import java.io.File


fun loadFromFilesIn(baseDirectory: File): (String) -> String =
    fun(name: String) =
        File(baseDirectory, "$name.dust").readText()
    
