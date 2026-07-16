package org.studiomexx.clitical_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import org.studiomexx.clitical_android.ui.AboutScreen
import org.studiomexx.clitical_android.ui.LanguageScreen
import org.studiomexx.clitical_android.ui.QuestionForm
import org.studiomexx.clitical_android.ui.ReferencesScreen
import org.studiomexx.clitical_android.ui.ResultScreen
import org.studiomexx.clitical_android.ui.localizedString
import org.studiomexx.clitical_android.ui.theme.CLiTICALAndroidTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CLiTICALAndroidTheme {
                val risk = viewModel.calculatedRisk
                var selectedTab by remember { mutableStateOf(0) }
                val locale = viewModel.locale

                if (risk != null) {
                    ResultScreen(
                        risk = risk,
                        onBack = { viewModel.calculatedRisk = null },
                        locale = locale
                    )
                } else {
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                val tabs = listOf(
                                    Icons.Default.Analytics to R.string.riskAssessmentTab,
                                    Icons.Default.Language to R.string.language,
                                    Icons.AutoMirrored.Filled.MenuBook to R.string.references,
                                    Icons.Default.Info to R.string.aboutTab
                                )
                                tabs.forEachIndexed { index, (icon: ImageVector, labelRes: Int) ->
                                    NavigationBarItem(
                                        icon = { Icon(icon, contentDescription = null) },
                                        label = {
                                            Text(
                                                text = localizedString(labelRes, locale),
                                                maxLines = 1,
                                                softWrap = false,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        },
                                        selected = selectedTab == index,
                                        onClick = { selectedTab = index }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        val contentModifier = Modifier.padding(innerPadding)
                        when (selectedTab) {
                            0 -> QuestionForm(viewModel = viewModel, modifier = contentModifier)
                            1 -> LanguageScreen(viewModel = viewModel, modifier = contentModifier)
                            2 -> ReferencesScreen(locale = locale, modifier = contentModifier)
                            3 -> AboutScreen(locale = locale, modifier = contentModifier)
                        }
                    }
                }
            }
        }
    }
}
