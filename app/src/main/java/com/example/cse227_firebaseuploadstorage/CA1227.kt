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

class CA1227 : AppCompatActivity() {
    private lateinit var chooseImageBtn: Button
    private lateinit var choosePdfBtn: Button
    private lateinit var uploadFileBtn: Button
    private lateinit var imageView: ImageView
    var fileUri: Uri? = null

    private lateinit var getImage: ActivityResultLauncher<String>
    private lateinit var getPdf: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ca1227)

        chooseImageBtn = findViewById(R.id.idBtnChooseImage)
        choosePdfBtn = findViewById(R.id.idBtnChoosePdf)
        uploadFileBtn = findViewById(R.id.idBtnUploadFile)
        imageView = findViewById(R.id.idIVImage)

        getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                fileUri = it
                // Display the selected image if it's an image file
                if (contentResolver.getType(it)?.startsWith("image/") == true) {
                    imageView.setImageURI(it)
                }
            }
        }

        getPdf = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                fileUri = it
                // Do not display PDF in the ImageView
                Toast.makeText(this, "PDF selected: ${fileUri?.path}", Toast.LENGTH_SHORT).show()
            }
        }

        chooseImageBtn.setOnClickListener {
            getImage.launch("image/*")
        }

        choosePdfBtn.setOnClickListener {
            getPdf.launch("application/pdf")
        }

        uploadFileBtn.setOnClickListener {
            uploadFile()
        }
    }

    private fun uploadFile() {
        fileUri?.let { uri ->
            val progressDialog = ProgressDialog(this).apply {
                setTitle("Uploading...")
                setMessage("Uploading your file...")
                show()
            }

            val storageRef: StorageReference = FirebaseStorage.getInstance().reference
                .child("uploads/${UUID.randomUUID()}")

            storageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "File Uploaded", Toast.LENGTH_SHORT).show()
                    imageView.setImageResource(0)
                    fileUri = null
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Failed to Upload File: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(applicationContext, "Please select a file first", Toast.LENGTH_SHORT).show()
        }
    }
}
