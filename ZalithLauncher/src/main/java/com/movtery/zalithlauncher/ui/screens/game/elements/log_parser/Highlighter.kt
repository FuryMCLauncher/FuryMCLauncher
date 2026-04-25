/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.ui.screens.game.elements.log_parser

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

/**
 * 所有日志级别规则
 */
private val allLevelRules by lazy {
    listOf(INFO, ERROR, DEBUG, WARN)
}

class LogHighlighter(
    val defaultColor: Color = Color.White,
    val timeColor: Color = Color(0xFF6E7C83),
    val stringColor: Color = Color(0xFF6AAB73),
    val numberColor: Color = Color(0xFFC67CBA),
    val packageColor: Color = Color(0xFFC67CBA),
    val linkColor: Color = Color(0xFFC67CBA)
) {
    fun highlight(logText: String): AnnotatedString {
        return runCatching {
            highlightInternal(logText)
        }.getOrElse {
            //一旦出现错误，需要使用默认颜色
            AnnotatedString(
                text = logText,
                spanStyles = listOf(
                    AnnotatedString.Range(
                        SpanStyle(color = defaultColor),
                        0,
                        logText.length
                    )
                )
            )
        }
    }

    fun highlightInternal(logText: String): AnnotatedString = buildAnnotatedString {
        var i = 0
        val n = logText.length
        var inStackTrace = false

        while (i < n) {
            //Java 异常堆栈
            if (isLineStart(logText, i) && isStackTraceStart(logText, i)) {
                inStackTrace = true
            }

            if (inStackTrace) {
                val lineEnd = logText.indexOf('\n', i).let { if (it == -1) n else it + 1 }
                withStyle(SpanStyle(color = stringColor)) {
                    append(logText.substring(i, lineEnd))
                }

                //判断是否退出堆栈
                if (lineEnd < n && !isStackTraceLine(logText, lineEnd)) {
                    inStackTrace = false
                }

                i = lineEnd
                continue
            }

            //字符串
            if (logText[i] == '"' || logText[i] == '\'') {
                fun styleString(): Boolean {
                    val quote = logText[i]
                    val end = findClosingQuote(logText, i, quote)

                    if (end != -1) {
                        withStyle(SpanStyle(color = stringColor)) {
                            append(logText.substring(i, end + 1))
                        }
                        i = end + 1
                        return true
                    }
                    return false
                }

                if (logText[i] == '\'') {
                    //单引号字符串开始前要求必须为空格
                    logText.getOrNull(i - 1)?.let { before ->
                        if (before == ' ' && styleString()) {
                            continue
                        }
                    }
                } else if (styleString()) {
                    continue
                }
            }

            //网站链接
            val linkMatch = matchWebLink(logText, i)
            if (linkMatch != null) {
                withStyle(SpanStyle(color = linkColor)) {
                    append(linkMatch)
                }
                i += linkMatch.length
                continue
            }

            //时间
            val timeMatch = matchTime(logText, i)
            if (timeMatch != null) {
                withStyle(SpanStyle(color = timeColor)) {
                    append(timeMatch)
                }
                i += timeMatch.length
                continue
            }

            //日志等级
            if (isLogLevel(logText, i)) {
                val level = matchLogLevel(logText, i)
                if (level != null) {
                    val rule = findLevelRule(level)
                    withStyle(
                        SpanStyle(
                            color = rule?.textColor ?: defaultColor,
                            background = rule?.backgroundColor ?: Color.Unspecified
                        )
                    ) {
                        append(level)
                    }
                    i += level.length
                    continue
                }
            }

            //数字
            if (isDigit(logText[i])) {
                val segment = scanNumericSegment(logText, i)
                if (segment != null) {
                    if (isIndependentNumber(logText, i, segment.length)) {
                        withStyle(SpanStyle(color = numberColor)) {
                            append(segment)
                        }
                    } else {
                        append(segment)
                    }
                    i += segment.length
                    continue
                }
            }

            //包名
            val packageName = scanPackageName(logText, i)
            if (packageName != null) {
                withStyle(SpanStyle(color = packageColor)) {
                    append(packageName)
                }
                i += packageName.length
                continue
            }

            append(logText[i])
            i++
        }
    }


    private val timePatterns = listOf(
        Regex("""\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(?:\.\d+)?Z"""), // ISO-8601
        Regex("""\d{4}-\d{2}-\d{2} \d{1,2}:\d{2}:\d{2}"""),
        Regex("""\d{1,2}:\d{2}:\d{2}(?:\.\d{1,3})?""")
    )

    private fun matchTime(text: String, start: Int): String? {
        for (r in timePatterns) {
            val m = r.find(text, start)
            if (m != null && m.range.first == start) return m.value
        }
        return null
    }


    private fun isLineStart(text: String, i: Int): Boolean =
        i == 0 || text[i - 1] == '\n'

    private fun isStackTraceStart(text: String, i: Int): Boolean =
        text.startsWith("Caused by:", i) ||
                text.startsWith("\tat ", i) ||
                text.startsWith("    at ", i) ||
                isExceptionDeclaration(text, i)

    private fun isStackTraceLine(text: String, i: Int): Boolean =
        text.startsWith("\tat ", i) ||
                text.startsWith("    at ", i) ||
                text.startsWith("Caused by:", i)

    private fun isExceptionDeclaration(text: String, start: Int): Boolean {
        var i = start
        while (i < text.length && (text[i].isLetterOrDigit() || text[i] == '.')) i++
        if (i <= start) return false
        val name = text.substring(start, i)
        return name.endsWith("Exception") || name.endsWith("Error")
    }

    /**
     * 匹配网站链接
     */
    private fun matchWebLink(
        text: String,
        start: Int
    ): String? {
        if (
            !text.startsWith("http://", start) &&
            !text.startsWith("https://", start)
        ) {
            return null
        }

        var i = start
        val n = text.length

        while (i < n) {
            val c = text[i]
            if (
                c.isWhitespace() ||
                c == '"' ||
                c == '\'' ||
                c == '<' ||
                c == '>' ||
                c == '(' ||
                c == ')'
            ) {
                break
            }
            i++
        }

        return if (i > start) {
            text.substring(start, i)
        } else {
            null
        }
    }

    /**
     * 查找结束引号，用于判断是否为完整字符串
     */
    private fun findClosingQuote(
        text: String,
        start: Int,
        quote: Char
    ): Int {
        var i = start + 1
        while (i < text.length) {
            when {
                //防跨行
                text[i] == '\n' -> return -1
                text[i] == quote && text[i - 1] != '\\' -> return i
            }
            i++
        }
        return -1
    }


    private fun isLogLevel(text: String, start: Int): Boolean =
        text[start].isLetter() && (start == 0 || isWordBoundary(text[start - 1]))

    private fun matchLogLevel(text: String, start: Int): String? {
        for (rule in allLevelRules) {
            for (id in rule.identifiers) {
                if (text.regionMatches(start, id, 0, id.length)) {
                    val end = start + id.length
                    if (end == text.length || isWordBoundary(text[end])) {
                        return id
                    }
                }
            }
        }
        return null
    }

    private fun findLevelRule(level: String): LogLevelRule? =
        allLevelRules.firstOrNull {
            it.identifiers.any { id -> id.equals(level, true) }
        }


    private fun scanNumericSegment(text: String, start: Int): String? {
        var i = start
        while (i < text.length && isDigit(text[i])) i++
        if (i == start) return null
        return text.substring(start, i)
    }

    private fun isIndependentNumber(text: String, start: Int, length: Int): Boolean {
        val end = start + length
        val before = if (start == 0) ' ' else text[start - 1]
        val after = if (end >= text.length) ' ' else text[end]

        //只有首尾是标点符号或空格才应该被高亮
        return !before.isLetterOrDigit() && !after.isLetterOrDigit()
    }

    private fun isWordBoundary(c: Char): Boolean = !(c.isLetterOrDigit() || c == '_')

    private fun isDigit(c: Char): Boolean = c in '0'..'9'


    private fun scanPackageName(text: String, start: Int): String? {
        val n = text.length
        var i = start

        //前边界必须是空格或行首
        val before = if (start == 0) ' ' else text[start - 1]
        if (before != ' ') return null

        if (i >= n || !text[i].isLetter()) return null

        var lastDotIndex = -1
        var dotCount = 0

        while (i < n) {
            val c = text[i]
            when {
                c.isLetterOrDigit() -> i++
                c == '.' -> {
                    if (lastDotIndex == i - 1) return null
                    dotCount++
                    lastDotIndex = i
                    i++
                    if (i >= n || !text[i].isLetter()) return null
                }
                else -> break
            }
        }

        if (dotCount == 0) return null

        val end = i
        //后边界也必须是空格或行尾
        val after = if (end >= n) ' ' else text[end]
        if (after != ' ') return null

        return text.substring(start, end)
    }
}