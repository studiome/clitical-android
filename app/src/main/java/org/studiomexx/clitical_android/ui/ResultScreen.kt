package org.studiomexx.clitical_android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.studiomexx.clitical_android.R
import org.studiomexx.clitical_android.model.PatientRisk
import org.studiomexx.clitical_android.ui.theme.CLiTICALAndroidTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    risk: PatientRisk,
    onBack: () -> Unit,
    locale: Locale
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        localizedString(R.string.result, locale),
                        modifier = Modifier.semantics { heading() }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = localizedString(R.string.back, locale)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val notAvailable = localizedString(R.string.notAvailable, locale)

            // 30-day Amputation/Death
            val adVal = if (risk.predicted30DDeathOrAmputation.isNaN()) notAvailable else String.format(Locale.US, "%.1f%%", risk.predicted30DDeathOrAmputation * 100.0)
            InfoCard(
                title = localizedString(R.string.predicted30DAD, locale),
                subtitle = localizedString(R.string.predicted30DADDescription, locale),
                value = adVal
            )

            // 30-day MALE
            val maleVal = if (risk.predicted30DMALE.isNaN()) notAvailable else String.format(Locale.US, "%.1f%%", risk.predicted30DMALE * 100.0)
            InfoCard(
                title = localizedString(R.string.predicted30DMALE, locale),
                subtitle = localizedString(R.string.predicted30DMALEDescription, locale),
                value = maleVal
            )

            // 2-year OS
            val osVal = if (risk.predictedOS.isNaN()) notAvailable else String.format(Locale.US, "%.0f%%", risk.predictedOS * 100.0)
            val osRiskLabel = risk.osRisk?.let { localizedString(it.stringResId, locale) } ?: notAvailable
            InfoCard(
                title = localizedString(R.string.predicted2yrOS, locale),
                subtitle = localizedString(R.string.predicted2yrOSDescription, locale),
                value = osVal,
                extraLabel = osRiskLabel
            )

            // 2-year AFS
            val afsVal = if (risk.predictedAFS.isNaN()) notAvailable else String.format(Locale.US, "%.0f%%", risk.predictedAFS * 100.0)
            InfoCard(
                title = localizedString(R.string.predicted2yrAFS, locale),
                subtitle = localizedString(R.string.predicted2yrAFSDescription, locale),
                value = afsVal
            )

            // GNRI
            val gnriVal = if (risk.gnri.isNaN()) notAvailable else String.format(Locale.US, "%.1f", risk.gnri)
            val gnriRiskLabel = risk.gnriRisk?.let { localizedString(it.stringResId, locale) } ?: notAvailable
            InfoCard(
                title = localizedString(R.string.gnri, locale),
                subtitle = localizedString(R.string.gnriDesctiption, locale),
                value = gnriVal,
                extraLabel = gnriRiskLabel
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InfoCardPreview() {
    CLiTICALAndroidTheme {
        InfoCard(title = "GNRI", subtitle = "Geriatric Nutritional Risk Index", value = "101.3", extraLabel = "No Risk")
    }
}

@Composable
fun InfoCard(
    title: String,
    subtitle: String,
    value: String,
    extraLabel: String? = null
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Start)
                    .semantics { heading() }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (extraLabel != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = extraLabel,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
