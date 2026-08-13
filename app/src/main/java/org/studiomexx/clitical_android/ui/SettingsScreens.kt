package org.studiomexx.clitical_android.ui

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import org.studiomexx.clitical_android.BuildConfig
import org.studiomexx.clitical_android.R
import java.util.Locale

/**
 * Groups language, legal information, and app info in a single tab, mirroring the
 * iOS app's Settings tab (which likewise folds these settings-like items out of
 * the tab bar rather than giving each its own top-level destination).
 */
@Composable
fun SettingsScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val locale = viewModel.locale
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        SectionTitle(localizedString(R.string.language, locale))
        SectionCard(
            rows = listOf(
                { LanguagePicker(viewModel = viewModel) }
            )
        )

        SectionCard(
            modifier = Modifier.padding(top = 8.dp),
            rows = listOf(
                { LegalLinkRow(R.string.appTerms, "terms", locale) },
                { LegalLinkRow(R.string.appPrivacyPolicy, "privacy", locale) },
                { LegalLinkRow(R.string.appSupport, "support", locale) }
            )
        )

        SectionTitle(localizedString(R.string.about, locale))
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // The bare version number, as clitical-ios shows it: a "Version:"
            // label would be one more string to translate for no added meaning.
            // Merging the row keeps the number tied to the app name for a
            // screen reader, which is what the iOS list row does by default.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics(mergeDescendants = true) { }
            ) {
                Text("CLiTICAL", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    BuildConfig.VERSION_NAME,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(localizedString(R.string.aboutDescription, locale), style = MaterialTheme.typography.bodyMedium)
            Text(
                localizedString(R.string.aboutDataSource, locale),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                localizedString(R.string.aboutPrivacy, locale),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                localizedString(R.string.aboutDisclaimer, locale),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(localizedString(R.string.appLegalese, locale), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun LegalLinkRow(labelRes: Int, page: String, locale: Locale) {
    val context = LocalContext.current
    ListItem(
        headlineContent = { Text(localizedString(labelRes, locale)) },
        trailingContent = {
            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null)
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clickable {
            CustomTabsIntent.Builder().build()
                .launchUrl(context, legalPageUrl(page, locale).toUri())
        }
    )
}

private fun legalPageUrl(page: String, locale: Locale): String {
    val language = if (locale.language == "ja") "ja" else "en"
    return "https://studiome.github.io/clitical-legal/$page/$language/"
}

@Composable
private fun LanguagePicker(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val locale = viewModel.locale
    Column(modifier = modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectable(
                    selected = (locale.language == "ja"),
                    role = Role.RadioButton,
                    onClick = { viewModel.locale = Locale.forLanguageTag("ja") }
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = (locale.language == "ja"),
                onClick = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(localizedString(R.string.ja, locale))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectable(
                    selected = (locale.language == "en"),
                    role = Role.RadioButton,
                    onClick = { viewModel.locale = Locale.forLanguageTag("en") }
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = (locale.language == "en"),
                onClick = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(localizedString(R.string.en, locale))
        }
    }
}

@Composable
fun ReferencesScreen(locale: Locale, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Text(
            localizedString(R.string.tapToOpenLink, locale),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        SectionCard(
            rows = listOf(
                {
                    ListItem(
                        headlineContent = {
                            Text(
                                "1. Miyata T. et al, Risk prediction model for early outcomes of revascularization for chronic limb-threatening ischaemia. Br J Surg. 2022 Oct 14;109(11):1123.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        trailingContent = {
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null)
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier.clickable {
                            CustomTabsIntent.Builder().build()
                                .launchUrl(context, "https://doi.org/10.1093/bjs/znab036".toUri())
                        }
                    )
                },
                {
                    ListItem(
                        headlineContent = {
                            Text(
                                "2. Miyata T. et al, Prediction Models for Two Year Overall Survival and Amputation Free Survival After Revascularisation for Chronic Limb Threatening Ischaemia. Eur J Vasc Endovasc Surg . 2022 Jun 7;S1078-5884(22)00340-9.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        trailingContent = {
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null)
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier.clickable {
                            CustomTabsIntent.Builder().build()
                                .launchUrl(context, "https://doi.org/10.1016/j.ejvs.2022.05.038".toUri())
                        }
                    )
                }
            )
        )
    }
}
