package com.kyf.knowyourfood

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.webkit.WebViewAssetLoader
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView
    private var pendingPermissionRequest: PermissionRequest? = null
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private var cameraPhotoUri: Uri? = null

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pendingPermissionRequest?.let { req ->
                runOnUiThread {
                    req.grant(req.resources)
                    pendingPermissionRequest = null
                }
            }
            // Reload camera in webview if open
            webView.evaluateJavascript("window.onCameraPermissionGranted && window.onCameraPermissionGranted();", null)
        } else {
            pendingPermissionRequest?.deny()
            pendingPermissionRequest = null
            Toast.makeText(this, "Camera permission is required for barcode scanning and food photos.", Toast.LENGTH_LONG).show()
        }
    }

    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val results: Array<Uri>? = when {
                data?.data != null -> arrayOf(data.data!!)
                data?.clipData != null -> {
                    val count = data.clipData!!.itemCount
                    Array(count) { i -> data.clipData!!.getItemAt(i).uri }
                }
                cameraPhotoUri != null -> arrayOf(cameraPhotoUri!!)
                else -> null
            }
            filePathCallback?.onReceiveValue(results)
        } else {
            filePathCallback?.onReceiveValue(null)
        }
        filePathCallback = null
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check & prompt camera permission upfront so live camera viewfinder works immediately
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        webView = WebView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#060911"))
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                loadWithOverviewMode = true
                useWideViewPort = true
                cacheMode = WebSettings.LOAD_DEFAULT
                mediaPlaybackRequiresUserGesture = false
            }

            addJavascriptInterface(WebAppInterface(), "Android")

            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView,
                    request: WebResourceRequest
                ): WebResourceResponse? {
                    return assetLoader.shouldInterceptRequest(request.url)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Log.d("KYF_WebView", "Page loaded: $url")
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    consoleMessage?.let {
                        Log.d("KYF_JS", "[${it.messageLevel()}] ${it.message()} (line ${it.lineNumber()} of ${it.sourceId()})")
                    }
                    return true
                }

                override fun onPermissionRequest(request: PermissionRequest?) {
                    runOnUiThread {
                        if (request == null) return@runOnUiThread
                        pendingPermissionRequest = request

                        val hasCamera = ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasCamera) {
                            request.grant(request.resources)
                            pendingPermissionRequest = null
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                }

                override fun onPermissionRequestCanceled(request: PermissionRequest?) {
                    pendingPermissionRequest = null
                }

                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    this@MainActivity.filePathCallback?.onReceiveValue(null)
                    this@MainActivity.filePathCallback = filePathCallback

                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    val photoFile = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        null
                    }
                    if (photoFile != null) {
                        cameraPhotoUri = FileProvider.getUriForFile(
                            this@MainActivity,
                            "${applicationContext.packageName}.fileprovider",
                            photoFile
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoUri)
                    }

                    val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "image/*"
                    }

                    val intentArray: Array<Intent> = if (photoFile != null) arrayOf(takePictureIntent) else emptyArray()

                    val chooserIntent = Intent(Intent.ACTION_CHOOSER).apply {
                        putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                        putExtra(Intent.EXTRA_TITLE, "Select Image or Take Photo")
                        putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                    }

                    fileChooserLauncher.launch(chooserIntent)
                    return true
                }
            }

            loadUrl("https://appassets.androidplatform.net/assets/index.html")
        }

        setContentView(webView)

        // Native Android Back Navigation connected to React Navigation Stack
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                webView.evaluateJavascript("window.handleAndroidBack ? window.handleAndroidBack() : false") { result ->
                    val handled = result?.replace("\"", "")?.trim() == "true"
                    if (!handled) {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                        isEnabled = true
                    }
                }
            }
        })
    }

    inner class WebAppInterface {
        @JavascriptInterface
        fun requestCameraPermission() {
            runOnUiThread {
                if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        }

        @JavascriptInterface
        fun vibrate(durationMs: Long) {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as? Vibrator
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(durationMs)
                }
            }
        }

        @JavascriptInterface
        fun showToast(message: String) {
            runOnUiThread {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        }

        @JavascriptInterface
        fun shareText(title: String, text: String) {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TITLE, title)
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, title))
        }
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            webView.destroy()
        }
        super.onDestroy()
    }
}
