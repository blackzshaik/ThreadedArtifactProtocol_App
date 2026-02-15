package com.blackzshaik.tap.utils

import androidx.compose.runtime.Composable
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

@Composable
fun String.toMarkDown(): String {
    val flavour =  CommonMarkFlavourDescriptor()
    val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(this)
    val html = HtmlGenerator(this, parsedTree, flavour).generateHtml()
    return html.replaceFirst("<p>","")
}