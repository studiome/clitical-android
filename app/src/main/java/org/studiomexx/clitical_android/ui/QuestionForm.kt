package org.studiomexx.clitical_android.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.studiomexx.clitical_android.R
import org.studiomexx.clitical_android.model.Activity
import org.studiomexx.clitical_android.model.CKD
import org.studiomexx.clitical_android.model.Labeled
import org.studiomexx.clitical_android.model.MalignantNeoplasm
import org.studiomexx.clitical_android.model.RutherfordClassification
import org.studiomexx.clitical_android.model.Sex
import org.studiomexx.clitical_android.model.ValidationError
import org.studiomexx.clitical_android.ui.theme.CLiTICALAndroidTheme
import java.util.Locale

private val heightWeightPattern = Regex("^\\d{0,3}(\\.\\d{0,1})?$")
private val albuminPattern = Regex("^\\d{0,2}(\\.\\d{0,1})?$")

@Composable
fun QuestionForm(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val locale = viewModel.locale
    val patientData = viewModel.patientData
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(
                text = localizedString(R.string.questionFormTitle, locale),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        item { SectionTitle(localizedString(R.string.basicInfo, locale)) }
        item {
            SectionCard(
                rows = listOf(
                    {
                        TextEditRow(
                            label = localizedString(R.string.questionAgeTitle, locale),
                            value = viewModel.ageText,
                            onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() }) viewModel.ageText = it },
                            isDecimal = false
                        )
                    },
                    {
                        EnumChoiceRow(
                            label = localizedString(R.string.questionSexTitle, locale),
                            options = Sex.entries,
                            selectedOption = patientData.sex,
                            onOptionSelected = { sex -> viewModel.updatePatientData { it.copy(sex = sex) } },
                            locale = locale
                        )
                    },
                    {
                        TextEditRow(
                            label = localizedString(R.string.questionHeightTitle, locale),
                            value = viewModel.heightText,
                            onValueChange = { if (it.isEmpty() || it.matches(heightWeightPattern)) viewModel.heightText = it },
                            isDecimal = true
                        )
                    },
                    {
                        TextEditRow(
                            label = localizedString(R.string.questionWeightTitle, locale),
                            value = viewModel.weightText,
                            onValueChange = { if (it.isEmpty() || it.matches(heightWeightPattern)) viewModel.weightText = it },
                            isDecimal = true
                        )
                    }
                )
            )
        }

        item { SectionTitle(localizedString(R.string.socialHistory, locale)) }
        item {
            SectionCard(
                rows = listOf(
                    {
                        SwitchRow(
                            label = localizedString(R.string.questionSmokingTitle, locale),
                            checked = patientData.isSmoking,
                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(isSmoking = checked) } },
                            description = localizedString(R.string.questionSmokingDescription, locale)
                        )
                    },
                    {
                        EnumChoiceRow(
                            label = localizedString(R.string.questionActivityTitle, locale),
                            options = Activity.entries,
                            selectedOption = patientData.activity,
                            onOptionSelected = { activity -> viewModel.updatePatientData { it.copy(activity = activity) } },
                            locale = locale
                        )
                    }
                )
            )
        }

        item { SectionTitle(localizedString(R.string.clinicalInfo, locale)) }
        item {
            SectionCard(
                rows = listOf(
                    {
                        TextEditRow(
                            label = localizedString(R.string.questionAlbTitle, locale),
                            value = viewModel.albText,
                            onValueChange = { if (it.isEmpty() || it.matches(albuminPattern)) viewModel.albText = it },
                            isDecimal = true
                        )
                    },
                    {
                        EnumChoiceRow(
                            label = localizedString(R.string.questionCKDTitle, locale),
                            options = CKD.entries,
                            selectedOption = patientData.ckd,
                            onOptionSelected = { ckd -> viewModel.updatePatientData { it.copy(ckd = ckd) } },
                            locale = locale,
                            description = localizedString(R.string.questionCKDDescription, locale)
                        )
                    },
                    {
                        SwitchRow(
                            label = localizedString(R.string.questionUrgentTitle, locale),
                            checked = patientData.isUrgent,
                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(isUrgent = checked) } },
                            description = localizedString(R.string.questionUrgentDescription, locale)
                        )
                    },
                    {
                        SwitchRow(
                            label = localizedString(R.string.questionFeverTitle, locale),
                            checked = patientData.hasFever,
                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasFever = checked) } },
                            description = localizedString(R.string.questionFeverDescription, locale)
                        )
                    },
                    {
                        SwitchRow(
                            label = localizedString(R.string.questionAbnormalWBCTitle, locale),
                            checked = patientData.hasAbnormalWBC,
                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasAbnormalWBC = checked) } },
                            description = localizedString(R.string.questionAbnormalWBCDescription, locale)
                        )
                    },
                    {
                        SwitchRow(
                            label = localizedString(R.string.questionLocalInfectionTitle, locale),
                            checked = patientData.hasLocalInfection,
                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasLocalInfection = checked) } },
                            description = localizedString(R.string.questionLocalInfectionDescription, locale)
                        )
                    },
                    {
                        EnumChoiceRow(
                            label = localizedString(R.string.questionRutherfordTitle, locale),
                            options = RutherfordClassification.entries,
                            selectedOption = patientData.rutherford,
                            onOptionSelected = { rutherford -> viewModel.updatePatientData { it.copy(rutherford = rutherford) } },
                            locale = locale
                        )
                    }
                )
            )
        }

        item { SectionTitle(localizedString(R.string.lesionInfo, locale)) }
        item {
            SectionCard(
                rows = listOf(
                    {
                        SwitchRow(
                            label = localizedString(R.string.questionAILesionTitle, locale),
                            checked = patientData.hasAILesion,
                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasAILesion = checked) } }
                        )
                    },
                    {
                        SwitchRow(
                            label = localizedString(R.string.questionFPLesionTitle, locale),
                            checked = patientData.hasFPLesion,
                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasFPLesion = checked) } }
                        )
                    },
                    {
                        SwitchRow(
                            label = localizedString(R.string.questionBKLesionTitle, locale),
                            checked = patientData.hasBKLesion,
                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasBKLesion = checked) } }
                        )
                    }
                )
            )
        }

        item { SectionTitle(localizedString(R.string.otherLesionInfo, locale)) }
        item {
            SectionCard(
                rows = listOf(
                    {
                        SwitchRow(
                            label = localizedString(R.string.questionContraTitle, locale),
                            checked = patientData.hasContraLateralLesion,
                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasContraLateralLesion = checked) } },
                            description = localizedString(R.string.questionContraDescription, locale)
                        )
                    },
                    {
                        SwitchRow(
                            label = localizedString(R.string.questionOtherLesionTitle, locale),
                            checked = patientData.hasOtherVD,
                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasOtherVD = checked) } },
                            description = localizedString(R.string.questionOtherLesionDescription, locale)
                        )
                    }
                )
            )
        }

        item { SectionTitle(localizedString(R.string.complications, locale)) }
        item {
            SectionCard(
                rows = listOf(
                    {
                        SwitchRow(
                            label = localizedString(R.string.questionCHFTitle, locale),
                            checked = patientData.hasCHF,
                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasCHF = checked) } },
                            description = localizedString(R.string.questionCHFDescription, locale)
                        )
                    },
                    {
                        SwitchRow(
                            label = localizedString(R.string.questionCADTitle, locale),
                            checked = patientData.hasCAD,
                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasCAD = checked) } },
                            description = localizedString(R.string.questionCADDescription, locale)
                        )
                    },
                    {
                        SwitchRow(
                            label = localizedString(R.string.questionCVDTitle, locale),
                            checked = patientData.hasCVD,
                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasCVD = checked) } },
                            description = localizedString(R.string.questionCVDDescription, locale)
                        )
                    },
                    {
                        SwitchRow(
                            label = localizedString(R.string.questionDLTitle, locale),
                            checked = patientData.hasDyslipidemia,
                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasDyslipidemia = checked) } },
                            description = localizedString(R.string.questionDLDescription, locale)
                        )
                    },
                    {
                        EnumChoiceRow(
                            label = localizedString(R.string.questionMalignantTitle, locale),
                            options = MalignantNeoplasm.entries,
                            selectedOption = patientData.malignant,
                            onOptionSelected = { malignant -> viewModel.updatePatientData { it.copy(malignant = malignant) } },
                            locale = locale
                        )
                    }
                )
            )
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = {
                        val error = viewModel.calculateRisk()
                        if (error != null) {
                            val msgId = when (error) {
                                ValidationError.NO_LESION_SELECTED -> R.string.analysisLesionErrorMessage
                                ValidationError.EMPTY_FIELDS -> R.string.analysisNullErrorMessage
                            }
                            val message = context.localizedString(msgId, locale)
                            coroutineScope.launch { snackbarHostState.showSnackbar(message) }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Analytics, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(localizedString(R.string.predictRisks, locale))
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { viewModel.reset() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(localizedString(R.string.reset, locale))
                }
            }
        }
    }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TextEditRowPreview() {
    CLiTICALAndroidTheme { TextEditRow(label = "Age", value = "65", onValueChange = {}, isDecimal = false) }
}

@Preview(showBackground = true)
@Composable
private fun SwitchRowPreview() {
    CLiTICALAndroidTheme { SwitchRow(label = "Smoking", checked = true, onCheckedChange = {}) }
}

private val transparentListItemColors
    @Composable get() = ListItemDefaults.colors(containerColor = Color.Transparent)

@Composable
fun TextEditRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isDecimal: Boolean
) {
    ListItem(
        headlineContent = { Text(label) },
        trailingContent = {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                // The headline is a separate node, so the field needs its own label.
                modifier = Modifier
                    .width(110.dp)
                    .semantics { contentDescription = label },
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (isDecimal) KeyboardType.Decimal else KeyboardType.Number
                ),
                singleLine = true
            )
        },
        colors = transparentListItemColors
    )
}

@Composable
fun SwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    description: String? = null
) {
    // Toggling on the row rather than the Switch merges the two into one node, so the
    // switch is announced with its label and the whole row becomes a tap target.
    ListItem(
        headlineContent = { Text(label) },
        // The clinical definition of "yes/no" lives here as supporting text.
        supportingContent = description?.let {
            { Text(it, style = MaterialTheme.typography.bodySmall) }
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = null
            )
        },
        modifier = Modifier.toggleable(
            value = checked,
            onValueChange = onCheckedChange,
            role = Role.Switch
        ),
        colors = transparentListItemColors
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> EnumChoiceRow(
    label: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    locale: Locale,
    description: String? = null
) where T : Enum<T>, T : Labeled {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.width(180.dp)
        ) {
            OutlinedTextField(
                value = localizedString(selectedOption.stringResId, locale),
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
                    .semantics { contentDescription = label }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(localizedString(option.stringResId, locale)) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
