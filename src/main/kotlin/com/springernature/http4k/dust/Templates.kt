package com.springernature.http4k.dust

interface Templates: AutoCloseable {
    operator fun get(templateName: String): Template
}
