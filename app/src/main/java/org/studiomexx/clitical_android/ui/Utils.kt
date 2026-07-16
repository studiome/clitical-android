package org.studiomexx.clitical_android.ui

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

private fun Context.withLocale(base: Configuration, locale: Locale): Context {
    val configuration = Configuration(base)
    configuration.setLocale(locale)
    return createConfigurationContext(configuration)
}

/** Resolves [id] in [locale] from outside composition, e.g. a click handler. */
fun Context.localizedString(@StringRes id: Int, locale: Locale): String =
    withLocale(resources.configuration, locale).resources.getString(id)

@Composable
fun localizedString(@StringRes id: Int, locale: Locale): String {
    val context = LocalContext.current
    val baseConfiguration = LocalConfiguration.current
    val localizedContext = remember(context, baseConfiguration, locale) {
        context.withLocale(baseConfiguration, locale)
    }
    return localizedContext.resources.getString(id)
}
