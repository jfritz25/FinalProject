import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.example.finalproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var imageView: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var captureButton: Button
    private var imageCapture: ImageCapture? = null
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    private lateinit var outputDirectory: File
    private lateinit var viewFinder: PreviewView


    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageView.setImageURI(uri)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        imageView = findViewById(R.id.profile)
        nameEditText = findViewById(R.id.name)
        emailEditText = findViewById(R.id.email)
        signUpButton = findViewById(R.id.signup)
        captureButton = findViewById(R.id.capture)
        passwordEditText = findViewById(R.id.password)

        imageView.setOnClickListener { view ->
            showImagePickerDialog()
        }

        signUpButton.setOnClickListener { view ->
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val name = nameEditText.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser

                        val database = FirebaseDatabase.getInstance().reference

                        val userMap = HashMap<String, Any>()
                        userMap["name"] = name
                        userMap["email"] = email

                        val storageRef = user?.let {
                            FirebaseStorage.getInstance().reference.child("profileImages").child(
                                it.uid)
                        }
                        imageView.isDrawingCacheEnabled = true
                        imageView.buildDrawingCache()
                        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                        val baos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data = baos.toByteArray()

                        var uploadTask = storageRef?.putBytes(data)
                        uploadTask?.addOnFailureListener {
                        }?.addOnSuccessListener { taskSnapshot ->
                            val downloadUrl = taskSnapshot.metadata!!.reference!!.downloadUrl
                            downloadUrl.addOnSuccessListener { uri ->
                                userMap["profileImage"] = uri.toString()

                                user?.let { database.child("users").child(it.uid) }
                                    ?.setValue(userMap)
                            }
                        }
                    } else {

                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }


        captureButton.setOnClickListener{view ->
            takePhoto()
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Choose from Gallery", "Camera")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> getContent.launch("image/*")
                1 -> startCamera()
            }
        }
        builder.show()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

                captureButton.visibility = View.VISIBLE

            } catch(exc: Exception) {
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        outputDirectory = getOutputDirectory()
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis()) + ".jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    imageView.setImageURI(savedUri)
                }
                override fun onError(exc: ImageCaptureException) {

                }
            })
    }
}
