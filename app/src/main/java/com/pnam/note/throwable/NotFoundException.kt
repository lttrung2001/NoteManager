package com.pnam.note.throwable

import java.io.IOException

class NotFoundException(private val _message: String? = null) : IOException(_message) {
    override val message: String
        get() = _message ?: "Not found"
}