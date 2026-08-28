package org.studiomexx.clitical_android.ui

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import org.studiomexx.clitical_android.IntendedUseDisclaimerStore
import org.studiomexx.clitical_android.R
import org.studiomexx.clitical_android.ui.theme.JsvsColor
import java.util.Locale

private data class DisclaimerPoint(val title: Int, val detail: Int)

private val disclaimerPoints = listOf(
    DisclaimerPoint(R.string.disclaimerIntendedUser, R.string.disclaimerIntendedUserBody),
    DisclaimerPoint(R.string.disclaimerNotADevice, R.string.disclaimerNotADeviceBody),
    DisclaimerPoint(R.string.disclaimerValues, R.string.disclaimerValuesBody),
    DisclaimerPoint(R.string.disclaimerPopulation, R.string.disclaimerPopulationBody),
    DisclaimerPoint(R.string.disclaimerResponsibility, R.string.disclaimerResponsibilityBody)
)

@Composable
fun IntendedUseGate(
    store: IntendedUseDisclaimerStore,
    locale: Locale,
    content: @Composable () -> Unit
) {
    var acknowledged by remember { mutableStateOf(store.isAcknowledged) }
    if (acknowledged) {
        content()
    } else {
        IntendedUseDisclaimerScreen(
            locale = locale,
            onAcknowledge = {
                store.acknowledge()
                acknowledged = true
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntendedUseDisclaimerScreen(locale: Locale, onAcknowledge: () -> Unit) {
    val context = LocalContext.current
    val acknowledgeLabel = localizedString(R.string.disclaimerAcknowledge, locale)
    val termsLabel = localizedString(R.string.disclaimerReadTerms, locale)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(localizedString(R.string.disclaimerTitle, locale)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onAcknowledge,
                    modifier = Modifier.fillMaxWidth().semantics {
                        contentDescription = acknowledgeLabel
                    }
                ) {
                    Text(acknowledgeLabel)
                }
                Text(
                    localizedString(R.string.disclaimerAcknowledgeFooter, locale),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = JsvsColor)
                Text(
                    localizedString(R.string.disclaimerHeadline, locale),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            disclaimerPoints.forEach { point ->
                SectionCard(
                    rows = listOf {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                localizedString(point.title, locale),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                localizedString(point.detail, locale),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                )
            }
            ListItem(
                headlineContent = { Text(termsLabel) },
                leadingContent = { Icon(Icons.Default.Description, contentDescription = null) },
                trailingContent = { Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier
                    .clickable {
                        CustomTabsIntent.Builder().build().launchUrl(
                            context,
                            "https://studiome.github.io/clitical-legal/terms/${if (locale.language == "ja") "ja" else "en"}/".toUri()
                        )
                    }
                    .semantics(mergeDescendants = true) {
                        contentDescription = termsLabel
                }
            )
            Spacer(modifier = Modifier.padding(bottom = 96.dp))
        }
    }
}
