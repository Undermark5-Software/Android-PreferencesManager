package fr.simon.marquis.preferencesmanager.ui.components

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showToast(@StringRes res: Int) {
    showToast(text = getString(res))
}

fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}
