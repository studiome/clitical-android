package org.studiomexx.clitical_android.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
fun localizedString(id: Int, locale: Locale): String {
    val configuration = Configuration(LocalConfiguration.current)
    configuration.setLocale(locale)
    val localizedContext = LocalContext.current.createConfigurationContext(configuration)
    return localizedContext.resources.getString(id)
}
