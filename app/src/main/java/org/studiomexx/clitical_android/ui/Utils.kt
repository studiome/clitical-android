package org.studiomexx.clitical_android.ui

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
fun localizedString(@StringRes id: Int, locale: Locale): String {
    val context = LocalContext.current
    val baseConfiguration = LocalConfiguration.current
    val localizedContext = remember(context, baseConfiguration, locale) {
        val configuration = Configuration(baseConfiguration)
        configuration.setLocale(locale)
        context.createConfigurationContext(configuration)
    }
    return localizedContext.resources.getString(id)
}
