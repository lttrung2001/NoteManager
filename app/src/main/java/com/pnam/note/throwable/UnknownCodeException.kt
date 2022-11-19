package com.pnam.note.throwable

import java.io.IOException

class UnknownCodeException(private val _message: String? = null) : IOException(_message) {
    override val message: String?
        get() = _message ?: "Unknown error"
}