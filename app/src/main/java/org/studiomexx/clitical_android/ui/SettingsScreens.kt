package org.studiomexx.clitical_android.ui

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import org.studiomexx.clitical_android.BuildConfig
import org.studiomexx.clitical_android.R
import java.util.Locale

@Composable
fun LanguageScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val locale = viewModel.locale
    Column(modifier = modifier.padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectable(
                    selected = (locale.language == "ja"),
                    onClick = { viewModel.locale = Locale.forLanguageTag("ja") }
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = (locale.language == "ja"),
                onClick = { viewModel.locale = Locale.forLanguageTag("ja") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(localizedString(R.string.ja, locale))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectable(
                    selected = (locale.language == "en"),
                    onClick = { viewModel.locale = Locale.forLanguageTag("en") }
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = (locale.language == "en"),
                onClick = { viewModel.locale = Locale.forLanguageTag("en") }
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
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(localizedString(R.string.tapToOpenLink, locale), style = MaterialTheme.typography.bodyMedium)
        HorizontalDivider()
        TextButton(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, "https://doi.org/10.1093/bjs/znab036".toUri())
                context.startActivity(intent)
            }
        ) {
            Text(
                "1. Miyata T. et al, Risk prediction model for early outcomes of revascularization for chronic limb-threatening ischaemia. Br J Surg. 2022 Oct 14;109(11):1123.",
                style = MaterialTheme.typography.bodySmall
            )
        }
        TextButton(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, "https://doi.org/10.1016/j.ejvs.2022.05.038".toUri())
                context.startActivity(intent)
            }
        ) {
            Text(
                "2. Miyata T. et al, Prediction Models for Two Year Overall Survival and Amputation Free Survival After Revascularisation for Chronic Limb Threatening Ischaemia. Eur J Vasc Endovasc Surg . 2022 Jun 7;S1078-5884(22)00340-9.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun AboutScreen(locale: Locale, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(modifier = modifier.padding(16.dp)) {
        Text("CLiTICAL", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Version: ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(localizedString(R.string.appLegalese, locale), style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(32.dp))
        TextButton(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, "https://studiome.github.io/clti_risk/".toUri())
                context.startActivity(intent)
            }
        ) {
            Text(localizedString(R.string.appTerms, locale))
        }
    }
}
