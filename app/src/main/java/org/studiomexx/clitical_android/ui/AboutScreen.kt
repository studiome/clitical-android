package org.studiomexx.clitical_android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.studiomexx.clitical_android.BuildConfig
import org.studiomexx.clitical_android.R
import java.util.Locale

private data class Prediction(val title: Int, val icon: ImageVector)

private val predictions = listOf(
    Prediction(R.string.predicted30DAD, Icons.Default.MonitorHeart),
    Prediction(R.string.predicted30DMALE, Icons.Default.FavoriteBorder),
    Prediction(R.string.predicted2yrOS, Icons.Default.FavoriteBorder),
    Prediction(R.string.predicted2yrAFS, Icons.Default.DirectionsWalk),
    Prediction(R.string.gnri, Icons.Default.Restaurant)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    locale: Locale,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(localizedString(R.string.about, locale)) },
                navigationIcon = onBack?.let {
                    {
                        IconButton(onClick = it) {
                            Icon(Icons.Default.ArrowBack, contentDescription = localizedString(R.string.back, locale))
                        }
                    }
                } ?: {}
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text("CLiTICAL", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                Text(
                    localizedString(R.string.aboutTagline, locale),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            AboutSection(R.string.aboutOverview, localizedString(R.string.aboutOverviewBody, locale), locale)
            AboutSection(R.string.aboutIntendedUse, localizedString(R.string.aboutIntendedUseBody, locale), locale)
            SectionTitle(localizedString(R.string.aboutPredictions, locale), modifier = Modifier.padding(horizontal = 0.dp))
            predictions.forEach { prediction ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp)) {
                    Icon(prediction.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        localizedString(prediction.title, locale),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }
            Text(
                localizedString(R.string.aboutPredictionsFooter, locale),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            AboutSection(R.string.aboutMethodology, localizedString(R.string.aboutMethodologyBody, locale), locale)
            AboutSection(R.string.aboutModelSource, localizedString(R.string.aboutModelSourceBody, locale), locale)
            AboutSection(R.string.aboutLimitations, localizedString(R.string.aboutLimitationsBody, locale), locale)
            AboutSection(R.string.aboutPrivacy, localizedString(R.string.aboutPrivacyBody, locale), locale)
            AboutSection(R.string.aboutDisclaimer, localizedString(R.string.aboutDisclaimerBody, locale), locale)

            SectionTitle(localizedString(R.string.aboutCredits, locale), modifier = Modifier.padding(horizontal = 0.dp))
            CreditRow(R.string.aboutPublisher, localizedString(R.string.aboutPublisherName, locale), locale)
            CreditRow(R.string.aboutDeveloper, localizedString(R.string.aboutDeveloperName, locale), locale)
            CreditRow(R.string.aboutVersion, BuildConfig.VERSION_NAME, locale)
            CreditRow(R.string.aboutBuild, BuildConfig.VERSION_CODE.toString(), locale)
            Spacer(modifier = Modifier.padding(bottom = 8.dp))
        }
    }
}

@Composable
private fun AboutSection(title: Int, body: String, locale: Locale) {
    SectionTitle(localizedString(title, locale), modifier = Modifier.padding(horizontal = 0.dp))
    Text(body, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 4.dp))
}

@Composable
private fun CreditRow(label: Int, value: String, locale: Locale) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Text(localizedString(label, locale), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
