package org.studiomexx.clitical_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import org.studiomexx.clitical_android.ui.AboutScreen
import org.studiomexx.clitical_android.ui.LanguageScreen
import org.studiomexx.clitical_android.ui.MainViewModel
import org.studiomexx.clitical_android.ui.QuestionForm
import org.studiomexx.clitical_android.ui.ReferencesScreen
import org.studiomexx.clitical_android.ui.ResultScreen
import org.studiomexx.clitical_android.ui.localizedString
import org.studiomexx.clitical_android.ui.theme.CLiTICALAndroidTheme

/** [labelRes] is kept short so every navigation bar item fits on one line. */
private enum class Tab(
    val icon: ImageVector,
    @StringRes val titleRes: Int,
    @StringRes val labelRes: Int
) {
    RISK(Icons.Default.Analytics, R.string.riskAssessmentTab, R.string.riskAssessmentTab),
    LANGUAGE(Icons.Default.Language, R.string.language, R.string.language),
    REFERENCES(Icons.AutoMirrored.Filled.MenuBook, R.string.references, R.string.references),
    ABOUT(Icons.Default.Info, R.string.about, R.string.aboutTab)
}

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CLiTICALAndroidTheme {
                CLiTICALApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CLiTICALApp(viewModel: MainViewModel) {
    val risk = viewModel.calculatedRisk
    var selectedTab by rememberSaveable { mutableStateOf(Tab.RISK) }
    val locale = viewModel.locale

    BackHandler(enabled = risk != null) { viewModel.calculatedRisk = null }

    if (risk != null) {
        ResultScreen(
            risk = risk,
            onBack = { viewModel.calculatedRisk = null },
            locale = locale
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(localizedString(selectedTab.titleRes, locale)) })
            },
            bottomBar = {
                NavigationBar {
                    Tab.entries.forEach { tab ->
                        NavigationBarItem(
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = {
                                Text(
                                    text = localizedString(tab.labelRes, locale),
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab }
                        )
                    }
                }
            }
        ) { innerPadding ->
            val contentModifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
            when (selectedTab) {
                Tab.RISK -> QuestionForm(viewModel = viewModel, modifier = contentModifier)
                Tab.LANGUAGE -> LanguageScreen(viewModel = viewModel, modifier = contentModifier)
                Tab.REFERENCES -> ReferencesScreen(locale = locale, modifier = contentModifier)
                Tab.ABOUT -> AboutScreen(locale = locale, modifier = contentModifier)
            }
        }
    }
}
