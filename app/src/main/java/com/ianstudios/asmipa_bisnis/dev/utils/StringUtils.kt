package com.ianstudios.asmipa_bisnis.dev.utils

class StringUtils {

    fun truncate(original: String, ellipsis: String, maxLength: Int): String {
        if (original.length > maxLength) {
            return original.substring(0, maxLength - ellipsis.length) + ellipsis
        }
        return original
    }
}