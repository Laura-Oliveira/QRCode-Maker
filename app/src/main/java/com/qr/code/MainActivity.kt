package com.qr.code

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.Display
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidmads.library.qrgenearator.QRGSaver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import java.io.File
import java.io.FileOutputStream
import android.Manifest
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import androidx.core.view.drawToBitmap


class MainActivity : AppCompatActivity() {

    lateinit var img_qrcode: ImageView
    lateinit var input_name: EditText
    lateinit var listQRCodeData: MutableList<String>
    lateinit var btn_generateQRCode: Button
    lateinit var btn_save_qrcode: Button

    // on below line we are creating
    // a variable for bitmap
    lateinit var bitmap: Bitmap

    // on below line we are creating
    // a variable for qr encoder.
    lateinit var qrEncoder: QRGEncoder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        img_qrcode = findViewById(R.id.img_qrcode)
        input_name = findViewById(R.id.input_name)
        listQRCodeData = mutableListOf(String())
        val qrcode_save = QRGSaver()
        btn_generateQRCode = findViewById(R.id.btn_GenerateQRCode)
        btn_save_qrcode = findViewById(R.id.btn_save_qrcode)

        btn_generateQRCode.setOnClickListener {
            // on below line we are checking if msg edit text is empty or not.
            if (TextUtils.isEmpty(input_name.text.toString())) {
                //if the message box is empty the message below will appear for the user input some data
                Toast.makeText(applicationContext, "Insira um Link", Toast.LENGTH_SHORT).show()
            } else {
                // on below line we are getting service for window manager
                val windowManager: WindowManager = getSystemService(WINDOW_SERVICE) as WindowManager

                // on below line we are initializing a
                // variable for our default display
                val display: Display = windowManager.defaultDisplay

                // on below line we are creating a variable
                // for point which is use to display in qr code
                val point: Point = Point()
                display.getSize(point)

                // on below line we are getting
                // height and width of our point
                val width = point.x
                val height = point.y

                // on below line we are generating
                // dimensions for width and height
                var dimen = if (width < height) width else height
                dimen = dimen * 3 / 4

                listQRCodeData.add(1, input_name.toString())
                // listQRCodeData.add(2, input_lastame.toString())

                qrEncoder = QRGEncoder(
                    listQRCodeData.toString(), null,
                    QRGContents.Type.TEXT, dimen
                )
                // img_qrcode.Color.BLUE
                // on below line we are initializing our qr encoder
//                QRGContents()

                // on below line we are running a try
                // and catch block for initializing our bitmap
                try {
                    // on below line we are
                    // initializing our bitmap
                    bitmap = qrEncoder.bitmap
                    //  encodeAsBitmap()

                    // on below line we are setting
                    // this bitmap to our image view
                    img_qrcode.setImageBitmap(bitmap)

                    //save qrcode as a jpeg image
                } catch (e: Exception) {
                    // on below line we
                    // are handling exception
                    e.printStackTrace()
                }
            }
        }

/*        btn_save_qrcode.setOnClickListener {
            val drawable = img_qrcode.drawable;
            val bitmap = drawable.toBitmap()
            val qrcode_fileName = "qrCodeScreenshot";

            qrcode_save.save(
                LOCATION_SERVICE,
                qrcode_fileName,
                bitmap,
                QRGContents.ImageType.IMAGE_JPEG
            )

            val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "QRCode_Screenshots")
            val directoryPath = directory?.absolutePath
            if (directoryPath != null) {
                Log.e("Diretório da Imagem", directoryPath)
            }
            Toast.makeText(applicationContext, "Image saved successfully!" + directoryPath, Toast.LENGTH_SHORT)
                .show()
        }*/


        fun saveQrcodeImage() {
            val drawable = img_qrcode.drawable
            val bitmap = drawable.toBitmap()

            // Verificar se o armazenamento externo está disponível para escrita
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val directory = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath,
                    "QRCode"
                )
                val directoryPath = directory.absolutePath

                val fileName = "qrcodeImage.jpeg"
                val imageFile = File(directoryPath, fileName)

                if (directory.exists())
                {
                    //diretorio ja existe
                    FileOutputStream(imageFile).use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        outputStream.flush();
                        outputStream.close();
                    }
                    Toast.makeText(
                        applicationContext,
                        "Image saved successfully!" + "existente",
                        Toast.LENGTH_SHORT
                    ).show()

                }
                else
                {
                    val isDirectoryCreated = directory.mkdirs()
                    if (isDirectoryCreated)
                    {
                        //diretorio criado
                        FileOutputStream(imageFile).use { outputStream ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                            outputStream.flush();
                            outputStream.close();
                        }

                        Log.d("Diretorio", directoryPath)
                        Toast.makeText(
                            applicationContext,
                            "Image saved successfully!" + "criado",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                    else
                    {
                        //falha ao criar o diretorio
                        Toast.makeText(
                            applicationContext,
                            "Failed to save image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
            else
            {
                Toast.makeText(
                    applicationContext,
                    "External storage not available",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btn_save_qrcode.setOnClickListener {
            val drawable = img_qrcode.drawable;
            val bitmap = drawable.toBitmap()
            val qrcode_fileName = "qrcode"

            // Verificar se a permissão já foi concedida
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permissão já concedida, pode prosseguir com a ação
                saveQrcodeImage()
                Toast.makeText(applicationContext, "Image saved successfully!", Toast.LENGTH_LONG).show()
            }
            else
            {
                // Caso a permissão não tenha sido concedida, solicitar permissão
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                saveQrcodeImage()
            }
        }


        /*
        btn_save_qrcode.setOnClickListener {
            val drawable = img_qrcode.drawable
            val bitmap = drawable.toBitmap()

            // Verificar se o armazenamento externo está disponível para escrita
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                //  getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath(), "QRCode_Screenshots")
                val directoryPath = directory.absolutePath
                val fileName = "qrcodeImage.jpg"
                val imageFile = File(directory, fileName)

                if (!directory.exists())
                {
                    val isDirectoryCreated = directory.mkdirs()
                    if (isDirectoryCreated)
                    {
                        //diretorio criado
                        FileOutputStream(imageFile).use { outputStream ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                            outputStream.flush();
                            outputStream.close();
                        }

                        Log.d("Diretorio", directoryPath)
                        Toast.makeText(applicationContext, "Image saved successfully!", Toast.LENGTH_LONG).show()
                    } else
                    {
                        //falha ao criar o diretorio
                        Toast.makeText(applicationContext, "Failed to save image", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    //diretorio ja existe
                    FileOutputStream(imageFile).use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        outputStream.flush();
                        outputStream.close();
                    }
                    Toast.makeText(applicationContext, directoryPath , Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(applicationContext, "External storage not available", Toast.LENGTH_SHORT).show()
            }
        }
*/
    }


}