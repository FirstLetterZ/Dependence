package com.zpf.aaa.utils

data class TextRect(
    val text: String,
    val polygons: List<FloatArray>,
) {
    var drawInfo: RoundBorder? = null
    var select: Boolean = false
    var tempSelect: Boolean = false
}