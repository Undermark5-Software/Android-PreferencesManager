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

import android.text.TextUtils
import fr.simon.marquis.preferencesmanager.util.PrefManager
import fr.simon.marquis.preferencesmanager.util.XmlUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import org.xmlpull.v1.XmlPullParserException
import timber.log.Timber

data class KeyValueIndex(
    var index: Int? = null,
    var key: String,
    var value: Any,
)

class PreferenceFile {

    private var mPreferences: MutableMap<String, Any>? = null
    private var mList: MutableList<KeyValueIndex>? = null
    lateinit var file: String

    var list: MutableList<KeyValueIndex>
        get() {
            if (mList == null) {
                mList = ArrayList()
            }
            return mList!!
        }
        set(mList) {
            this.mList = mList
            this.mPreferences = HashMap()

            mList.map {
                mPreferences!![it.key] = it.value
            }

            updateSort()
        }

    val isValid: Boolean
        get() {
            try {
                XmlUtils.readMapXml(ByteArrayInputStream(toXml().toByteArray()))
            } catch (e: Exception) {
                return false
            }

            return true
        }

    init {
        mPreferences = HashMap()
    }

    private fun setPreferences(map: HashMap<String, Any>?) {
        mPreferences = map
        mList = map?.map {
            KeyValueIndex(key = it.key, value = it.value)
        }?.toMutableList()
        updateSort()
    }

    fun toXml(): String {
        val out = ByteArrayOutputStream()
        try {
            XmlUtils.writeMapXml(mPreferences, out)
        } catch (exception: XmlPullParserException) {
            Timber.e(exception)
        } catch (exception: IOException) {
            Timber.e(exception)
        }

        return out.toString()
    }

    private fun updateValue(key: String, value: Any) {
        for (entry in mList!!) {
            if (entry.key == key) {
                entry.value = value

                break
            }
        }

        mPreferences!![key] = value
        updateSort()
    }

    private fun removeValue(key: String) {
        mPreferences!!.remove(key)

        for (entry in mList!!) {
            if (entry.key == key) {
                mList!!.remove(entry)

                break
            }
        }
    }

    private fun createAndAddValue(key: String, value: Any) {
        mList!!.add(0, KeyValueIndex(key = key, value = value))
        mPreferences!![key] = value

        updateSort()
    }

    fun add(previousKey: String?, newKey: String?, value: Any?, editMode: Boolean) {
        if (newKey.isNullOrBlank()) {
            return
        }

        if (!editMode) {
            if (mPreferences!!.containsKey(newKey)) {
                updateValue(newKey, value!!)
            } else {
                createAndAddValue(newKey, value!!)
            }
        } else {
            if (newKey == previousKey) {
                updateValue(newKey, value!!)
            } else {
                removeValue(previousKey!!)

                if (mPreferences!!.containsKey(newKey)) {
                    updateValue(newKey, value!!)
                } else {
                    createAndAddValue(newKey, value!!)
                }
            }
        }
    }

    private fun updateSort() {
        val sortType = PrefManager.keySortType
        val comparator = PreferenceComparator(EPreferencesSort.values()[sortType])

        Collections.sort(list, comparator)
    }

    companion object {

        fun fromXml(xml: String, file: String = ""): PreferenceFile {
            val preferenceFile = PreferenceFile()

            preferenceFile.file = file

            // Check for empty files
            if (TextUtils.isEmpty(xml) || xml.trim { it <= ' ' }.isEmpty()) {
                return preferenceFile
            }

            try {
                val bais = ByteArrayInputStream(xml.toByteArray())
                val map = XmlUtils.readMapXml(bais)
                bais.close()

                preferenceFile.setPreferences(map)

                return preferenceFile
            } catch (exception: XmlPullParserException) {
                Timber.e(exception)
            } catch (exception: IOException) {
                Timber.e(exception)
            }

            return preferenceFile
        }
    }
}
