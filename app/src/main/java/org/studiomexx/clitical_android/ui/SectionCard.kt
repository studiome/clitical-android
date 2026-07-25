package org.studiomexx.clitical_android.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.studiomexx.clitical_android.ui.theme.CLiTICALAndroidTheme

/**
 * Section title matching web's `.section-title` (question-form.scss / settings.scss):
 * title-small, bold, primary color, sitting above the card rather than inside it.
 */
@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)
            .semantics { heading() }
    )
}

/**
 * Section card matching web's `.section-card` (question-form.scss / settings.scss /
 * references.scss): surface-container-low background, 1px outline-variant border,
 * 16dp rounded corners, and an outline-variant divider between each row
 * (`.section-card > * + *`).
 */
@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    rows: List<@Composable () -> Unit>
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column {
            rows.forEachIndexed { index, row ->
                if (index > 0) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
                row()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SectionTitlePreview() {
    CLiTICALAndroidTheme { SectionTitle("Basic Info") }
}

@Preview(showBackground = true)
@Composable
private fun SectionCardPreview() {
    CLiTICALAndroidTheme {
        SectionCard(
            rows = listOf(
                { Text("Row 1", modifier = Modifier.padding(16.dp)) },
                { Text("Row 2", modifier = Modifier.padding(16.dp)) },
                { Text("Row 3", modifier = Modifier.padding(16.dp)) }
            )
        )
    }
}
