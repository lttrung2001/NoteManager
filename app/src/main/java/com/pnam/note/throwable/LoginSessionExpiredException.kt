package com.pnam.note.throwable

import java.io.IOException

class LoginSessionExpiredException(private val _message: String? = null) : IOException(_message) {
    override val message: String?
        get() = _message ?: "Login session expired"
}