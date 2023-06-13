package com.qr.code

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.text.method.LinkMovementMethod
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
import com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {

    lateinit var img_qrcode: ImageView
    lateinit var input_name: EditText
    lateinit var listQRCodeData: MutableList<String>
  //  lateinit var txt_company_name:TextView
    lateinit var btn_generateQRCode: Button
    lateinit var btn_save_qrcode: Button
    lateinit var btn_company_name:Button

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
        input_name = findViewById(R.id.input_link)
       // txt_company_name = findViewById(R.id.txt_company_name)
        listQRCodeData = mutableListOf(String())
        //var txt_input_name = input_name.text.toString()
       // val qrcode_save = QRGSaver()
        btn_generateQRCode = findViewById(R.id.btn_GenerateQRCode)
        btn_save_qrcode = findViewById(R.id.btn_save_qrcode)
        btn_company_name = findViewById(R.id.btn_company_name)

      //  txt_company_name.setMovementMethod(LinkMovementMethod.getInstance())

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

                if (input_name.text.isNotEmpty()) {
                    val url = "${input_name.text}" // URL desejada

                    val barcodeEncoder = BarcodeEncoder()
                    try {
                        val bitmap =
                            barcodeEncoder.encodeBitmap(url, BarcodeFormat.QR_CODE, 400, 400)

                        img_qrcode.setImageBitmap(bitmap)
                    } catch (e: WriterException) {
                        e.printStackTrace()
                    }
                }
            }
        }

            fun saveQrcodeImage() {
                val drawable = img_qrcode.drawable
                val bitmap = drawable.toBitmap()

                // Verificar se o armazenamento externo está disponível para escrita
                if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                    val directory = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath)
                    val directoryPath = directory.absolutePath

                    val fileName = "qrcode.jpeg"
                    val imageFile = File(directoryPath, fileName)

                    if (directory.exists()) {
                        //diretorio ja existe
                        FileOutputStream(imageFile).use { outputStream ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                            outputStream.flush();
                            outputStream.close();
                        }
                        Toast.makeText(
                            applicationContext,
                            "Image saved successfully!",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        val isDirectoryCreated = directory.mkdirs()
                        if (isDirectoryCreated) {
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

                        } else {
                            //falha ao criar o diretorio
                            Toast.makeText(
                                applicationContext,
                                "Failed to save image",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                } else {
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
                } else {
                    // Caso a permissão não tenha sido concedida, solicitar permissão
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1
                    )
                    saveQrcodeImage()
                }
            }

            fun openProfileDeveloper()
            {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/company/michi-in/")))
               /* val url = "https://www.linkedin.com/in/laura-oliveira-mobile/"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)

                val urlCompanyName = "${txt_company_name.text}https://www.linkedin.com/company/michi-in/"
                txt_company_name.setMovementMethod(LinkMovementMethod.getInstance())*/
            }

      /*  fun openProfileDeveloper() {
            txt_company_name.setMovementMethod(LinkMovementMethod.getInstance())
        }*/

            fun checkInternetPermission()
            {
            // Verificar se a permissão já foi concedida
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                // Permissão já concedida, pode prosseguir com a ação que requer acesso à Internet

              //  openProfileDeveloper()
            } else {
                // Caso a permissão não tenha sido concedida, solicitar permissão
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET), REQUEST_CODE)
              //  openProfileDeveloper()
            }

            }


              btn_company_name.setOnClickListener {
                  try {
                      openProfileDeveloper()
                  } catch (e: WriterException) {
                      e.printStackTrace()
                      Log.d("erro abrir link", e.toString())
                  }


              }
        }
    }
