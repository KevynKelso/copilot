package com.example.copilot.presentation

import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import com.example.copilot.data.ImageClassifierHelper
import com.example.copilot.domain.Classification
import com.example.copilot.domain.ObjectClassifier
import org.tensorflow.lite.task.vision.classifier.Classifications

class ObjectImageAnalyzer(
    private val classifier: ObjectClassifier,
    private val onResults: (List<Classification>) -> Unit
): ImageAnalysis.Analyzer {

    private var frameSkipCounter = 0

    override fun analyze(image: ImageProxy) {
        if(frameSkipCounter % 30 == 0) {
            val rotationDegrees = image.imageInfo.rotationDegrees
            val bitmap = image
                .toBitmap()
//            bitmap.height = 256
//            bitmap.width = 256

            val results = classifier.classify(bitmap, rotationDegrees)
            onResults(results)
        }
        frameSkipCounter++

        image.close()
    }
}

class ObjectImageAnalyzer2(
    private val helper: ImageClassifierHelper,
): ImageAnalysis.Analyzer {

    private var frameSkipCounter = 0

    override fun analyze(image: ImageProxy) {
        if(frameSkipCounter % 2 == 0) {
            val rotationDegrees = image.imageInfo.rotationDegrees
            val bitmap = image
                .toBitmap()
//            bitmap.height = 256
//            bitmap.width = 256

            val results = helper.classify(bitmap, rotationDegrees)
        }
        frameSkipCounter++

        image.close()
    }
}

class ClassifierListenerAnalyzer(
    private val onResultsCback: (List<Classifications>?) -> Unit,
    private val onErrorCback: (String) -> Unit,
): ImageClassifierHelper.ClassifierListener {
    override fun onError(error: String) {
        onErrorCback(error)
    }
    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
        onResultsCback(results)
    }
}