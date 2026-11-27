package org.studiomexx.clitical_android.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.launch
import org.studiomexx.clitical_android.R
import org.studiomexx.clitical_android.model.Activity
import org.studiomexx.clitical_android.model.CKD
import org.studiomexx.clitical_android.model.Labelled
import org.studiomexx.clitical_android.model.MalignantNeoplasm
import org.studiomexx.clitical_android.model.PatientData
import org.studiomexx.clitical_android.model.RutherfordClassification
import org.studiomexx.clitical_android.model.Sex
import org.studiomexx.clitical_android.model.ValidationError
import org.studiomexx.clitical_android.ui.theme.CLiTICALAndroidTheme
import java.util.Locale

/** Whole bounds without a trailing ".0", e.g. 100.0 -> "100". */
private fun formatBound(value: Double): String =
    if ((value % 1.0) == 0.0) value.toLong().toString() else value.toString()

private val heightWeightPattern = Regex("^\\d{0,3}(\\.\\d?)?$")
private val albuminPattern = Regex("^\\d{0,2}(\\.\\d?)?$")

/**
 * The form's list rows, emitted in declaration order. The scroll target of focusFirstInvalid is
 * the entry's ordinal, so adding, removing or reordering a row can't leave it pointing at the
 * wrong place.
 */
private enum class FormItem {
    SCREEN_TITLE,
    BASIC_INFO_TITLE, BASIC_INFO,
    SOCIAL_HISTORY_TITLE, SOCIAL_HISTORY,
    CLINICAL_INFO_TITLE, CLINICAL_INFO,
    LESION_INFO_TITLE, LESION_INFO,
    OTHER_LESION_INFO_TITLE, OTHER_LESION_INFO,
    COMPLICATIONS_TITLE, COMPLICATIONS,
    CLEAR_ACTION
}

@Composable
fun QuestionForm(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    val context = LocalContext.current
    val locale = viewModel.locale
    val patientData = viewModel.patientData
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val ageFocusRequester = remember { FocusRequester() }
    val heightFocusRequester = remember { FocusRequester() }
    val weightFocusRequester = remember { FocusRequester() }
    val albFocusRequester = remember { FocusRequester() }
    val lesionFocusRequester = remember { FocusRequester() }

    val ageError = when (viewModel.ageIssue) {
        FieldIssue.MISSING -> localizedString(R.string.questionAgeError, locale)
        FieldIssue.OUT_OF_RANGE -> localizedString(
            R.string.questionAgeRangeError, locale,
            PatientData.validAgeRange.first, PatientData.validAgeRange.last,
        )
        null -> null
    }
    // The height field is in cm, the model's range in metres.
    val heightError = when (viewModel.heightIssue) {
        FieldIssue.MISSING -> localizedString(R.string.questionHeightError, locale)
        FieldIssue.OUT_OF_RANGE -> localizedString(
            R.string.questionHeightRangeError, locale,
            formatBound(PatientData.validHeightRange.start * 100),
            formatBound(PatientData.validHeightRange.endInclusive * 100),
        )
        null -> null
    }
    val weightError = when (viewModel.weightIssue) {
        FieldIssue.MISSING -> localizedString(R.string.questionWeightError, locale)
        FieldIssue.OUT_OF_RANGE -> localizedString(
            R.string.questionWeightRangeError, locale,
            formatBound(PatientData.validWeightRange.start),
            formatBound(PatientData.validWeightRange.endInclusive),
        )
        null -> null
    }
    val albError = when (viewModel.albIssue) {
        FieldIssue.MISSING -> localizedString(R.string.questionAlbError, locale)
        FieldIssue.OUT_OF_RANGE -> localizedString(
            R.string.questionAlbRangeError, locale,
            formatBound(PatientData.validAlbRange.start),
            formatBound(PatientData.validAlbRange.endInclusive),
        )
        null -> null
    }
    val lesionError = if (
        viewModel.hasSubmitted &&
        !patientData.run { hasAILesion || hasFPLesion || hasBKLesion }
    ) localizedString(R.string.lesionSelectionError, locale) else null

    fun focusFirstInvalid() {
        val (formItem, focusRequester) = when {
            ageError != null -> Pair(FormItem.BASIC_INFO, ageFocusRequester)
            heightError != null -> Pair(FormItem.BASIC_INFO, heightFocusRequester)
            weightError != null -> Pair(FormItem.BASIC_INFO, weightFocusRequester)
            albError != null -> Pair(FormItem.CLINICAL_INFO, albFocusRequester)
            lesionError != null -> Pair(FormItem.LESION_INFO, lesionFocusRequester)
            else -> null
        } ?: return
        coroutineScope.launch {
            listState.animateScrollToItem(formItem.ordinal)
            withFrameNanos { }
            focusRequester.requestFocus()
        }
    }

    // React only to submits made while this form is shown. Without this, recreating the form
    // (fold/unfold, rotation, switching tabs) replays the last failed submit and steals focus.
    var handledAttempt by remember { mutableIntStateOf(viewModel.validationAttempt) }
    LaunchedEffect(viewModel.validationAttempt) {
        if (viewModel.validationAttempt == handledAttempt) return@LaunchedEffect
        handledAttempt = viewModel.validationAttempt
        if (viewModel.hasSubmitted) focusFirstInvalid()
        // Reported here rather than at the button, so the FAB's submits report the same way.
        val msgId = when (viewModel.validationError) {
            ValidationError.NO_LESION_SELECTED -> R.string.analysisLesionErrorMessage
            ValidationError.EMPTY_FIELDS -> R.string.analysisNullErrorMessage
            ValidationError.OUT_OF_RANGE -> R.string.analysisRangeErrorMessage
            null -> return@LaunchedEffect
        }
        snackbarHostState.showSnackbar(context.localizedString(msgId, locale))
    }

    BoxWithConstraints(modifier = modifier) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = readableContentPadding(maxWidth, bottom = PredictFabClearance),
        ) {
            FormItem.entries.forEach { formItem ->
                item(key = formItem.name) {
                    when (formItem) {
                        FormItem.SCREEN_TITLE -> ScreenTitle(localizedString(R.string.questionFormTitle, locale))

                        FormItem.BASIC_INFO_TITLE -> SectionTitle(localizedString(R.string.basicInfo, locale))
                        FormItem.BASIC_INFO -> {
                            SectionCard(
                                rows = listOf(
                                    {
                                        TextEditRow(
                                            label = localizedString(R.string.questionAgeTitle, locale),
                                            value = viewModel.ageText,
                                            // 3 digits cover the range; more could overflow Int and read as "enter age".
                                            onValueChange = { if ((it.length <= 3) && it.all { c -> c.isDigit() }) viewModel.ageText = it },
                                            isDecimal = false,
                                            errorMessage = ageError,
                                            focusRequester = ageFocusRequester,
                                        )
                                    },
                                    {
                                        EnumChoiceRow(
                                            label = localizedString(R.string.questionSexTitle, locale),
                                            options = Sex.entries,
                                            selectedOption = patientData.sex,
                                            onOptionSelected = { sex -> viewModel.updatePatientData { it.copy(sex = sex) } },
                                            locale = locale,
                                        )
                                    },
                                    {
                                        TextEditRow(
                                            label = localizedString(R.string.questionHeightTitle, locale),
                                            value = viewModel.heightText,
                                            onValueChange = { if (it.isEmpty() || it.matches(heightWeightPattern)) viewModel.heightText = it },
                                            isDecimal = true,
                                            errorMessage = heightError,
                                            focusRequester = heightFocusRequester,
                                        )
                                    },
                                    {
                                        TextEditRow(
                                            label = localizedString(R.string.questionWeightTitle, locale),
                                            value = viewModel.weightText,
                                            onValueChange = { if (it.isEmpty() || it.matches(heightWeightPattern)) viewModel.weightText = it },
                                            isDecimal = true,
                                            errorMessage = weightError,
                                            focusRequester = weightFocusRequester,
                                        )
                                    },
                                ),
                            )
                        }

                        FormItem.SOCIAL_HISTORY_TITLE -> SectionTitle(localizedString(R.string.socialHistory, locale))
                        FormItem.SOCIAL_HISTORY -> {
                            SectionCard(
                                rows = listOf(
                                    {
                                        SwitchRow(
                                            label = localizedString(R.string.questionSmokingTitle, locale),
                                            checked = patientData.isSmoking,
                                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(isSmoking = checked) } },
                                            description = localizedString(R.string.questionSmokingDescription, locale),
                                        )
                                    },
                                    {
                                        EnumChoiceRow(
                                            label = localizedString(R.string.questionActivityTitle, locale),
                                            options = Activity.entries,
                                            selectedOption = patientData.activity,
                                            onOptionSelected = { activity -> viewModel.updatePatientData { it.copy(activity = activity) } },
                                            locale = locale,
                                        )
                                    },
                                ),
                            )
                        }

                        FormItem.CLINICAL_INFO_TITLE -> SectionTitle(localizedString(R.string.clinicalInfo, locale))
                        FormItem.CLINICAL_INFO -> {
                            SectionCard(
                                rows = listOf(
                                    {
                                        TextEditRow(
                                            label = localizedString(R.string.questionAlbTitle, locale),
                                            value = viewModel.albText,
                                            onValueChange = { if (it.isEmpty() || it.matches(albuminPattern)) viewModel.albText = it },
                                            isDecimal = true,
                                            errorMessage = albError,
                                            focusRequester = albFocusRequester,
                                        )
                                    },
                                    {
                                        EnumChoiceRow(
                                            label = localizedString(R.string.questionCKDTitle, locale),
                                            options = CKD.entries,
                                            selectedOption = patientData.ckd,
                                            onOptionSelected = { ckd -> viewModel.updatePatientData { it.copy(ckd = ckd) } },
                                            locale = locale,
                                            description = localizedString(R.string.questionCKDDescription, locale),
                                        )
                                    },
                                    {
                                        SwitchRow(
                                            label = localizedString(R.string.questionUrgentTitle, locale),
                                            checked = patientData.isUrgent,
                                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(isUrgent = checked) } },
                                            description = localizedString(R.string.questionUrgentDescription, locale),
                                        )
                                    },
                                    {
                                        SwitchRow(
                                            label = localizedString(R.string.questionFeverTitle, locale),
                                            checked = patientData.hasFever,
                                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasFever = checked) } },
                                            description = localizedString(R.string.questionFeverDescription, locale),
                                        )
                                    },
                                    {
                                        SwitchRow(
                                            label = localizedString(R.string.questionAbnormalWBCTitle, locale),
                                            checked = patientData.hasAbnormalWBC,
                                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasAbnormalWBC = checked) } },
                                            description = localizedString(R.string.questionAbnormalWBCDescription, locale),
                                        )
                                    },
                                    {
                                        SwitchRow(
                                            label = localizedString(R.string.questionLocalInfectionTitle, locale),
                                            checked = patientData.hasLocalInfection,
                                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasLocalInfection = checked) } },
                                            description = localizedString(R.string.questionLocalInfectionDescription, locale),
                                        )
                                    },
                                    {
                                        EnumChoiceRow(
                                            label = localizedString(R.string.questionRutherfordTitle, locale),
                                            options = RutherfordClassification.entries,
                                            selectedOption = patientData.rutherford,
                                            onOptionSelected = { rutherford -> viewModel.updatePatientData { it.copy(rutherford = rutherford) } },
                                            locale = locale,
                                        )
                                    },
                                ),
                            )
                        }

                        FormItem.LESION_INFO_TITLE -> SectionTitle(localizedString(R.string.lesionInfo, locale))
                        FormItem.LESION_INFO -> {
                            SectionCard(
                                rows = listOf(
                                    {
                                        SwitchRow(
                                            label = localizedString(R.string.questionAILesionTitle, locale),
                                            checked = patientData.hasAILesion,
                                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasAILesion = checked) } },
                                            focusRequester = lesionFocusRequester,
                                        )
                                    },
                                    {
                                        SwitchRow(
                                            label = localizedString(R.string.questionFPLesionTitle, locale),
                                            checked = patientData.hasFPLesion,
                                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasFPLesion = checked) } },
                                        )
                                    },
                                    {
                                        SwitchRow(
                                            label = localizedString(R.string.questionBKLesionTitle, locale),
                                            checked = patientData.hasBKLesion,
                                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasBKLesion = checked) } },
                                        )
                                    },
                                    {
                                        lesionError?.let { message ->
                                            Text(
                                                text = message,
                                                color = MaterialTheme.colorScheme.error,
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp)
                                                    .semantics { error(message) },
                                            )
                                        }
                                    },
                                ),
                            )
                        }

                        FormItem.OTHER_LESION_INFO_TITLE -> SectionTitle(localizedString(R.string.otherLesionInfo, locale))
                        FormItem.OTHER_LESION_INFO -> {
                            SectionCard(
                                rows = listOf(
                                    {
                                        SwitchRow(
                                            label = localizedString(R.string.questionContraTitle, locale),
                                            checked = patientData.hasContralateralLesion,
                                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasContralateralLesion = checked) } },
                                            description = localizedString(R.string.questionContraDescription, locale),
                                        )
                                    },
                                    {
                                        SwitchRow(
                                            label = localizedString(R.string.questionOtherLesionTitle, locale),
                                            checked = patientData.hasOtherVD,
                                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasOtherVD = checked) } },
                                            description = localizedString(R.string.questionOtherLesionDescription, locale),
                                        )
                                    },
                                ),
                            )
                        }

                        FormItem.COMPLICATIONS_TITLE -> SectionTitle(localizedString(R.string.complications, locale))
                        FormItem.COMPLICATIONS -> {
                            SectionCard(
                                rows = listOf(
                                    {
                                        SwitchRow(
                                            label = localizedString(R.string.questionCHFTitle, locale),
                                            checked = patientData.hasCHF,
                                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasCHF = checked) } },
                                            description = localizedString(R.string.questionCHFDescription, locale),
                                        )
                                    },
                                    {
                                        SwitchRow(
                                            label = localizedString(R.string.questionCADTitle, locale),
                                            checked = patientData.hasCAD,
                                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasCAD = checked) } },
                                            description = localizedString(R.string.questionCADDescription, locale),
                                        )
                                    },
                                    {
                                        SwitchRow(
                                            label = localizedString(R.string.questionCVDTitle, locale),
                                            checked = patientData.hasCVD,
                                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasCVD = checked) } },
                                            description = localizedString(R.string.questionCVDDescription, locale),
                                        )
                                    },
                                    {
                                        SwitchRow(
                                            label = localizedString(R.string.questionDLTitle, locale),
                                            checked = patientData.hasDyslipidemia,
                                            onCheckedChange = { checked -> viewModel.updatePatientData { it.copy(hasDyslipidemia = checked) } },
                                            description = localizedString(R.string.questionDLDescription, locale),
                                        )
                                    },
                                    {
                                        EnumChoiceRow(
                                            label = localizedString(R.string.questionMalignantTitle, locale),
                                            options = MalignantNeoplasm.entries,
                                            selectedOption = patientData.malignant,
                                            onOptionSelected = { malignant -> viewModel.updatePatientData { it.copy(malignant = malignant) } },
                                            locale = locale,
                                        )
                                    },
                                ),
                            )
                        }

                        // Predict is the floating action below, not a second button here. Clearing stays in
                        // the flow at the end: a destructive action shouldn't float within reach. It wipes
                        // every answer at once, so it's offered only when there is something to clear and
                        // can be undone from the snackbar rather than confirmed up front.
                        FormItem.CLEAR_ACTION -> {
                            Column(modifier = Modifier.padding(16.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.reset()
                                        coroutineScope.launch {
                                            snackbarHostState.currentSnackbarData?.dismiss()
                                            val result = snackbarHostState.showSnackbar(
                                                message = context.localizedString(R.string.resetDone, locale),
                                                actionLabel = context.localizedString(R.string.undo, locale),
                                                duration = SnackbarDuration.Long,
                                            )
                                            if (result == SnackbarResult.ActionPerformed) viewModel.undoReset()
                                        }
                                    },
                                    enabled = viewModel.canReset,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                ) {
                                    Text(localizedString(R.string.reset, locale))
                                }
                            }
                        }
                    }
                }
            }
        }
        // The screen's one primary action. It floats over the form so it can be reached
        // from anywhere in the long list, and keeps its label except while scrolling down.
        val predictLabel = localizedString(R.string.predictRisks, locale)
        ExtendedFloatingActionButton(
            text = { Text(predictLabel) },
            icon = { Icon(Icons.Default.Calculate, contentDescription = null) },
            expanded = !listState.lastScrolledForward,
            onClick = { viewModel.calculateRisk() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                // Collapsed to its icon, the FAB still needs its name.
                .semantics { contentDescription = predictLabel },
        )
        // Above the FAB, as Material places snackbars.
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = PredictFabClearance),
        )
    }
}

/** The FAB's 56dp plus its 16dp margin and a little air, kept clear below the last row. */
private val PredictFabClearance = 88.dp

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
    isDecimal: Boolean,
    errorMessage: String? = null,
    focusRequester: FocusRequester? = null,
) {
    Column {
        ListItem(
            trailingContent = {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    isError = errorMessage != null,
                    // The headline is a separate node, so the field needs its own label.
                    modifier = Modifier
                        .width(110.dp)
                        .then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier)
                        .semantics {
                            contentDescription = label
                            errorMessage?.let { error(it) }
                        },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (isDecimal) KeyboardType.Decimal else KeyboardType.Number,
                    ),
                    singleLine = true,
                )
            },
            colors = transparentListItemColors,
        ) {
            Text(label)
        }
        // Under the whole row rather than as the field's supporting text, where the 110dp
        // field broke the message mid-phrase. The field already announces it as its error.
        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                    .semantics { hideFromAccessibility() },
            )
        }
    }
}

@Composable
fun SwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    description: String? = null,
    focusRequester: FocusRequester? = null,
) {
    // Toggling on the row rather than the Switch merges the two into one node, so the
    // switch is announced with its label and the whole row becomes a tap target.
    ListItem(
        // The clinical definition of "yes/no" lives here as supporting text.
        supportingContent = description?.let {
            { Text(it, style = MaterialTheme.typography.bodySmall) }
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = null,
            )
        },
        modifier = Modifier
            .then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier)
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Switch,
            ),
        colors = transparentListItemColors,
    ) {
        Text(label)
    }
}

@Composable
fun <T> EnumChoiceRow(
    label: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    locale: Locale,
    description: String? = null,
) where T : Enum<T>, T : Labelled {
    var expanded by remember { mutableStateOf(value = false) }

    // The label/description Text nodes are purely visual: the OutlinedTextField below
    // carries an equivalent contentDescription, so a screen reader should focus that
    // single node rather than announcing the label twice.
    val fieldDescription = description?.let { "$label, $it" } ?: label

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clearAndSetSemantics { },
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.width(180.dp),
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
                    .semantics { contentDescription = fieldDescription },
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { option ->
                    val isSelected = option == selectedOption
                    DropdownMenuItem(
                        text = { Text(localizedString(option.stringResId, locale)) },
                        trailingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else null,
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        },
                        modifier = Modifier.semantics { selected = isSelected },
                    )
                }
            }
        }
    }
}
