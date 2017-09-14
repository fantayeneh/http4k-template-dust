package com.springernature.dust

interface Templates: AutoCloseable {
    operator fun get(templateName: String): Template
}
