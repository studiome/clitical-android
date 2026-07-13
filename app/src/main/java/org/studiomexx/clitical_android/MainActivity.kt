package org.studiomexx.clitical_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import org.studiomexx.clitical_android.ui.QuestionForm
import org.studiomexx.clitical_android.ui.ResultScreen
import org.studiomexx.clitical_android.ui.theme.CLiTICALAndroidTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CLiTICALAndroidTheme {
                val risk = viewModel.calculatedRisk
                if (risk == null) {
                    QuestionForm(viewModel = viewModel)
                } else {
                    ResultScreen(
                        risk = risk,
                        onBack = { viewModel.calculatedRisk = null },
                        locale = viewModel.locale
                    )
                }
            }
        }
    }
}