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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.window.core.layout.WindowSizeClass
import org.studiomexx.clitical_android.ui.MainViewModel
import org.studiomexx.clitical_android.ui.AboutScreen
import org.studiomexx.clitical_android.ui.IntendedUseGate
import org.studiomexx.clitical_android.ui.QuestionForm
import org.studiomexx.clitical_android.ui.ReferencesScreen
import org.studiomexx.clitical_android.ui.ResultScreen
import org.studiomexx.clitical_android.ui.RiskResultPane
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
    @StringRes val labelRes: Int,
    @StringRes val a11yLabelRes: Int,
) {
    RISK(Icons.Default.Analytics, R.string.riskAssessmentTabShort, R.string.riskAssessmentTabA11y),
    REFERENCES(Icons.AutoMirrored.Filled.MenuBook, R.string.referencesTabShort, R.string.referencesTabA11y),
    SETTINGS(Icons.Default.Settings, R.string.settingsTabShort, R.string.settingsTabA11y)
}

class MainActivity : ComponentActivity() {
    private val preferences by lazy {
        getSharedPreferences(AppLanguage.PREFERENCES_NAME, MODE_PRIVATE)
    }

    private val viewModel: MainViewModel by viewModels {
        viewModelFactory {
            initializer {
                MainViewModel(LanguageStore(preferences), createSavedStateHandle())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CLiTICALAndroidTheme {
                IntendedUseGate(
                    store = IntendedUseDisclaimerStore(preferences),
                    locale = viewModel.locale,
                ) {
                    CLiTICALApp(viewModel)
                }
            }
        }
    }
}

@Composable
private fun CLiTICALApp(viewModel: MainViewModel) {
    val risk = viewModel.calculatedRisk
    var selectedTab by rememberSaveable { mutableStateOf(Tab.RISK) }
    var showAbout by rememberSaveable { mutableStateOf(value = false) }
    val locale = viewModel.locale
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    // Bottom bar on phones and folded devices; a rail along the edge once unfolded or on a tablet.
    val navigationSuiteType = NavigationSuiteScaffoldDefaults.navigationSuiteType(adaptiveInfo)
    // Wide enough (an unfolded foldable, a tablet in landscape) for the result to sit beside
    // the form, as the iOS app does in regular width, instead of replacing it.
    val isTwoPane = adaptiveInfo.windowSizeClass
        .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    val fullScreenRisk = risk.takeUnless { isTwoPane }

    BackHandler(enabled = (fullScreenRisk != null) || showAbout) {
        if (fullScreenRisk != null) viewModel.calculatedRisk = null else showAbout = false
    }

    AnimatedContent(
        targetState = fullScreenRisk,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "riskResultTransition",
    ) { targetRisk ->
        if (targetRisk != null) {
            ResultScreen(
                risk = targetRisk,
                onBack = { viewModel.calculatedRisk = null },
                locale = locale,
            )
        } else if (showAbout) {
            AboutScreen(locale = locale, onBack = { showAbout = false }, modifier = Modifier.fillMaxSize())
        } else {
            NavigationSuiteScaffold(
                navigationSuiteType = navigationSuiteType,
                navigationItems = {
                    Tab.entries.forEach { tab ->
                        val accessibleLabel = localizedString(tab.a11yLabelRes, locale)
                        NavigationSuiteItem(
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = {
                                Text(
                                    text = localizedString(tab.labelRes, locale),
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            modifier = Modifier.semantics {
                                contentDescription = accessibleLabel
                            },
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            navigationSuiteType = navigationSuiteType,
                        )
                    }
                },
            ) {
                // No top app bar: it only repeated the selected tab's name. Each screen opens
                // with its own heading, and the Scaffold still keeps content clear of the
                // status bar.
                Scaffold { innerPadding ->
                    val contentModifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                    when (selectedTab) {
                        Tab.RISK -> RiskAssessment(
                            viewModel = viewModel,
                            isTwoPane = isTwoPane,
                            modifier = contentModifier,
                        )
                        Tab.REFERENCES -> ReferencesScreen(locale = locale, modifier = contentModifier)
                        Tab.SETTINGS -> SettingsScreen(
                            viewModel = viewModel,
                            modifier = contentModifier,
                        ) { showAbout = true }
                    }
                }
            }
        }
    }
}

/**
 * The form alone, or the form with the predicted risks beside it. The form keeps a single
 * call site either way so its scroll position survives folding and unfolding.
 */
@Composable
private fun RiskAssessment(viewModel: MainViewModel, isTwoPane: Boolean, modifier: Modifier) {
    BoxWithConstraints(modifier) {
        val formWidth = (maxWidth * 0.45f).coerceIn(360.dp, 520.dp)
        Row(Modifier.fillMaxSize()) {
            QuestionForm(
                viewModel = viewModel,
                modifier = if (isTwoPane) Modifier.width(formWidth).fillMaxHeight() else Modifier.fillMaxSize(),
            )
            if (isTwoPane) {
                VerticalDivider()
                RiskResultPane(
                    risk = viewModel.calculatedRisk,
                    locale = viewModel.locale,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
            }
        }
    }
}
