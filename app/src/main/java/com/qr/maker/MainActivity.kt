package com.qr.maker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.Display
import android.view.WindowManager
import android.widget.*
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.qr.code.maker.R
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {

    lateinit var img_qrcode: ImageView
    lateinit var input_name: EditText
    lateinit var listQRCodeData: MutableList<String>
    lateinit var btn_generateQRCode: Button
    lateinit var btn_save_qrcode: Button
    lateinit var btn_company_name:Button

    lateinit var bitmap: Bitmap
    lateinit var qrEncoder: QRGEncoder


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        img_qrcode = findViewById(R.id.img_qrcode)
        input_name = findViewById(R.id.input_link)
        listQRCodeData = mutableListOf(String())
        btn_generateQRCode = findViewById(R.id.btn_GenerateQRCode)
        btn_save_qrcode = findViewById(R.id.btn_save_qrcode)
        btn_company_name = findViewById(R.id.btn_company_name)

        btn_generateQRCode.setOnClickListener {
            // on below line we are checking if msg edit text is empty or not.
            if (TextUtils.isEmpty(input_name.text.toString()))
            {
                //if the message box is empty the message below will appear for the user input some data
                Toast.makeText(applicationContext, "Insira um Link", Toast.LENGTH_SHORT).show()
            }
            else
            {
                // getting service for window manager
                val windowManager: WindowManager = getSystemService(WINDOW_SERVICE) as WindowManager

                // initializing a variable for our default display
                val display: Display = windowManager.defaultDisplay

                // creating a variable for point which is use to display in qr code
                val point: Point = Point()
                display.getSize(point)

                // getting height and width of our point
                val width = point.x
                val height = point.y

                // generating dimensions for width and height
                var dimen = if (width < height) width else height
                dimen = dimen * 3 / 4

                listQRCodeData.add(1, input_name.toString())

                qrEncoder = QRGEncoder(
                    listQRCodeData.toString(), null,
                    QRGContents.Type.TEXT, dimen
                )

                if (input_name.text.isNotEmpty()) {
                    val url = "${input_name.text}" // URL desejada

                    val barcodeEncoder = BarcodeEncoder()
                    try {
                        bitmap =
                            barcodeEncoder.encodeBitmap(url, BarcodeFormat.QR_CODE, 400, 400)

                        img_qrcode.setImageBitmap(bitmap)
                    } catch (e: WriterException) {
                        e.printStackTrace()
                    }
                }
            }
        }

            fun saveQrcodeImage()
            {
                val drawable = img_qrcode.drawable
                val bitmap = drawable.toBitmap()

                // Checks if the external storage is available to write
                if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
                {
                    val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath)
                    val directoryPath = directory.absolutePath
                    val fileName = "qrcode.jpeg"
                    val imageFile = File(directoryPath, fileName)

                    if (directory.exists())
                    {
                        //the directory exists
                        FileOutputStream(imageFile).use { outputStream ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                            outputStream.flush();
                            outputStream.close();
                        }
                        Toast.makeText(applicationContext, "Image saved successfully!", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        val isDirectoryCreated = directory.mkdirs()
                        if (isDirectoryCreated)
                        {
                            //directory created
                            FileOutputStream(imageFile).use { outputStream ->
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                                outputStream.flush();
                                outputStream.close();
                            }

                            Log.d("Diretorio", directoryPath)
                            Toast.makeText(applicationContext, "Image saved successfully!", Toast.LENGTH_LONG).show()

                        }
                        else
                        {
                            //Error creating the directory
                            Toast.makeText(applicationContext, "Failed to save image", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
                else
                {
                    Toast.makeText(applicationContext, "External storage not available", Toast.LENGTH_SHORT).show()
                }
            }

            btn_save_qrcode.setOnClickListener {
                val drawable = img_qrcode.drawable;

                // Checks if the application have permission to write
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission granted, move on to save the qrcode image
                    saveQrcodeImage()
                } else {
                    // If the application doesn`t have permission to write, ask for permission with default pop up message
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1
                    )
                    // Permission granted, move on to save the qrcode image
                    saveQrcodeImage()
                }
            }

            fun openProfileCompany()
            {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/company/michi-in/")))
            }

              btn_company_name.setOnClickListener {
                  try {
                      openProfileCompany()
                  } catch (e: WriterException) {
                      e.printStackTrace()
                      Log.d("Error trying to open the link", e.toString())
                  }


              }
        }
    }
