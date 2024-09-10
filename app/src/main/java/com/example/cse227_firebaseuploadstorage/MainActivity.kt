package com.example.cse227_firebaseuploadstorage

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private lateinit var chooseImageBtn: Button
    private lateinit var uploadImageBtn: Button
    private lateinit var imageView: ImageView
    var fileUri: Uri? = null

    private lateinit var getImage: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chooseImageBtn = findViewById(R.id.idBtnChooseImage)
        uploadImageBtn = findViewById(R.id.idBtnUploadImage)
        imageView = findViewById(R.id.idIVImage)

        getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                fileUri = it
                //imageView.setImageURI(it)
            }
        }

        chooseImageBtn.setOnClickListener {
            // for uploading image
           // getImage.launch("image/*")

            // for uploading pdf file
            getImage.launch("application/pdf")
        }
 
        uploadImageBtn.setOnClickListener {
            uploadImage()
        }
    }

    private fun uploadImage() {
        fileUri?.let { uri ->
            val progressDialog = ProgressDialog(this).apply {
                setTitle("Uploading...")
                setMessage("Uploading your image...")
                show()
            }

            val storageRef: StorageReference = FirebaseStorage.getInstance().reference
                .child("images/${UUID.randomUUID()}")
            storageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Image Uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Failed to Upload Image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(applicationContext, "Please select an image first", Toast.LENGTH_SHORT).show()
        }
    }
}
