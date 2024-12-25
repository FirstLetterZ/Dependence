package com.zpf.aaa.utils

import java.util.LinkedList
import kotlin.math.min

class SoundMarkHelper {
    private val chinesePinyinMap = HashMap<Char, Pair<Char, Int>>().apply {
        put('ā', Pair('a', 1))
        put('á', Pair('a', 2))
        put('ǎ', Pair('a', 3))
        put('à', Pair('a', 4))
        put('ē', Pair('e', 1))
        put('é', Pair('e', 2))
        put('ě', Pair('e', 3))
        put('è', Pair('e', 4))
        put('ī', Pair('i', 1))
        put('í', Pair('i', 2))
        put('ǐ', Pair('i', 3))
        put('ì', Pair('i', 4))
        put('ō', Pair('o', 1))
        put('ó', Pair('o', 2))
        put('ǒ', Pair('o', 3))
        put('ò', Pair('o', 4))
        put('ū', Pair('u', 1))
        put('ú', Pair('u', 2))
        put('ǔ', Pair('u', 3))
        put('ù', Pair('u', 4))
        put('u', Pair('u', 5))
        put('ǖ', Pair('v', 1))
        put('ǘ', Pair('v', 2))
        put('ǚ', Pair('v', 3))
        put('ǜ', Pair('v', 4))
        put('ü', Pair('v', 5))
    }

    private val chineseMarkMap = HashMap<Char, ChineseMarkEntry>()
    private val markBuilder = StringBuilder()
    private val textBuilder = StringBuilder()
    private val tempBuilder = StringBuilder()
    private val resultBuilder = StringBuilder()

    fun addChineseMarkInfo(text: String?, mark: String?) {
        if (text.isNullOrEmpty() || mark.isNullOrEmpty()) {
            return
        }
        tempBuilder.clear()
        val markList = LinkedList<String>()
        for (c in mark.toCharArray()) {
            if (c == ' ') {
                if (tempBuilder.isNotEmpty()) {
                    markList.add(tempBuilder.toString())
                    tempBuilder.clear()
                }
            } else {
                tempBuilder.append(c)
            }
        }
        if (tempBuilder.isNotEmpty()) {
            markList.add(tempBuilder.toString())
        }
        tempBuilder.clear()
        val markPairList = ArrayList<Pair<String, String?>>()
        var lastEntry: ChineseMarkEntry? = null
        var nextMark = markList.poll()
        var i = 0
        while (i < text.length) {
            val c = text[i]
            val cacheEntry = chineseMarkMap[c]
            val entry = cacheEntry ?: ChineseMarkEntry()
            if (i == 0) {
                chineseMarkMap[c] = entry
            }
            if (isChinese(c)) {
                if (tempBuilder.isNotEmpty()) {
                    val tempText = tempBuilder.toString()
                    if (tempText == nextMark) {
                        nextMark = markList.poll()
                    }
                    markPairList.add(Pair(tempText, null))
                    tempBuilder.clear()
                }
                markPairList.add(Pair(c.toString(), formatPinyinDisplay(nextMark, markBuilder)))
                nextMark = markList.poll()
            } else {
                tempBuilder.append(c)
            }
            lastEntry?.nextEntryMap?.put(c, entry)
            lastEntry = entry
            i++
        }
        if (tempBuilder.isNotEmpty()) {
            markPairList.add(Pair(tempBuilder.toString(), null))
            tempBuilder.clear()
        }
        if (lastEntry == null) {
            return
        }
        markBuilder.clear()
        textBuilder.clear()
        resultBuilder.clear()
        tempBuilder.clear()
        markPairList.forEachIndexed { index, data ->
            if (index > 0) {
                tempBuilder.append("<break time=\"0.1s\"></break>")
            }
            if (data.second.isNullOrEmpty()) {
                tempBuilder.append(data.first)
                if (textBuilder.isNotEmpty()) {
                    resultBuilder.append("<phoneme alphabet=\"py\" ph=\"${markBuilder}\">${textBuilder}</phoneme>")
                    markBuilder.clear()
                    textBuilder.clear()
                }
                resultBuilder.append(data.first)
            } else {
                tempBuilder.append("<phoneme alphabet=\"py\" ph=\"${data.second}\">${data.first}</phoneme>")
                textBuilder.append(data.first)
                if (markBuilder.isNotEmpty()) {
                    markBuilder.append(" ")
                }
                markBuilder.append(data.second)
            }
        }
        if (textBuilder.isNotEmpty()) {
            resultBuilder.append("<phoneme alphabet=\"py\" ph=\"${markBuilder}\">${textBuilder}</phoneme>")
            markBuilder.clear()
            textBuilder.clear()
        }
        lastEntry.totalSpeak = resultBuilder.toString()
        lastEntry.breakSpeak = tempBuilder.toString()
    }

    fun formatChinese(text: String?): String {
        if (text.isNullOrEmpty()) {
            return ""
        }
        if (chineseMarkMap.isEmpty()) {
            return text
        }
        resultBuilder.clear()
        tempBuilder.clear()
        var i = 0
        var lastEntry: ChineseMarkEntry? = null
        var tempEntry: ChineseMarkEntry?
        var shouldBreakSpeak = false
        while (i < text.length) {
            val ci = text[i]
            if (ci == '—') {
                shouldBreakSpeak = true
                tempBuilder.append(ci)
            } else {
                if (lastEntry == null) {
                    tempEntry = chineseMarkMap[ci]
                    shouldBreakSpeak = false
                } else {
                    tempEntry = lastEntry.nextEntryMap[ci]
                    if (tempEntry == null) {
                        val entryText = if (shouldBreakSpeak) {
                            lastEntry.breakSpeak
                        } else {
                            lastEntry.totalSpeak
                        }
                        if (entryText.isNullOrEmpty()) {
                            resultBuilder.append(tempBuilder.toString())
                        } else {
                            resultBuilder.append(entryText)
                        }
                        tempBuilder.clear()
                        tempEntry = chineseMarkMap[ci]
                        shouldBreakSpeak = false
                    }
                }
                if (tempEntry == null) {
                    resultBuilder.append(ci)
                } else {
                    tempBuilder.append(ci)
                }
                lastEntry = tempEntry
            }
            i++
        }
        val entryText = if (shouldBreakSpeak) {
            lastEntry?.breakSpeak
        } else {
            lastEntry?.totalSpeak
        }
        if (entryText.isNullOrEmpty()) {
            resultBuilder.append(tempBuilder.toString())
        } else {
            resultBuilder.append(entryText)
        }
        tempBuilder.clear()
        return resultBuilder.toString()
    }

    private fun formatPinyinDisplay(pinyin: String?, builder: StringBuilder): String? {
        if (pinyin.isNullOrEmpty()) {
            return null
        }
        builder.clear()
        var tone = 5
        var lastChar: Char? = null
        for (mc in pinyin.toCharArray()) {
            val charInfo =
                if (lastChar == 'j' || lastChar == 'q' || lastChar == 'x' || lastChar == 'y') {
                    when (mc) {
                        'ū' -> {
                            chinesePinyinMap['ǖ']
                        }
                        'ú' -> {
                            chinesePinyinMap['ǘ']
                        }
                        'ǔ' -> {
                            chinesePinyinMap['ǚ']
                        }
                        'ù' -> {
                            chinesePinyinMap['ǜ']
                        }
                        'u' -> {
                            chinesePinyinMap['ü']
                        }
                        else -> {
                            chinesePinyinMap[mc]
                        }
                    }
                } else {
                    chinesePinyinMap[mc]
                }
            if (charInfo != null) {
                tone = min(charInfo.second, tone)
                builder.append(charInfo.first)
            } else {
                builder.append(mc)
            }
            lastChar = mc
        }
        builder.append(tone)
        return builder.toString()
    }


    class ChineseMarkEntry {
        val nextEntryMap = HashMap<Char, ChineseMarkEntry>()
        var totalSpeak: String? = null
        var breakSpeak: String? = null
    }

    private fun isChinese(c: Char): Boolean {
        return (c in '\u4e00'..'\u9fa5')
    }
}