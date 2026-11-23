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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
                IntendedUseGate(
                    store = IntendedUseDisclaimerStore(
                        getSharedPreferences("clitical_preferences", MODE_PRIVATE)
                    ),
                    locale = viewModel.locale
                ) {
                    CLiTICALApp(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CLiTICALApp(viewModel: MainViewModel) {
    val risk = viewModel.calculatedRisk
    var selectedTab by rememberSaveable { mutableStateOf(Tab.RISK) }
    var showAbout by rememberSaveable { mutableStateOf(false) }
    val locale = viewModel.locale
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    // Bottom bar on phones and folded devices; a rail along the edge once unfolded or on a tablet.
    val navigationSuiteType = NavigationSuiteScaffoldDefaults.navigationSuiteType(adaptiveInfo)
    // Wide enough (an unfolded foldable, a tablet in landscape) for the result to sit beside
    // the form, as the iOS app does in regular width, instead of replacing it.
    val isTwoPane = adaptiveInfo.windowSizeClass
        .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    val fullScreenRisk = risk.takeUnless { isTwoPane }

    BackHandler(enabled = fullScreenRisk != null || showAbout) {
        if (fullScreenRisk != null) viewModel.calculatedRisk = null else showAbout = false
    }

    AnimatedContent(
        targetState = fullScreenRisk,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "riskResultTransition"
    ) { targetRisk ->
        if (targetRisk != null) {
            ResultScreen(
                risk = targetRisk,
                onBack = { viewModel.calculatedRisk = null },
                locale = locale
            )
        } else if (showAbout) {
            AboutScreen(locale = locale, onBack = { showAbout = false }, modifier = Modifier.fillMaxSize())
        } else {
            val formListState = rememberLazyListState()
            val isBar = navigationSuiteType in BarNavigationSuiteTypes
            NavigationSuiteScaffold(
                navigationSuiteType = navigationSuiteType,
                // Predict is the screen's primary action: an extended FAB above the bottom bar
                // on a phone, or a FAB in the rail's header once unfolded.
                primaryActionContent = {
                    if (selectedTab == Tab.RISK) {
                        PredictFab(
                            label = localizedString(R.string.predictRisks, locale),
                            // Shrinks to its icon while the form scrolls down, so it covers less.
                            expanded = isBar && !formListState.lastScrolledForward,
                            onClick = { viewModel.calculateRisk() },
                            // The rail's header doesn't center it in the 96dp rail by itself.
                            modifier = if (isBar) Modifier else Modifier.padding(horizontal = 20.dp)
                        )
                    }
                },
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
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            modifier = Modifier.semantics {
                                contentDescription = accessibleLabel
                            },
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            navigationSuiteType = navigationSuiteType
                        )
                    }
                }
            ) {
                val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        TopAppBar(
                            title = { Text(localizedString(selectedTab.titleRes, locale)) },
                            scrollBehavior = scrollBehavior
                        )
                    }
                ) { innerPadding ->
                    val contentModifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                    when (selectedTab) {
                        Tab.RISK -> RiskAssessment(
                            viewModel = viewModel,
                            isTwoPane = isTwoPane,
                            listState = formListState,
                            bottomInset = if (isBar) PredictFabClearance else 0.dp,
                            modifier = contentModifier
                        )
                        Tab.REFERENCES -> ReferencesScreen(locale = locale, modifier = contentModifier)
                        Tab.SETTINGS -> SettingsScreen(
                            viewModel = viewModel,
                            modifier = contentModifier,
                            onAboutClick = { showAbout = true }
                        )
                    }
                }
            }
        }
    }
}

private val BarNavigationSuiteTypes = setOf(
    NavigationSuiteType.ShortNavigationBarCompact,
    NavigationSuiteType.ShortNavigationBarMedium,
    NavigationSuiteType.NavigationBar
)

/** A FAB's 56dp plus the 16dp the scaffold keeps around it, and a little air. */
private val PredictFabClearance = 88.dp

@Composable
private fun PredictFab(
    label: String,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        text = { Text(label) },
        // Not the Risk tab's Analytics icon, which sits right below it in the rail.
        icon = { Icon(Icons.Default.Calculate, contentDescription = null) },
        expanded = expanded,
        onClick = onClick,
        // Collapsed to its icon, the FAB still needs its name.
        modifier = modifier.semantics { contentDescription = label }
    )
}

/**
 * The form alone, or the form with the predicted risks beside it. The form keeps a single
 * call site either way so its scroll position survives folding and unfolding.
 */
@Composable
private fun RiskAssessment(
    viewModel: MainViewModel,
    isTwoPane: Boolean,
    listState: LazyListState,
    bottomInset: Dp,
    modifier: Modifier
) {
    BoxWithConstraints(modifier) {
        val formWidth = (maxWidth * 0.45f).coerceIn(360.dp, 520.dp)
        Row(Modifier.fillMaxSize()) {
            QuestionForm(
                viewModel = viewModel,
                modifier = if (isTwoPane) Modifier.width(formWidth).fillMaxHeight() else Modifier.fillMaxSize(),
                listState = listState,
                bottomInset = bottomInset
            )
            if (isTwoPane) {
                VerticalDivider()
                RiskResultPane(
                    risk = viewModel.calculatedRisk,
                    locale = viewModel.locale,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }
        }
    }
}
