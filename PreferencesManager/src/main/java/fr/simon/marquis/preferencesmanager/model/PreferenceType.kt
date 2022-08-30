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

// import fr.simon.marquis.preferencesmanager.R
//
// enum class PreferenceType(
//    val dialogTitleAdd: Int,
//    val dialogTitleEdit: Int,
//    private val mCardBackground: Int
// ) {
//
//    UNSUPPORTED(0, 0, R.drawable.card_unknown),
//    BOOLEAN(R.string.title_add_boolean, R.string.title_edit_boolean, R.drawable.card_purpleborder),
//    STRING (R.string.title_add_string, R.string.title_edit_string, R.drawable.card_greenborder),
//    INT    (R.string.title_add_int, R.string.title_edit_int, R.drawable.card_redborder),
//    FLOAT  (R.string.title_add_float, R.string.title_edit_float, R.drawable.card_navyborder),
//    LONG   (R.string.title_add_long, R.string.title_edit_long, R.drawable.card_tealborder),
//    STRINGSET(
//        R.string.title_add_stringset,
//        R.string.title_edit_stringset,
//        R.drawable.card_goldborder
//    );
//
//    val cardBackground: Int
//        get() = mCardBackground
//
//    companion object {
//
//        fun fromObject(obj: Any): PreferenceType {
//            return when (obj) {
//                is String -> STRING
//                is Int -> INT
//                is Long -> LONG
//                is Float -> FLOAT
//                is Boolean -> BOOLEAN
//                is Set<*> -> STRINGSET
//                else -> UNSUPPORTED
//            }
//        }
//
//        fun getDialogLayout(obj: Any): Int {
//            return fromObject(obj).cardBackground
//        }
//    }
// }
