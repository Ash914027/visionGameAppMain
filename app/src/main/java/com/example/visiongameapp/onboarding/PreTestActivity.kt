import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.*
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.visiongameapp.databinding.ActivityPreTestBinding
import com.example.visiongameapp.utils.ToastHelper
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.*

class PreTestActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityPreTestBinding
    private lateinit var cameraExecutor: ExecutorService

    // Sensors
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    private var accelSensor: Sensor? = null

    // Data
    private var ambientLux: Float = 0f
    private var motionStability: Float = 1.0f
    private var faceAreaNorm: Float = 0f
    private var faceStability: Float = 0f
    private val faceAreaHistory = mutableListOf<Float>()

    private var lastAccel = 0f
    private var gameClass: String? = null


    private val detector by lazy {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .build()
        FaceDetection.getClient(options)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gameClass = intent.getStringExtra("GAME_CLASS")


        cameraExecutor = Executors.newSingleThreadExecutor()

        // Screen brightness
        window.attributes = window.attributes.apply {
            screenBrightness = 0.7f
        }

        setupSensors()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        binding.btnStartTest.setOnClickListener {
            val selectedEye = if (binding.rbLeftEye.isChecked) "Left" else "Right"

            if (gameClass != null) {
                try {
                    val clazz = Class.forName(gameClass!!)
                    val intent = Intent(this, clazz).apply {
                        putExtra("TEST_EYE", selectedEye)
                    }
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    ToastHelper.showCustomToast(this, "Error starting game", false)
                }
            } else {
                ToastHelper.showCustomToast(this, "Game not found", false)
            }
        }
    }

    private fun setupSensors() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            val analyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImageProxy(imageProxy)
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
                    preview,
                    analyzer
                )
            } catch (e: Exception) {
                Log.e("PreTest", "Camera error", e)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        detector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    val box = faces[0].boundingBox
                    val area = (box.width() * box.height()).toFloat()
                    val previewArea = (imageProxy.width * imageProxy.height).toFloat()

                    faceAreaNorm = area / previewArea

                    if (faceAreaHistory.size >= 10) {
                        faceAreaHistory.removeAt(0) // ✅ FIXED
                    }
                    faceAreaHistory.add(faceAreaNorm)

                    faceStability = calculateStability(faceAreaHistory)
                } else {
                    faceAreaNorm = 0f
                    faceStability = 0f
                }

                val score = calculateScore()
                runOnUiThread { updateUI(score) }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun calculateStability(history: List<Float>): Float {
        if (history.isEmpty()) return 0f
        val avg = history.average().toFloat()
        val variance = history.map { (it - avg).pow(2) }.average().toFloat()
        return (1f - variance * 100).coerceIn(0f, 1f)
    }

    // ✅ Logic-based scoring (no TFLite)
    private fun calculateScore(): Float {
        var score = 0f

        if (ambientLux > 60) score += 0.3f
        if (motionStability > 0.8f) score += 0.3f
        if (faceAreaNorm > 0.05f) score += 0.2f
        if (faceStability > 0.7f) score += 0.2f

        return score.coerceIn(0f, 1f)
    }

    private fun updateUI(score: Float) {
        binding.tvReliabilityScore.text = "Reliability: %.2f".format(score)

        when {
            score >= 0.6 -> {
                binding.tvFeedback.text = "✅ Ready"
                binding.tvFeedback.setTextColor(Color.GREEN)
                binding.btnStartTest.isEnabled = true
            }
            score >= 0.3 -> {
                binding.tvFeedback.text = "⚠️ Improve conditions"
                binding.tvFeedback.setTextColor(Color.YELLOW)
                binding.btnStartTest.isEnabled = false
            }
            else -> {
                binding.tvFeedback.text = "❌ Poor conditions"
                binding.tvFeedback.setTextColor(Color.RED)
                binding.btnStartTest.isEnabled = false
            }
        }

        binding.tvLightStatus.text = "Light: ${ambientLux.toInt()} lux"
        binding.tvMotionStatus.text =
            if (motionStability > 0.8f) "Stable" else "Moving"
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_LIGHT -> ambientLux = event.values[0]
            Sensor.TYPE_ACCELEROMETER -> {
                val accel = sqrt(event.values.map { it * it }.sum())
                val motion = abs(accel - lastAccel)
                motionStability = (1f - motion / 2f).coerceIn(0f, 1f)
                lastAccel = accel
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        lightSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        accelSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun allPermissionsGranted() =
        REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults) // ✅ FIXED

        if (requestCode == REQUEST_CODE_PERMISSIONS && allPermissionsGranted()) {
            startCamera()
        } else {
            ToastHelper.showCustomToast(this, "Permission denied", false)
            finish()
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}

