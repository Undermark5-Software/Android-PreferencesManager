package fr.simon.marquis.preferencesmanager.util

/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.util.Xml
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParser.*
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlSerializer

object XmlUtils {

    /**
     * Flatten a Map into an output stream as XML. The map can later be read
     * back with readMapXml().
     *
     * @param value The map to be flattened.
     * @param out Where to write the XML data.
     * @see .writeMapXml
     * @see .writeListXml
     *
     * @see .writeValueXml
     *
     * @see .readMapXml
     */
    @Throws(XmlPullParserException::class, java.io.IOException::class)
    fun writeMapXml(value: MutableMap<String, Any>?, out: OutputStream) {
        val serializer = FastXmlSerializer()
        serializer.setOutput(out, "utf-8")
        serializer.startDocument(null, true)
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true)
        writeMapXml(value, null, serializer)
        serializer.endDocument()
    }

    /**
     * Flatten a Map into an XmlSerializer. The map can later be read back with
     * readThisMapXml().
     *
     * @param value  The map to be flattened.
     * @param name Name attribute to include with this list's tag, or null for
     * none.
     * @param out  XmlSerializer to write the map into.
     * @see .writeMapXml
     * @see .writeListXml
     *
     * @see .writeValueXml
     *
     * @see .readMapXml
     */
    @Throws(XmlPullParserException::class, java.io.IOException::class)
    fun writeMapXml(value: Map<*, *>?, name: String?, out: XmlSerializer) {
        if (value == null) {
            out.startTag(null, "null")
            out.endTag(null, "null")
            return
        }

        val s = value.entries
        val i = s.iterator()

        out.startTag(null, "map")
        if (name != null) {
            out.attribute(null, "name", name)
        }

        while (i.hasNext()) {
            val e = i.next() as MutableMap.MutableEntry<*, *>
            writeValueXml(e.value, e.key as String, out)
        }

        out.endTag(null, "map")
    }

    /**
     * Flatten a List into an XmlSerializer. The list can later be read back
     * with readThisListXml().
     *
     * @param value  The list to be flattened.
     * @param name Name attribute to include with this list's tag, or null for
     * none.
     * @param out  XmlSerializer to write the list into.
     * @see .writeListXml
     * @see .writeMapXml
     *
     * @see .writeValueXml
     *
     * @see .readListXml
     */
    @Throws(XmlPullParserException::class, java.io.IOException::class)
    fun writeListXml(value: List<*>?, name: String?, out: XmlSerializer) {
        if (value == null) {
            out.startTag(null, "null")
            out.endTag(null, "null")
            return
        }

        out.startTag(null, "list")
        if (name != null) {
            out.attribute(null, "name", name)
        }

        val n = value.size
        var i = 0
        while (i < n) {
            writeValueXml(value[i], null, out)
            i++
        }

        out.endTag(null, "list")
    }

    @Throws(XmlPullParserException::class, java.io.IOException::class)
    fun writeSetXml(value: Set<*>?, name: String?, out: XmlSerializer) {
        if (value == null) {
            out.startTag(null, "null")
            out.endTag(null, "null")
            return
        }

        out.startTag(null, "set")
        if (name != null) {
            out.attribute(null, "name", name)
        }

        for (v in value) {
            writeValueXml(v, null, out)
        }

        out.endTag(null, "set")
    }

    /**
     * Flatten a byte[] into an XmlSerializer. The list can later be read back
     * with readThisByteArrayXml().
     *
     * @param byteArray  The byte array to be flattened.
     * @param name Name attribute to include with this array's tag, or null for
     * none.
     * @param out  XmlSerializer to write the array into.
     * @see .writeMapXml
     *
     * @see .writeValueXml
     */
    @Throws(XmlPullParserException::class, java.io.IOException::class)
    fun writeByteArrayXml(byteArray: ByteArray?, name: String?, out: XmlSerializer) {

        if (byteArray == null) {
            out.startTag(null, "null")
            out.endTag(null, "null")
            return
        }

        out.startTag(null, "byte-array")
        if (name != null) {
            out.attribute(null, "name", name)
        }

        val n = byteArray.size
        out.attribute(null, "num", n.toString())

        val sb = StringBuilder(byteArray.size * 2)
        for (i in 0 until n) {
            val b = byteArray[i].toInt()
            var h = b shr 4
            sb.append(if (h >= 10) 'a'.code + h - 10 else '0'.code + h)
            h = b and 0xff
            sb.append(if (h >= 10) 'a'.code + h - 10 else '0'.code + h)
        }

        out.text(sb.toString())

        out.endTag(null, "byte-array")
    }

    /**
     * Flatten an int[] into an XmlSerializer. The list can later be read back
     * with readThisIntArrayXml().
     *
     * @param value  The int array to be flattened.
     * @param name Name attribute to include with this array's tag, or null for
     * none.
     * @param out  XmlSerializer to write the array into.
     * @see .writeMapXml
     *
     * @see .writeValueXml
     *
     * @see .readThisIntArrayXml
     */
    @Throws(XmlPullParserException::class, java.io.IOException::class)
    fun writeIntArrayXml(value: IntArray?, name: String?, out: XmlSerializer) {

        if (value == null) {
            out.startTag(null, "null")
            out.endTag(null, "null")
            return
        }

        out.startTag(null, "int-array")
        if (name != null) {
            out.attribute(null, "name", name)
        }

        val n = value.size
        out.attribute(null, "num", n.toString())

        for (i in 0 until n) {
            out.startTag(null, "item")
            out.attribute(null, "value", value[i].toString())
            out.endTag(null, "item")
        }

        out.endTag(null, "int-array")
    }

    /**
     * Flatten an object's value into an XmlSerializer. The value can later be
     * read back with readThisValueXml().
     *
     *
     * Currently supported value types are: null, String, Integer, Long, Float,
     * Double Boolean, Map, List.
     *
     * @param v    The object to be flattened.
     * @param name Name attribute to include with this value's tag, or null for
     * none.
     * @param out  XmlSerializer to write the object into.
     * @see .writeMapXml
     *
     * @see .writeListXml
     *
     * @see .readValueXml
     */
    @Throws(XmlPullParserException::class, java.io.IOException::class)
    fun writeValueXml(v: Any?, name: String?, out: XmlSerializer) {
        val typeStr: String
        when (v) {
            null -> {
                out.startTag(null, "null")
                if (name != null) {
                    out.attribute(null, "name", name)
                }
                out.endTag(null, "null")
                return
            }
            is String -> {
                out.startTag(null, "string")
                if (name != null) {
                    out.attribute(null, "name", name)
                }
                out.text(v.toString())
                out.endTag(null, "string")
                return
            }
            is Int -> typeStr = "int"
            is Long -> typeStr = "long"
            is Float -> typeStr = "float"
            is Double -> typeStr = "double"
            is Boolean -> typeStr = "boolean"
            is ByteArray -> {
                writeByteArrayXml(v as ByteArray?, name, out)
                return
            }
            is IntArray -> {
                writeIntArrayXml(v as IntArray?, name, out)
                return
            }
            is Map<*, *> -> {
                writeMapXml(v as Map<*, *>?, name, out)
                return
            }
            is List<*> -> {
                writeListXml(v as List<*>?, name, out)
                return
            }
            is Set<*> -> {
                writeSetXml(v as Set<*>?, name, out)
                return
            }
            is CharSequence -> {
                // XXX This is to allow us to at least write something if
                // we encounter styled text... but it means we will drop all
                // of the styling information. :(
                out.startTag(null, "string")
                if (name != null) {
                    out.attribute(null, "name", name)
                }
                out.text(v.toString())
                out.endTag(null, "string")
                return
            }
            else -> throw RuntimeException("writeValueXml: unable to write value $v")
        }

        out.startTag(null, typeStr)
        if (name != null) {
            out.attribute(null, "name", name)
        }
        out.attribute(null, "value", v.toString())
        out.endTag(null, typeStr)
    }

    /**
     * Read a HashMap from an InputStream containing XML. The stream can
     * previously have been written by writeMapXml().
     *
     * @param in The InputStream from which to read.
     * @return HashMap The resulting map.
     * @see .readListXml
     *
     * @see .readValueXml
     *
     * @see .readThisMapXml .see .writeMapXml
     */
    @Suppress("Unchecked_Cast")
    @Throws(XmlPullParserException::class, java.io.IOException::class)
    fun readMapXml(`in`: InputStream): HashMap<String, Any>? {
        val parser = Xml.newPullParser()
        parser.setInput(`in`, null)
        return readValueXml(parser, arrayOfNulls(1)) as HashMap<String, Any>?
    }

    /**
     * Read a HashMap object from an XmlPullParser. The XML data could
     * previously have been generated by writeMapXml(). The XmlPullParser must
     * be positioned *after* the tag that begins the map.
     *
     * @param parser The XmlPullParser from which to read the map data.
     * @param endTag Name of the tag that will end the map, usually "map".
     * @param name   An array of one string, used to return the name attribute of
     * the map's tag.
     * @return HashMap The newly generated map.
     * @see .readMapXml
     */
    @Throws(XmlPullParserException::class, java.io.IOException::class)
    fun readThisMapXml(parser: XmlPullParser, endTag: String, name: Array<String?>): HashMap<*, *> {
        val map = HashMap<Any, Any>()

        var eventType = parser.eventType
        do {
            if (eventType == START_TAG) {
                val value = readThisValueXml(parser, name)
                if (name[0] != null) {
                    // System.out.println("Adding to map: " + name + " -> " + val);
                    map[name[0]!!] = value!!
                } else {
                    throw XmlPullParserException("Map value without name attribute: " + parser.name)
                }
            } else if (eventType == END_TAG) {
                if (parser.name == endTag) {
                    return map
                }
                throw XmlPullParserException("Expected " + endTag + " end tag at: " + parser.name)
            }
            eventType = parser.next()
        } while (eventType != END_DOCUMENT)

        throw XmlPullParserException("Document ended before $endTag end tag")
    }

    /**
     * Read an ArrayList object from an XmlPullParser. The XML data could
     * previously have been generated by writeListXml(). The XmlPullParser must
     * be positioned *after* the tag that begins the list.
     *
     * @param parser The XmlPullParser from which to read the list data.
     * @param endTag Name of the tag that will end the list, usually "list".
     * @param name   An array of one string, used to return the name attribute of
     * the list's tag.
     * @return HashMap The newly generated list.
     * @see .readListXml
     */
    @Throws(XmlPullParserException::class, java.io.IOException::class)
    fun readThisListXml(parser: XmlPullParser, endTag: String, name: Array<String?>): ArrayList<*> {
        val list = ArrayList<Any>()

        var eventType = parser.eventType
        do {
            if (eventType == START_TAG) {
                val value = readThisValueXml(parser, name)
                list.add(value!!)
                // System.out.println("Adding to list: " + val);
            } else if (eventType == END_TAG) {
                if (parser.name == endTag) {
                    return list
                }
                throw XmlPullParserException("Expected " + endTag + " end tag at: " + parser.name)
            }
            eventType = parser.next()
        } while (eventType != END_DOCUMENT)

        throw XmlPullParserException("Document ended before $endTag end tag")
    }

    /**
     * Read a HashSet object from an XmlPullParser. The XML data could
     * previously have been generated by writeSetXml(). The XmlPullParser must
     * be positioned *after* the tag that begins the set.
     *
     * @param parser The XmlPullParser from which to read the set data.
     * @param endTag Name of the tag that will end the set, usually "set".
     * @param name   An array of one string, used to return the name attribute of
     * the set's tag.
     * @return HashSet The newly generated set.
     * @throws XmlPullParserException
     * @throws java.io.IOException
     * @see .readSetXml
     */
    @Throws(XmlPullParserException::class, java.io.IOException::class)
    fun readThisSetXml(parser: XmlPullParser, endTag: String, name: Array<String?>): HashSet<*> {
        val set = HashSet<Any>()

        var eventType = parser.eventType
        do {
            if (eventType == START_TAG) {
                val value = readThisValueXml(parser, name)
                set.add(value!!)
                // System.out.println("Adding to set: " + val);
            } else if (eventType == END_TAG) {
                if (parser.name == endTag) {
                    return set
                }
                throw XmlPullParserException("Expected " + endTag + " end tag at: " + parser.name)
            }
            eventType = parser.next()
        } while (eventType != END_DOCUMENT)

        throw XmlPullParserException("Document ended before $endTag end tag")
    }

    /**
     * Read an int[] object from an XmlPullParser. The XML data could previously
     * have been generated by writeIntArrayXml(). The XmlPullParser must be
     * positioned *after* the tag that begins the list.
     *
     * @param parser The XmlPullParser from which to read the list data.
     * @param endTag Name of the tag that will end the list, usually "list".
     * @param name   An array of one string, used to return the name attribute of
     * the list's tag.
     * @return Returns a newly generated int[].
     * @see .readListXml
     */
    @Suppress("Unused_Parameter")
    @Throws(XmlPullParserException::class, java.io.IOException::class)
    fun readThisIntArrayXml(parser: XmlPullParser, endTag: String, name: Array<String?>): IntArray {

        val num: Int
        try {
            num = Integer.parseInt(parser.getAttributeValue(null, "num"))
        } catch (e: NullPointerException) {
            throw XmlPullParserException("Need num attribute in byte-array")
        } catch (e: NumberFormatException) {
            throw XmlPullParserException("Not a number in num attribute in byte-array")
        }

        val array = IntArray(num)
        var i = 0

        var eventType = parser.eventType
        do {
            if (eventType == START_TAG) {
                if (parser.name == "item") {
                    try {
                        array[i] = Integer.parseInt(parser.getAttributeValue(null, "value"))
                    } catch (e: NullPointerException) {
                        throw XmlPullParserException("Need value attribute in item")
                    } catch (e: NumberFormatException) {
                        throw XmlPullParserException("Not a number in value attribute in item")
                    }
                } else {
                    throw XmlPullParserException("Expected item tag at: " + parser.name)
                }
            } else if (eventType == END_TAG) {
                when (parser.name) {
                    endTag -> return array
                    "item" -> i++
                    else -> throw XmlPullParserException(
                        "Expected " + endTag + " end tag at: " + parser.name
                    )
                }
            }
            eventType = parser.next()
        } while (eventType != END_DOCUMENT)

        throw XmlPullParserException("Document ended before $endTag end tag")
    }

    /**
     * Read a flattened object from an XmlPullParser. The XML data could
     * previously have been written with writeMapXml(), writeListXml(), or
     * writeValueXml(). The XmlPullParser must be positioned *at* the tag
     * that defines the value.
     *
     * @param parser The XmlPullParser from which to read the object.
     * @param name   An array of one string, used to return the name attribute of
     * the value's tag.
     * @return Object The newly generated value object.
     * @see .readMapXml
     *
     * @see .readListXml
     *
     * @see .writeValueXml
     */
    @Throws(XmlPullParserException::class, java.io.IOException::class)
    fun readValueXml(parser: XmlPullParser, name: Array<String?>): Any? {
        var eventType = parser.eventType
        do {
            when (eventType) {
                START_TAG -> return readThisValueXml(parser, name)
                END_TAG -> throw XmlPullParserException("Unexpected end tag at: " + parser.name)
                TEXT -> throw XmlPullParserException("Unexpected text: " + parser.text)
                else -> eventType = parser.next()
            }
        } while (eventType != END_DOCUMENT)

        throw XmlPullParserException("Unexpected end of document")
    }

    @Throws(XmlPullParserException::class, java.io.IOException::class)
    private fun readThisValueXml(parser: XmlPullParser, name: Array<String?>): Any? {
        val valueName = parser.getAttributeValue(null, "name")
        val tagName = parser.name

        // System.out.println("Reading this value tag: " + tagName + ", name=" + valueName);

        val res: Any?

        when (tagName) {
            "null" -> res = null
            "string" -> {
                var value = ""
                var eventType: Int

                do {
                    eventType = parser.next()

                    when (eventType) {
                        END_TAG -> {
                            if (parser.name == "string") {
                                name[0] = valueName
                                // System.out.println("Returning value for " + valueName + ": " + value);
                                return value
                            }
                            throw XmlPullParserException(
                                "Unexpected end tag in <string>: " + parser.name
                            )
                        }
                        TEXT -> value += parser.text
                        START_TAG -> throw XmlPullParserException(
                            "Unexpected start tag in <string>: " + parser.name
                        )
                    }
                } while (eventType != END_DOCUMENT)

                throw XmlPullParserException("Unexpected end of document in <string>")
            }
            "int" -> res = Integer.parseInt(parser.getAttributeValue(null, "value"))
            "long" -> res = java.lang.Long.valueOf(parser.getAttributeValue(null, "value"))
            "float" -> res = parser.getAttributeValue(null, "value").toFloat()
            "double" -> res = parser.getAttributeValue(null, "value").toDouble()
            "boolean" -> res = java.lang.Boolean.valueOf(parser.getAttributeValue(null, "value"))
            "int-array" -> {
                parser.next()
                res = readThisIntArrayXml(parser, "int-array", name)
                name[0] = valueName
                // System.out.println("Returning value for " + valueName + ": " + res);
                return res
            }
            "map" -> {
                parser.next()
                res = readThisMapXml(parser, "map", name)
                name[0] = valueName
                // System.out.println("Returning value for " + valueName + ": " + res);
                return res
            }
            "list" -> {
                parser.next()
                res = readThisListXml(parser, "list", name)
                name[0] = valueName
                // System.out.println("Returning value for " + valueName + ": " + res);
                return res
            }
            "set" -> {
                parser.next()
                res = readThisSetXml(parser, "set", name)
                name[0] = valueName
                // System.out.println("Returning value for " + valueName + ": " + res);
                return res
            }
            else -> throw XmlPullParserException("Unknown tag: $tagName")
        }

        // Skip through to end tag.
        var eventType: Int

        do {
            eventType = parser.next()

            when (eventType) {
                END_TAG -> {
                    if (parser.name == tagName) {
                        name[0] = valueName
                        // System.out.println("Returning value for " + valueName + ": " + res);
                        return res
                    }
                    throw XmlPullParserException(
                        "Unexpected end tag in <" + tagName + ">: " + parser.name
                    )
                }
                TEXT -> throw XmlPullParserException(
                    "Unexpected text in <" + tagName + ">: " + parser.name
                )
                START_TAG -> throw XmlPullParserException(
                    "Unexpected start tag in <" + tagName + ">: " + parser.name
                )
            }
        } while (eventType != END_DOCUMENT)

        throw XmlPullParserException("Unexpected end of document in <$tagName>")
    }
}
