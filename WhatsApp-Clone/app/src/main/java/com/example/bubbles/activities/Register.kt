package com.example.bubbles.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.example.bubbles.ChangeImageDialogFragment
import com.example.bubbles.R
import com.example.bubbles.Service
import com.example.bubbles.models.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.installations.FirebaseInstallations
import java.io.File

class Register : FragmentActivity(), ChangeImageDialogFragment.ChangeImageDialogListener {

    private lateinit var profile_image: ImageView;
    private lateinit var photoFile: File
    private val FILE_NAME = "photo.jpg"
    private var ImageUri: Uri? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        Service.fetchUsers()
        profile_image = findViewById(R.id.iv_profile_image)
    }

    fun register(view: View) {
        val email = findViewById<TextInputEditText>(R.id.tit_email_login)
        val name = findViewById<TextInputEditText>(R.id.tit_name)
        if (email.text!!.isEmpty()) {
            val toast = Toast.makeText(applicationContext, "Email is required", Toast.LENGTH_SHORT).show()
            return
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text).matches()) {
            val toast = Toast.makeText(applicationContext, "Incorrect email format", Toast.LENGTH_SHORT).show()
            return
        }
        else if (name.text!!.isEmpty()) {
            val toast = Toast.makeText(applicationContext, "Name is required", Toast.LENGTH_SHORT).show()
            return
        }
        else {
            var imageName: String? = null
            if(ImageUri != null) {
                imageName = Service.uploadImage(ImageUri!!)
                if (imageName == "") Toast.makeText(applicationContext, "Image upload failed", Toast.LENGTH_SHORT).show()
                else Toast.makeText(applicationContext, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
            }
            val user = User(email.text.toString(), name.text.toString(), imageName)
            Service.addUser(user)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun test() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun changeProfileImage(view: View) {
        val dialog = ChangeImageDialogFragment("Take a photo with the camera or upload an image from the gallery", "Camera", "Gallery")
        dialog.show(supportFragmentManager, "ChangeImageDialogFragment")
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_SELECT)
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
       val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(applicationContext, "Could Not Open Camera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getPhotoFile(filenName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(filenName, ".jpg", storageDirectory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            ImageUri = Service.getImageUri(this, imageBitmap)
            profile_image.setImageBitmap(imageBitmap)
        }
        else if (requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK && data != null && data.data != null) {
            ImageUri = data.data!!
            profile_image.setImageURI(ImageUri)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        val REQUEST_IMAGE_CAPTURE = 1001
        val REQUEST_IMAGE_SELECT = 1002
        val PERMISSION_CODE_GALERY = 1003
        val PERMISSION_CODE_CAMERA = 1004
    }

    fun enterLogin(view: View) {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }
}