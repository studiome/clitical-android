package org.studiomexx.clitical_android.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.studiomexx.clitical_android.Activity
import org.studiomexx.clitical_android.CKD
import org.studiomexx.clitical_android.MalignantNeoplasm
import org.studiomexx.clitical_android.MainViewModel
import org.studiomexx.clitical_android.Questions
import org.studiomexx.clitical_android.R
import org.studiomexx.clitical_android.RutherfordClassification
import org.studiomexx.clitical_android.Sex
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun QuestionForm(viewModel: MainViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val locale = viewModel.locale

    val pagerState = rememberPagerState(pageCount = { Questions.entries.size })
    val currentQuestion = remember(pagerState.currentPage) { Questions.entries[pagerState.currentPage] }

    // Dialog flags
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showReferencesDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    // Listen VM resets
    LaunchedEffect(viewModel.calculatedRisk) {
        if (viewModel.calculatedRisk == null && pagerState.currentPage != 0) {
            // If reset manually via action button
            // But we check if VM text fields are empty to differentiate
            if (viewModel.ageText.isEmpty() && viewModel.heightText.isEmpty() && viewModel.weightText.isEmpty() && viewModel.albText.isEmpty()) {
                pagerState.scrollToPage(0)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "CLiTICAL",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp)
                )
                Divider()
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Language, contentDescription = null) },
                    label = { Text(localizedString(R.string.language, locale)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        showLanguageDialog = true
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text(localizedString(R.string.references, locale)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        showReferencesDialog = true
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text(localizedString(R.string.about, locale)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        showAboutDialog = true
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text(localizedString(R.string.appTerms, locale)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://studiome.github.io/clti_risk/"))
                        context.startActivity(intent)
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(localizedString(currentQuestion.titleResId, locale)) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(Questions.SUMMARY.ordinal)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Summarize,
                                contentDescription = localizedString(R.string.summaryButtonLabel, locale)
                            )
                        }
                        IconButton(onClick = {
                            viewModel.reset()
                            scope.launch {
                                pagerState.scrollToPage(0)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = localizedString(R.string.refreshButtonLabel, locale)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            floatingActionButton = {
                if (currentQuestion == Questions.SUMMARY) {
                    FloatingActionButton(
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
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Analytics, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(localizedString(R.string.analysis, locale))
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Progress Indicator
                val progress = (pagerState.currentPage + 1).toFloat() / Questions.entries.size
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth()
                )

                // Questionnaire Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    HorizontalPager(
                        state = pagerState,
                        userScrollEnabled = false,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        QuestionPageContent(
                            question = Questions.entries[page],
                            viewModel = viewModel,
                            locale = locale
                        )
                    }
                }

                // Bottom Nav Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val isFirst = pagerState.currentPage == 0
                    val isLast = pagerState.currentPage == Questions.entries.size - 1

                    if (!isFirst) {
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        ) {
                            Text(localizedString(R.string.backButton, locale))
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (!isLast) {
                        // Validate numeric forms before moving next
                        val canGoNext = remember(currentQuestion, viewModel.ageText, viewModel.heightText, viewModel.weightText, viewModel.albText) {
                            derivedStateOf {
                                when (currentQuestion) {
                                    Questions.AGE -> viewModel.ageText.toIntOrNull() != null
                                    Questions.HEIGHT -> viewModel.heightText.toDoubleOrNull() != null
                                    Questions.WEIGHT -> viewModel.weightText.toDoubleOrNull() != null
                                    Questions.ALBUMIN -> viewModel.albText.toDoubleOrNull() != null
                                    else -> true
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (canGoNext.value) {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                } else {
                                    Toast.makeText(context, context.getString(R.string.formErrorMessage), Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text(localizedString(R.string.nextButton, locale))
                        }
                    }
                }
            }
        }
    }

    // Language Selection Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(localizedString(R.string.language, locale)) },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (locale.language == "ja"),
                                onClick = { viewModel.locale = Locale("ja") }
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (locale.language == "ja"),
                            onClick = { viewModel.locale = Locale("ja") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(localizedString(R.string.ja, locale))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (locale.language == "en"),
                                onClick = { viewModel.locale = Locale("en") }
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (locale.language == "en"),
                            onClick = { viewModel.locale = Locale("en") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(localizedString(R.string.en, locale))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(localizedString(R.string.ok, locale))
                }
            }
        )
    }

    // References Dialog
    if (showReferencesDialog) {
        AlertDialog(
            onDismissRequest = { showReferencesDialog = false },
            title = { Text(localizedString(R.string.references, locale)) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(localizedString(R.string.tapToOpenLink, locale), style = MaterialTheme.typography.bodyMedium)
                    Divider()
                    TextButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://doi.org/10.1093/bjs/znab036"))
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
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://doi.org/10.1016/j.ejvs.2022.05.038"))
                            context.startActivity(intent)
                        }
                    ) {
                        Text(
                            "2. Miyata T. et al, Prediction Models for Two Year Overall Survival and Amputation Free Survival After Revascularisation for Chronic Limb Threatening Ischaemia. Eur J Vasc Endovasc Surg . 2022 Jun 7;S1078-5884(22)00340-9.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReferencesDialog = false }) {
                    Text(localizedString(R.string.ok, locale))
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text(localizedString(R.string.about, locale)) },
            text = {
                Column {
                    Text("CLiTICAL", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Version: 1.0.0", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(localizedString(R.string.appLegalese, locale), style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text(localizedString(R.string.ok, locale))
                }
            }
        )
    }
}

@Composable
fun QuestionPageContent(
    question: Questions,
    viewModel: MainViewModel,
    locale: Locale
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Subtitle instructions
        Text(
            text = localizedString(question.subtitleResId, locale),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Question specific input components
        when (question) {
            Questions.INSTRUCTION -> {
                // Just text
            }
            Questions.SEX -> {
                RadioChoiceGroup(
                    selectedValue = viewModel.sex,
                    options = Sex.entries,
                    onSelected = { viewModel.sex = it },
                    locale = locale
                )
            }
            Questions.AGE -> {
                NumberInputField(
                    value = viewModel.ageText,
                    onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() }) viewModel.ageText = it },
                    label = localizedString(R.string.questionAgeTitle, locale),
                    isDecimal = false
                )
            }
            Questions.HEIGHT -> {
                NumberInputField(
                    value = viewModel.heightText,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d{0,3}(\\.\\d{0,1})?\$"))) {
                            viewModel.heightText = it
                        }
                    },
                    label = localizedString(R.string.questionHeightTitle, locale),
                    isDecimal = true
                )
            }
            Questions.WEIGHT -> {
                NumberInputField(
                    value = viewModel.weightText,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d{0,3}(\\.\\d{0,1})?\$"))) {
                            viewModel.weightText = it
                        }
                    },
                    label = localizedString(R.string.questionWeightTitle, locale),
                    isDecimal = true
                )
            }
            Questions.ALBUMIN -> {
                NumberInputField(
                    value = viewModel.albText,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d{0,2}(\\.\\d{0,1})?\$"))) {
                            viewModel.albText = it
                        }
                    },
                    label = localizedString(R.string.questionAlbTitle, locale),
                    isDecimal = true
                )
            }
            Questions.ACTIVITY -> {
                RadioChoiceGroup(
                    selectedValue = viewModel.activity,
                    options = Activity.entries,
                    onSelected = { viewModel.activity = it },
                    locale = locale
                )
            }
            Questions.CHF -> {
                BinaryChoiceGroup(
                    selectedValue = viewModel.hasCHF,
                    onSelected = { viewModel.hasCHF = it },
                    locale = locale
                )
            }
            Questions.CAD -> {
                BinaryChoiceGroup(
                    selectedValue = viewModel.hasCAD,
                    onSelected = { viewModel.hasCAD = it },
                    locale = locale
                )
            }
            Questions.CVD -> {
                BinaryChoiceGroup(
                    selectedValue = viewModel.hasCVD,
                    onSelected = { viewModel.hasCVD = it },
                    locale = locale
                )
            }
            Questions.CKD -> {
                RadioChoiceGroup(
                    selectedValue = viewModel.ckd,
                    options = CKD.entries,
                    onSelected = { viewModel.ckd = it },
                    locale = locale
                )
            }
            Questions.MALIGNANT_NEOPLASM -> {
                RadioChoiceGroup(
                    selectedValue = viewModel.malignant,
                    options = MalignantNeoplasm.entries,
                    onSelected = { viewModel.malignant = it },
                    locale = locale
                )
            }
            Questions.LESION_AI -> {
                BinaryChoiceGroup(
                    selectedValue = viewModel.hasAILesion,
                    onSelected = { viewModel.hasAILesion = it },
                    locale = locale
                )
            }
            Questions.LESION_FP -> {
                BinaryChoiceGroup(
                    selectedValue = viewModel.hasFPLesion,
                    onSelected = { viewModel.hasFPLesion = it },
                    locale = locale
                )
            }
            Questions.LESION_BK -> {
                BinaryChoiceGroup(
                    selectedValue = viewModel.hasBKLesion,
                    onSelected = { viewModel.hasBKLesion = it },
                    locale = locale
                )
            }
            Questions.URGENT_PROCEDURE -> {
                BinaryChoiceGroup(
                    selectedValue = viewModel.isUrgent,
                    onSelected = { viewModel.isUrgent = it },
                    locale = locale
                )
            }
            Questions.FEVER -> {
                BinaryChoiceGroup(
                    selectedValue = viewModel.hasFever,
                    onSelected = { viewModel.hasFever = it },
                    locale = locale
                )
            }
            Questions.ABNORMAL_WBC -> {
                BinaryChoiceGroup(
                    selectedValue = viewModel.hasAbnormalWBC,
                    onSelected = { viewModel.hasAbnormalWBC = it },
                    locale = locale
                )
            }
            Questions.LOCAL_INFECTION -> {
                BinaryChoiceGroup(
                    selectedValue = viewModel.hasLocalInfection,
                    onSelected = { viewModel.hasLocalInfection = it },
                    locale = locale
                )
            }
            Questions.DYSLIPIDEMIA -> {
                BinaryChoiceGroup(
                    selectedValue = viewModel.hasDyslipidemia,
                    onSelected = { viewModel.hasDyslipidemia = it },
                    locale = locale
                )
            }
            Questions.SMOKING -> {
                BinaryChoiceGroup(
                    selectedValue = viewModel.isSmoking,
                    onSelected = { viewModel.isSmoking = it },
                    locale = locale
                )
            }
            Questions.CONTRALATERAL -> {
                BinaryChoiceGroup(
                    selectedValue = viewModel.hasContraLateralLesion,
                    onSelected = { viewModel.hasContraLateralLesion = it },
                    locale = locale
                )
            }
            Questions.OTHERS -> {
                BinaryChoiceGroup(
                    selectedValue = viewModel.hasOtherVD,
                    onSelected = { viewModel.hasOtherVD = it },
                    locale = locale
                )
            }
            Questions.RUTHERFORD -> {
                RadioChoiceGroup(
                    selectedValue = viewModel.rutherford,
                    options = RutherfordClassification.entries,
                    onSelected = { viewModel.rutherford = it },
                    locale = locale
                )
            }
            Questions.SUMMARY -> {
                SummaryContent(viewModel = viewModel, locale = locale)
            }
        }
    }
}

@Composable
fun <T : Enum<T>> RadioChoiceGroup(
    selectedValue: T,
    options: List<T>,
    onSelected: (T) -> Unit,
    locale: Locale
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        options.forEach { option ->
            val labelRes = when (option) {
                is Sex -> option.stringResId
                is Activity -> option.stringResId
                is CKD -> option.stringResId
                is MalignantNeoplasm -> option.stringResId
                is RutherfordClassification -> option.stringResId
                else -> 0
            }
            val label = if (labelRes != 0) localizedString(labelRes, locale) else option.name

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (option == selectedValue),
                        onClick = { onSelected(option) }
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (option == selectedValue),
                    onClick = { onSelected(option) }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(label, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun BinaryChoiceGroup(
    selectedValue: Boolean,
    onSelected: (Boolean) -> Unit,
    locale: Locale
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectable(
                    selected = selectedValue,
                    onClick = { onSelected(true) }
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedValue,
                onClick = { onSelected(true) }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(localizedString(R.string.yes, locale), style = MaterialTheme.typography.bodyLarge)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectable(
                    selected = !selectedValue,
                    onClick = { onSelected(false) }
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = !selectedValue,
                onClick = { onSelected(false) }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(localizedString(R.string.no, locale), style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun NumberInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isDecimal: Boolean
) {
    var isError by remember(value) {
        mutableStateOf(false)
    }

    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
            isError = if (isDecimal) {
                it.toDoubleOrNull() == null
            } else {
                it.toIntOrNull() == null
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isDecimal) KeyboardType.Decimal else KeyboardType.Number
        ),
        isError = isError,
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SummaryContent(
    viewModel: MainViewModel,
    locale: Locale
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        SummaryItem(localizedString(R.string.questionSexTitle, locale), localizedString(viewModel.sex.stringResId, locale))
        SummaryItem(localizedString(R.string.questionAgeTitle, locale), viewModel.ageText)
        SummaryItem(localizedString(R.string.questionHeightTitle, locale), viewModel.heightText)
        SummaryItem(localizedString(R.string.questionWeightTitle, locale), viewModel.weightText)
        SummaryItem(localizedString(R.string.questionAlbTitle, locale), viewModel.albText)
        SummaryItem(localizedString(R.string.questionActivityTitle, locale), localizedString(viewModel.activity.stringResId, locale))
        SummaryItem(localizedString(R.string.questionCHFTitle, locale), localizedString(if (viewModel.hasCHF) R.string.yes else R.string.no, locale))
        SummaryItem(localizedString(R.string.questionCADTitle, locale), localizedString(if (viewModel.hasCAD) R.string.yes else R.string.no, locale))
        SummaryItem(localizedString(R.string.questionCVDTitle, locale), localizedString(if (viewModel.hasCVD) R.string.yes else R.string.no, locale))
        SummaryItem(localizedString(R.string.questionCKDTitle, locale), localizedString(viewModel.ckd.stringResId, locale))
        SummaryItem(localizedString(R.string.questionMalignantTitle, locale), localizedString(viewModel.malignant.stringResId, locale))
        SummaryItem(localizedString(R.string.questionAILesionTitle, locale), localizedString(if (viewModel.hasAILesion) R.string.yes else R.string.no, locale))
        SummaryItem(localizedString(R.string.questionFPLesionTitle, locale), localizedString(if (viewModel.hasFPLesion) R.string.yes else R.string.no, locale))
        SummaryItem(localizedString(R.string.questionBKLesionTitle, locale), localizedString(if (viewModel.hasBKLesion) R.string.yes else R.string.no, locale))
        SummaryItem(localizedString(R.string.questionUrgentTitle, locale), localizedString(if (viewModel.isUrgent) R.string.yes else R.string.no, locale))
        SummaryItem(localizedString(R.string.questionFeverTitle, locale), localizedString(if (viewModel.hasFever) R.string.yes else R.string.no, locale))
        SummaryItem(localizedString(R.string.questionAbnormalWBCTitle, locale), localizedString(if (viewModel.hasAbnormalWBC) R.string.yes else R.string.no, locale))
        SummaryItem(localizedString(R.string.questionLocalInfectionTitle, locale), localizedString(if (viewModel.hasLocalInfection) R.string.yes else R.string.no, locale))
        SummaryItem(localizedString(R.string.questionDLTitle, locale), localizedString(if (viewModel.hasDyslipidemia) R.string.yes else R.string.no, locale))
        SummaryItem(localizedString(R.string.questionSmokingTitle, locale), localizedString(if (viewModel.isSmoking) R.string.yes else R.string.no, locale))
        SummaryItem(localizedString(R.string.questionContraTitle, locale), localizedString(if (viewModel.hasContraLateralLesion) R.string.yes else R.string.no, locale))
        SummaryItem(localizedString(R.string.questionOtherLesionTitle, locale), localizedString(if (viewModel.hasOtherVD) R.string.yes else R.string.no, locale))
        SummaryItem(localizedString(R.string.questionRutherfordTitle, locale), localizedString(viewModel.rutherford.stringResId, locale))
    }
}

@Composable
fun SummaryItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
    Divider()
}
