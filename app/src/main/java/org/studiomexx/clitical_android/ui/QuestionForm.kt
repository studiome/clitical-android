package org.studiomexx.clitical_android.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.rotate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.tooling.preview.Preview
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
        item { SectionHeader(localizedString(R.string.basicInfo, locale)) }
        item {
            TextEditRow(
                label = localizedString(R.string.questionAgeTitle, locale),
                value = viewModel.ageText,
                onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() }) viewModel.ageText = it },
                isDecimal = false
            )
        }
        item {
            EnumChoiceRow(
                label = localizedString(R.string.questionSexTitle, locale),
                options = Sex.entries,
                selectedOption = patientData.sex,
                onOptionSelected = { sex -> viewModel.updatePatientData { it.copy(sex = sex) } },
                locale = locale
            )
        }
        item {
            TextEditRow(
                label = localizedString(R.string.questionHeightTitle, locale),
                value = viewModel.heightText,
                onValueChange = { if (it.isEmpty() || it.matches(heightWeightPattern)) viewModel.heightText = it },
                isDecimal = true
            )
        }
        item {
            TextEditRow(
                label = localizedString(R.string.questionWeightTitle, locale),
                value = viewModel.weightText,
                onValueChange = { if (it.isEmpty() || it.matches(heightWeightPattern)) viewModel.weightText = it },
                isDecimal = true
            )
        }

        item { SectionHeader(localizedString(R.string.socialHistory, locale)) }
        item {
            SwitchRow(
                label = localizedString(R.string.questionSmokingTitle, locale),
                checked = patientData.isSmoking,
                onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(isSmoking = checked) } }
            )
        }
        item {
            EnumChoiceRow(
                label = localizedString(R.string.questionActivityTitle, locale),
                options = Activity.entries,
                selectedOption = patientData.activity,
                onOptionSelected = { activity -> viewModel.updatePatientData { it.copy(activity = activity) } },
                locale = locale
            )
        }

        item { SectionHeader(localizedString(R.string.clinicalInfo, locale)) }
        item {
            TextEditRow(
                label = localizedString(R.string.questionAlbTitle, locale),
                value = viewModel.albText,
                onValueChange = { if (it.isEmpty() || it.matches(albuminPattern)) viewModel.albText = it },
                isDecimal = true
            )
        }
        item {
            EnumChoiceRow(
                label = localizedString(R.string.questionCKDTitle, locale),
                options = CKD.entries,
                selectedOption = patientData.ckd,
                onOptionSelected = { ckd -> viewModel.updatePatientData { it.copy(ckd = ckd) } },
                locale = locale
            )
        }
        item {
            SwitchRow(
                label = localizedString(R.string.questionUrgentTitle, locale),
                checked = patientData.isUrgent,
                onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(isUrgent = checked) } }
            )
        }
        item {
            SwitchRow(
                label = localizedString(R.string.questionFeverTitle, locale),
                checked = patientData.hasFever,
                onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasFever = checked) } }
            )
        }
        item {
            SwitchRow(
                label = localizedString(R.string.questionAbnormalWBCTitle, locale),
                checked = patientData.hasAbnormalWBC,
                onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasAbnormalWBC = checked) } }
            )
        }
        item {
            SwitchRow(
                label = localizedString(R.string.questionLocalInfectionTitle, locale),
                checked = patientData.hasLocalInfection,
                onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasLocalInfection = checked) } }
            )
        }
        item {
            EnumChoiceRow(
                label = localizedString(R.string.questionRutherfordTitle, locale),
                options = RutherfordClassification.entries,
                selectedOption = patientData.rutherford,
                onOptionSelected = { rutherford -> viewModel.updatePatientData { it.copy(rutherford = rutherford) } },
                locale = locale
            )
        }

        item { SectionHeader(localizedString(R.string.lesionInfo, locale)) }
        item {
            SwitchRow(
                label = localizedString(R.string.questionAILesionTitle, locale),
                checked = patientData.hasAILesion,
                onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasAILesion = checked) } }
            )
        }
        item {
            SwitchRow(
                label = localizedString(R.string.questionFPLesionTitle, locale),
                checked = patientData.hasFPLesion,
                onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasFPLesion = checked) } }
            )
        }
        item {
            SwitchRow(
                label = localizedString(R.string.questionBKLesionTitle, locale),
                checked = patientData.hasBKLesion,
                onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasBKLesion = checked) } }
            )
        }

        item { SectionHeader(localizedString(R.string.otherLesionInfo, locale)) }
        item {
            SwitchRow(
                label = localizedString(R.string.questionContraTitle, locale),
                checked = patientData.hasContraLateralLesion,
                onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasContraLateralLesion = checked) } }
            )
        }
        item {
            SwitchRow(
                label = localizedString(R.string.questionOtherLesionTitle, locale),
                checked = patientData.hasOtherVD,
                onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasOtherVD = checked) } }
            )
        }

        item { SectionHeader(localizedString(R.string.complications, locale)) }
        item {
            SwitchRow(
                label = localizedString(R.string.questionCHFTitle, locale),
                checked = patientData.hasCHF,
                onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasCHF = checked) } }
            )
        }
        item {
            SwitchRow(
                label = localizedString(R.string.questionCADTitle, locale),
                checked = patientData.hasCAD,
                onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasCAD = checked) } }
            )
        }
        item {
            SwitchRow(
                label = localizedString(R.string.questionCVDTitle, locale),
                checked = patientData.hasCVD,
                onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasCVD = checked) } }
            )
        }
        item {
            SwitchRow(
                label = localizedString(R.string.questionDLTitle, locale),
                checked = patientData.hasDyslipidemia,
                onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasDyslipidemia = checked) } }
            )
        }
        item {
            EnumChoiceRow(
                label = localizedString(R.string.questionMalignantTitle, locale),
                options = MalignantNeoplasm.entries,
                selectedOption = patientData.malignant,
                onOptionSelected = { malignant -> viewModel.updatePatientData { it.copy(malignant = malignant) } },
                locale = locale
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
private fun SectionHeaderPreview() {
    CLiTICALAndroidTheme { SectionHeader("Section") }
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

@Composable
fun SectionHeader(title: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
    }
}

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
        }
    )
}

@Composable
fun SwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    // Toggling on the row rather than the Switch merges the two into one node, so the
    // switch is announced with its label and the whole row becomes a tap target.
    ListItem(
        headlineContent = { Text(label) },
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
        )
    )
}

@Composable
fun <T> EnumChoiceRow(
    label: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    locale: Locale
) where T : Enum<T>, T : Labeled {
    var expanded by remember { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(if (expanded) 180f else 0f, label = "chevronRotation")

    Column {
        ListItem(
            headlineContent = { Text(label) },
            supportingContent = { Text(localizedString(selectedOption.stringResId, locale)) },
            trailingContent = {
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.rotate(chevronRotation)
                )
            },
            // The row expands the options rather than selecting anything itself.
            modifier = Modifier.clickable { expanded = !expanded }
        )
        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(start = 32.dp)) {
                options.forEach { option ->
                    // The row owns the click so the option is one target, not two.
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (option == selectedOption),
                                role = Role.RadioButton,
                                onClick = {
                                    onOptionSelected(option)
                                    expanded = false
                                }
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(localizedString(option.stringResId, locale))
                    }
                }
            }
        }
    }
}
