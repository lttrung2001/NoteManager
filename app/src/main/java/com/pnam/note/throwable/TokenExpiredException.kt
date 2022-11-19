package com.pnam.note.throwable

import java.io.IOException

class TokenExpiredException(private val _message: String? = null) : IOException(_message) {
    override val message: String?
        get() = _message ?: "Token expired"
}