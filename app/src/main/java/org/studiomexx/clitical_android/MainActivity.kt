package org.studiomexx.clitical_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import org.studiomexx.clitical_android.ui.MainViewModel
import org.studiomexx.clitical_android.ui.QuestionForm
import org.studiomexx.clitical_android.ui.ReferencesScreen
import org.studiomexx.clitical_android.ui.ResultScreen
import org.studiomexx.clitical_android.ui.SettingsScreen
import org.studiomexx.clitical_android.ui.localizedString
import org.studiomexx.clitical_android.ui.theme.CLiTICALAndroidTheme

/**
 * Three destinations, matching the iOS app's tab bar: settings-like items
 * (language, terms, app info) are grouped into a single Settings tab rather
 * than each holding a tab of their own.
 */
private enum class Tab(
    val icon: ImageVector,
    @StringRes val titleRes: Int,
    @StringRes val labelRes: Int,
    @StringRes val a11yLabelRes: Int
) {
    RISK(Icons.Default.Analytics, R.string.riskAssessmentTab, R.string.riskAssessmentTabShort, R.string.riskAssessmentTabA11y),
    REFERENCES(Icons.AutoMirrored.Filled.MenuBook, R.string.references, R.string.referencesTabShort, R.string.referencesTabA11y),
    SETTINGS(Icons.Default.Settings, R.string.settings, R.string.settingsTabShort, R.string.settingsTabA11y)
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

    AnimatedContent(
        targetState = risk,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "riskResultTransition"
    ) { targetRisk ->
        if (targetRisk != null) {
            ResultScreen(
                risk = targetRisk,
                onBack = { viewModel.calculatedRisk = null },
                locale = locale
            )
        } else {
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    TopAppBar(
                        title = { Text(localizedString(selectedTab.titleRes, locale)) },
                        scrollBehavior = scrollBehavior
                    )
                },
                bottomBar = {
                    NavigationBar {
                        Tab.entries.forEach { tab ->
                            val accessibleLabel = localizedString(tab.a11yLabelRes, locale)
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
                                modifier = Modifier.semantics {
                                    contentDescription = accessibleLabel
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
                    Tab.REFERENCES -> ReferencesScreen(locale = locale, modifier = contentModifier)
                    Tab.SETTINGS -> SettingsScreen(viewModel = viewModel, modifier = contentModifier)
                }
            }
        }
    }
}
