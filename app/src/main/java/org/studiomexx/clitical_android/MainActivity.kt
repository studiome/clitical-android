package org.studiomexx.clitical_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Analytics, contentDescription = null) },
                                    label = { Text(localizedString(R.string.riskAssessmentTab, locale)) },
                                    selected = selectedTab == 0,
                                    onClick = { selectedTab = 0 }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Language, contentDescription = null) },
                                    label = { Text(localizedString(R.string.language, locale)) },
                                    selected = selectedTab == 1,
                                    onClick = { selectedTab = 1 }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                                    label = { Text(localizedString(R.string.references, locale)) },
                                    selected = selectedTab == 2,
                                    onClick = { selectedTab = 2 }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                                    label = { Text(localizedString(R.string.about, locale)) },
                                    selected = selectedTab == 3,
                                    onClick = { selectedTab = 3 }
                                )
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
