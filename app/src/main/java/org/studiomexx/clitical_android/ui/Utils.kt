package org.studiomexx.clitical_android.ui

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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

/** Widest a column of forms and text gets on an unfolded foldable or tablet before it's hard to scan. */
val ReadableContentMaxWidth = 640.dp

/**
 * Caps the content at [ReadableContentMaxWidth] and centers it in the available width.
 * Apply after `verticalScroll` so the whole window, margins included, still scrolls.
 */
fun Modifier.readableWidth(): Modifier = layout { measurable, constraints ->
    val cap = ReadableContentMaxWidth.roundToPx()
    if (!constraints.hasBoundedWidth || constraints.maxWidth <= cap) {
        val placeable = measurable.measure(constraints)
        return@layout layout(placeable.width, placeable.height) { placeable.place(0, 0) }
    }
    val placeable = measurable.measure(
        constraints.copy(minWidth = constraints.minWidth.coerceAtMost(cap), maxWidth = cap)
    )
    val width = constraints.maxWidth
    layout(width, placeable.height) { placeable.place((width - placeable.width) / 2, 0) }
}

/** The [readableWidth] equivalent for lazy lists, which take their gutter as content padding. */
fun readableContentPadding(availableWidth: Dp): PaddingValues =
    PaddingValues(horizontal = ((availableWidth - ReadableContentMaxWidth) / 2).coerceAtLeast(0.dp))
