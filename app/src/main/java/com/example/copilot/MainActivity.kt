package com.example.copilot

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.copilot.data.ImageClassifierHelper
import com.example.copilot.domain.Classification
import com.example.copilot.presentation.CameraPreview
import com.example.copilot.presentation.ClassifierListenerAnalyzer
import com.example.copilot.presentation.ObjectImageAnalyzer2
import com.example.copilot.ui.theme.CopilotTheme
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        showErrorDialog(this, "test err")
        if(!hasCameraPermission()) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.CAMERA), 0
            )
        }
        setContent {
            CopilotTheme {
                var classifications by remember {
                    mutableStateOf(emptyList<Classifications>())
                }
                val helper = remember {
                    ImageClassifierHelper(
                        threshold = 0.6f,
                        context = applicationContext,
                        imageClassifierListener = ClassifierListenerAnalyzer(
                            onErrorCback = {
                                showErrorDialog(this, it)
                            },
                            onResultsCback = {
                                println("Results Received")
                                if (it != null) {
                                    classifications = it
                                }
                            }
                        ))
                }
                val analyzer = remember {
                    ObjectImageAnalyzer2(helper = helper)
                }
                val controller = remember {
                    LifecycleCameraController(applicationContext).apply {
                        setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
                        setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(applicationContext), analyzer)
                    }
                }
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CameraPreview(controller, Modifier.fillMaxSize())
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .align(alignment = Alignment.BottomCenter)
                        ) {
                            classifications.forEach { classification ->
                                classification.categories.forEach {
                                    print(it.index)
                                    print(it.label)
                                    print(it.score)
                                    print(it.displayName)
                                    Text(text = it.label + " " + it.score, modifier = Modifier.fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(8.dp),
                                        textAlign = TextAlign.Center,
                                        fontSize = 20.sp,
                                        color = MaterialTheme.colorScheme.primary)
                                }
                        }
                    }
                }
            }
        }
    }
    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        this, android.Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun showErrorDialog(context: Context, msg: String) {
        val alertDialog = AlertDialog.Builder(context)

        alertDialog.apply {
            //setIcon(R.drawable.ic_hello)
            setTitle("Error")
            setMessage(msg)
//            setPositiveButton("Positive") { _: DialogInterface?, _: Int ->
//                Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show()
//            }
//            setNegativeButton("Negative") { _, _ ->
//                Toast.makeText(context, "Negative", Toast.LENGTH_SHORT).show()
//            }
//            setNeutralButton("Neutral") { _, _ ->
//                Toast.makeText(context, "Neutral", Toast.LENGTH_SHORT).show()
//            }
//            setOnDismissListener {
//                Toast.makeText(context, "Hello!!!", Toast.LENGTH_SHORT).show()
//            }

        }.create().show()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CopilotTheme {
        Greeting("Android")
    }
}