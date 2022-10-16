/*
 * Copyright (C) 2013 Simon Marquis (http://www.simon-marquis.fr)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package fr.simon.marquis.preferencesmanager.model

import android.content.ContentResolver
import android.content.Context
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.text.TextUtils
import java.io.File

class AppEntry(
    val applicationInfo: ApplicationInfo,
    label: String = "",
    sortingValue: String? = null,
    isFavorite: Boolean = false,
    headerChar: Char = ' ',
    iconUri: Uri? = null,
) {
    constructor(applicationInfo: ApplicationInfo, context: Context) : this(applicationInfo) {
        loadLabels(context)
        buildIconUri(applicationInfo)
    }

    var label = label
        private set
    private val apkFile: File = File(applicationInfo.sourceDir)
    var sortingValue = sortingValue
        private set

    var isFavorite = isFavorite
        set(value) {
            field = value
            // IMPORTANT! also update the char used for sorting
            sortingValue = (if (value) " " else "") + label
            headerChar = formatChar(label)
        }
    var headerChar = headerChar
        private set

    var iconUri = iconUri
        private set

    private fun buildIconUri(info: ApplicationInfo) {
        val builder = Uri.Builder().apply {
            scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            authority(info.packageName)
            appendPath(info.icon.toString())
        }
        iconUri = builder.build()
    }

    /**
     * Generate the labels
     *
     * @param ctx .
     */
    private fun loadLabels(ctx: Context) {
        if (label.isEmpty()) {
            if (!apkFile.exists()) {
                label = applicationInfo.packageName
            } else {
                val pm = ctx.packageManager
                var label: CharSequence? = null
                if (pm != null) {
                    label = applicationInfo.loadLabel(pm)
                }
                this.label = label?.toString() ?: applicationInfo.packageName
            }

            // replace false spaces O_o
            label = label.replace("\\s".toRegex(), " ")
        }

        if (sortingValue == null)
            sortingValue = (if (isFavorite) " " else "") + label

        headerChar = formatChar(label)
    }

    /**
     * Generate a char from a string to index the entry
     *
     * @param s .
     * @return .
     */
    private fun formatChar(s: String): Char {
        if (isFavorite) {
            return '☆'
        }

        if (TextUtils.isEmpty(s)) {
            return '#'
        }

        val c = Character.toUpperCase(s[0])

        // Number
        if (c in '0'..'9') {
            return '#'
        }

        // Letter
        if (c in 'A'..'Z' || c in 'a'..'z') {
            return c
        }

        // Accented letter
        when (c) {
            'À', 'Á', 'Â', 'Ã', 'Ä' -> return 'A'
            'É', 'È', 'Ê', 'Ë' -> return 'E'
        }

        // Everything else
        return '#'
    }

    override fun toString(): String {
        return label
    }
}

//class AppEntry(val applicationInfo: ApplicationInfo, context: Context) {
//
//    /**
//     * File of the application
//     */
//    private val mApkFile: File
//
//    /**
//     * Label of the application
//     */
//    var label: String = ""
//        private set
//
//    /**
//     * Value used to sort the list of applications
//     */
//    var sortingValue: String? = null
//        private set
//
//    /**
//     * Detect if app is starred by user
//     */
//    private var isFavorite: Boolean = false
//
//    /**
//     * Char value used by indexed ListView
//     */
//    var headerChar: Char = ' '
//        private set
//
//    /**
//     * Uri of the app icon
//     */
//    var iconUri: Uri? = null
//        private set
//
//    init {
//        isFavorite = Utils.isFavorite(applicationInfo.packageName)
//        mApkFile = File(applicationInfo.sourceDir)
//        loadLabels(context)
//        buildIconUri(applicationInfo)
//    }
//
//    private fun buildIconUri(info: ApplicationInfo) {
//        val builder = Uri.Builder().apply {
//            scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
//            authority(info.packageName)
//            appendPath(info.icon.toString())
//        }
//        iconUri = builder.build()
//    }
//
//    fun setFavorite(isFavorite: Boolean) {
//        this.isFavorite = isFavorite
//        // IMPORTANT! also update the char used for sorting
//        sortingValue = (if (isFavorite) " " else "") + label
//        headerChar = formatChar(label)
//    }
//
//    /**
//     * Generate the labels
//     *
//     * @param ctx .
//     */
//    private fun loadLabels(ctx: Context) {
//        if (label.isEmpty()) {
//            if (!mApkFile.exists()) {
//                label = applicationInfo.packageName
//            } else {
//                val pm = ctx.packageManager
//                var label: CharSequence? = null
//                if (pm != null) {
//                    label = applicationInfo.loadLabel(pm)
//                }
//                this.label = label?.toString() ?: applicationInfo.packageName
//            }
//
//            // replace false spaces O_o
//            label = label.replace("\\s".toRegex(), " ")
//        }
//
//        if (sortingValue == null)
//            sortingValue = (if (isFavorite) " " else "") + label
//
//        headerChar = formatChar(label)
//    }
//
//    /**
//     * Generate a char from a string to index the entry
//     *
//     * @param s .
//     * @return .
//     */
//    private fun formatChar(s: String): Char {
//        if (isFavorite) {
//            return '☆'
//        }
//
//        if (TextUtils.isEmpty(s)) {
//            return '#'
//        }
//
//        val c = Character.toUpperCase(s[0])
//
//        // Number
//        if (c in '0'..'9') {
//            return '#'
//        }
//
//        // Letter
//        if (c in 'A'..'Z' || c in 'a'..'z') {
//            return c
//        }
//
//        // Accented letter
//        when (c) {
//            'À', 'Á', 'Â', 'Ã', 'Ä' -> return 'A'
//            'É', 'È', 'Ê', 'Ë' -> return 'E'
//        }
//
//        // Everything else
//        return '#'
//    }
//
//    override fun toString(): String {
//        return label
//    }
//}
