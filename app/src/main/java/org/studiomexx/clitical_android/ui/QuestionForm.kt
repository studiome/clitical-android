package org.studiomexx.clitical_android.ui

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.studiomexx.clitical_android.Activity
import org.studiomexx.clitical_android.CKD
import org.studiomexx.clitical_android.MalignantNeoplasm
import org.studiomexx.clitical_android.MainViewModel
import org.studiomexx.clitical_android.R
import org.studiomexx.clitical_android.RutherfordClassification
import org.studiomexx.clitical_android.Sex
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionForm(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val locale = viewModel.locale

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(localizedString(R.string.riskAssessmentTab, locale)) })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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
                    selectedOption = viewModel.sex,
                    onOptionSelected = { viewModel.sex = it },
                    locale = locale
                )
            }
            item {
                TextEditRow(
                    label = localizedString(R.string.questionHeightTitle, locale),
                    value = viewModel.heightText,
                    onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d{0,3}(\\.\\d{0,1})?$"))) viewModel.heightText = it },
                    isDecimal = true
                )
            }
            item {
                TextEditRow(
                    label = localizedString(R.string.questionWeightTitle, locale),
                    value = viewModel.weightText,
                    onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d{0,3}(\\.\\d{0,1})?$"))) viewModel.weightText = it },
                    isDecimal = true
                )
            }

            item { SectionHeader(localizedString(R.string.socialHistory, locale)) }
            item {
                SwitchRow(
                    label = localizedString(R.string.questionSmokingTitle, locale),
                    checked = viewModel.isSmoking,
                    onCheckedChange = { viewModel.isSmoking = it }
                )
            }
            item {
                EnumChoiceRow(
                    label = localizedString(R.string.questionActivityTitle, locale),
                    options = Activity.entries,
                    selectedOption = viewModel.activity,
                    onOptionSelected = { viewModel.activity = it },
                    locale = locale
                )
            }

            item { SectionHeader(localizedString(R.string.clinicalInfo, locale)) }
            item {
                TextEditRow(
                    label = localizedString(R.string.questionAlbTitle, locale),
                    value = viewModel.albText,
                    onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d{0,2}(\\.\\d{0,1})?$"))) viewModel.albText = it },
                    isDecimal = true
                )
            }
            item {
                EnumChoiceRow(
                    label = localizedString(R.string.questionCKDTitle, locale),
                    options = CKD.entries,
                    selectedOption = viewModel.ckd,
                    onOptionSelected = { viewModel.ckd = it },
                    locale = locale
                )
            }
            item {
                SwitchRow(
                    label = localizedString(R.string.questionUrgentTitle, locale),
                    checked = viewModel.isUrgent,
                    onCheckedChange = { viewModel.isUrgent = it }
                )
            }
            item {
                SwitchRow(
                    label = localizedString(R.string.questionFeverTitle, locale),
                    checked = viewModel.hasFever,
                    onCheckedChange = { viewModel.hasFever = it }
                )
            }
            item {
                SwitchRow(
                    label = localizedString(R.string.questionAbnormalWBCTitle, locale),
                    checked = viewModel.hasAbnormalWBC,
                    onCheckedChange = { viewModel.hasAbnormalWBC = it }
                )
            }
            item {
                SwitchRow(
                    label = localizedString(R.string.questionLocalInfectionTitle, locale),
                    checked = viewModel.hasLocalInfection,
                    onCheckedChange = { viewModel.hasLocalInfection = it }
                )
            }
            item {
                EnumChoiceRow(
                    label = localizedString(R.string.questionRutherfordTitle, locale),
                    options = RutherfordClassification.entries,
                    selectedOption = viewModel.rutherford,
                    onOptionSelected = { viewModel.rutherford = it },
                    locale = locale
                )
            }

            item { SectionHeader(localizedString(R.string.lesionInfo, locale)) }
            item {
                SwitchRow(
                    label = localizedString(R.string.questionAILesionTitle, locale),
                    checked = viewModel.hasAILesion,
                    onCheckedChange = { viewModel.hasAILesion = it }
                )
            }
            item {
                SwitchRow(
                    label = localizedString(R.string.questionFPLesionTitle, locale),
                    checked = viewModel.hasFPLesion,
                    onCheckedChange = { viewModel.hasFPLesion = it }
                )
            }
            item {
                SwitchRow(
                    label = localizedString(R.string.questionBKLesionTitle, locale),
                    checked = viewModel.hasBKLesion,
                    onCheckedChange = { viewModel.hasBKLesion = it }
                )
            }

            item { SectionHeader(localizedString(R.string.otherLesionInfo, locale)) }
            item {
                SwitchRow(
                    label = localizedString(R.string.questionContraTitle, locale),
                    checked = viewModel.hasContraLateralLesion,
                    onCheckedChange = { viewModel.hasContraLateralLesion = it }
                )
            }
            item {
                SwitchRow(
                    label = localizedString(R.string.questionOtherLesionTitle, locale),
                    checked = viewModel.hasOtherVD,
                    onCheckedChange = { viewModel.hasOtherVD = it }
                )
            }

            item { SectionHeader(localizedString(R.string.complications, locale)) }
            item {
                SwitchRow(
                    label = localizedString(R.string.questionCHFTitle, locale),
                    checked = viewModel.hasCHF,
                    onCheckedChange = { viewModel.hasCHF = it }
                )
            }
            item {
                SwitchRow(
                    label = localizedString(R.string.questionCADTitle, locale),
                    checked = viewModel.hasCAD,
                    onCheckedChange = { viewModel.hasCAD = it }
                )
            }
            item {
                SwitchRow(
                    label = localizedString(R.string.questionCVDTitle, locale),
                    checked = viewModel.hasCVD,
                    onCheckedChange = { viewModel.hasCVD = it }
                )
            }
            item {
                SwitchRow(
                    label = localizedString(R.string.questionDLTitle, locale),
                    checked = viewModel.hasDyslipidemia,
                    onCheckedChange = { viewModel.hasDyslipidemia = it }
                )
            }
            item {
                EnumChoiceRow(
                    label = localizedString(R.string.questionMalignantTitle, locale),
                    options = MalignantNeoplasm.entries,
                    selectedOption = viewModel.malignant,
                    onOptionSelected = { viewModel.malignant = it },
                    locale = locale
                )
            }

            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            val errorSource = viewModel.calculateRisk()
                            if (errorSource != null) {
                                val msgId = when (errorSource) {
                                    "LesionChoice" -> R.string.analysisLesionErrorMessage
                                    "NumberForm" -> R.string.analysisNullErrorMessage
                                    else -> R.string.analysisDefaultErrorMessage
                                }
                                Toast.makeText(context, context.getString(msgId), Toast.LENGTH_LONG).show()
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
    }
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
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.width(100.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (isDecimal) KeyboardType.Decimal else KeyboardType.Number
                ),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                )
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
    ListItem(
        headlineContent = { Text(label) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    )
}

@Composable
fun <T : Enum<T>> EnumChoiceRow(
    label: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    locale: Locale
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        ListItem(
            headlineContent = { Text(label) },
            supportingContent = {
                val labelRes = when (selectedOption) {
                    is Sex -> selectedOption.stringResId
                    is Activity -> selectedOption.stringResId
                    is CKD -> selectedOption.stringResId
                    is MalignantNeoplasm -> selectedOption.stringResId
                    is RutherfordClassification -> selectedOption.stringResId
                    else -> 0
                }
                Text(if (labelRes != 0) localizedString(labelRes, locale) else selectedOption.name)
            },
            modifier = Modifier.selectable(
                selected = false,
                onClick = { expanded = !expanded }
            )
        )
        if (expanded) {
            Column(modifier = Modifier.padding(start = 32.dp)) {
                options.forEach { option ->
                    val optionLabelRes = when (option) {
                        is Sex -> option.stringResId
                        is Activity -> option.stringResId
                        is CKD -> option.stringResId
                        is MalignantNeoplasm -> option.stringResId
                        is RutherfordClassification -> option.stringResId
                        else -> 0
                    }
                    val optionLabel = if (optionLabelRes != 0) localizedString(optionLabelRes, locale) else option.name

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (option == selectedOption),
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
                            onClick = {
                                onOptionSelected(option)
                                expanded = false
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(optionLabel)
                    }
                }
            }
        }
    }
}
