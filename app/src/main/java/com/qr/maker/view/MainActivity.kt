package com.qr.maker

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Display
import android.view.WindowManager
import android.widget.*
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.drawToBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.qr.code.R
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks
{

    private val REQUEST_CODE_WRITE_STORAGE = 123

    private lateinit var img_qrcode: ImageView
    private lateinit var input_name: EditText
    private lateinit var listQRCodeData: MutableList<String>
    private lateinit var btn_generateQRCode: Button
    private lateinit var btn_save_qrcode: Button
    private lateinit var btn_company_name: Button

    lateinit var bitmap: Bitmap
    lateinit var qrEncoder: QRGEncoder

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkAndRequestPermissions()

        img_qrcode = findViewById(R.id.img_qrcode)
        input_name = findViewById(R.id.input_link)
        listQRCodeData = mutableListOf(String())
        btn_generateQRCode = findViewById(R.id.btn_GenerateQRCode)
        btn_save_qrcode = findViewById(R.id.btn_save_qrcode)
        btn_company_name = findViewById(R.id.btn_company_name)

        btn_generateQRCode.setOnClickListener()
        {
            if (TextUtils.isEmpty(input_name.text.toString()))
            {
                Toast.makeText(applicationContext, "Insert a Link", Toast.LENGTH_SHORT).show()
            }
            else
            { generateAndDisplayQRCode() }
        }

        btn_save_qrcode.setOnClickListener()
        {
            val drawable = img_qrcode.drawable;
            checkAndRequestPermissions()
            saveImageToGallery(drawable.toBitmap())
        }

        btn_company_name.setOnClickListener()
        {
            try {
                openProfileCompany()
            } catch (e: WriterException) {
                e.printStackTrace()
                Log.d("Error trying to open the link", e.toString())
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (EasyPermissions.hasPermissions(this, *permissions)) {
            // As permissões já foram concedidas

        } else {
            // Solicitar permissões
            EasyPermissions.requestPermissions(
                this,
                "É necessário conceder permissão para salvar a imagem.",
                REQUEST_CODE_WRITE_STORAGE,
                *permissions
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Passar os resultados para EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        // Permissões concedidas, proceda com a operação desejada
        if (requestCode == REQUEST_CODE_WRITE_STORAGE) {
            //   saveImageToGallery()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        // Permissões negadas, exibir um diálogo ou mensagem informando sobre a importância das permissões
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    private fun generateAndDisplayQRCode()
    {
        if (TextUtils.isEmpty(input_name.text.toString()))
        {
            Toast.makeText(applicationContext, "Insert a Link", Toast.LENGTH_SHORT).show()
        }
        else
        {
            // Obtém o serviço WindowManager.
            val windowManager: WindowManager = getSystemService(WINDOW_SERVICE) as WindowManager

            // Inicializa uma variável para o display padrão.
            val display: Display = windowManager.defaultDisplay

            // Cria uma variável Point para exibir no código QR.
            val point = Point()
            display.getSize(point)

            // Obtém a largura e a altura da variável Point.
            val width = point.x
            val height = point.y

            // Gera dimensões para largura e altura.
            var dimen = if (width < height) width else height
            dimen = dimen * 3 / 4

            // Adiciona o texto do campo de entrada à listaQRCodeData.
            listQRCodeData.add(1, input_name.text.toString())

            // Inicializa o codificador QR.
            qrEncoder = QRGEncoder(listQRCodeData.toString(), null, QRGContents.Type.TEXT, dimen)

            if (input_name.text.isNotEmpty())
            {
                // Obtém a URL desejada do campo de texto.
                val url = "${input_name.text}"

                // Inicializa o codificador de barras.
                val barcodeEncoder = BarcodeEncoder()
                try {
                    // Codifica a URL em um bitmap QR Code.
                    bitmap = barcodeEncoder.encodeBitmap(url, BarcodeFormat.QR_CODE, 400, 400)

                    // Define o bitmap no ImageView para exibir o QR Code gerado.
                    img_qrcode.setImageBitmap(bitmap)
                    /* _____________________________________________SAVE THE QRCODE IMAGE INTO THE PICTURES FOLDER */
                   // saveImageToGallery(img_qrcode.drawToBitmap())
                }
                catch (e: WriterException) { e.printStackTrace() }
            }
        }
    }

    private fun saveImageToGallery(bitmap: Bitmap)
    {
        val imageName =
            "image_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
        }

        val contentResolver = contentResolver
        val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let {
            contentResolver.openOutputStream(it).use { outputStream ->
                if (outputStream != null)
                {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    Toast.makeText(this, "Image Successfully Saved!", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(applicationContext, "Failed to save image", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    fun openProfileCompany()
    {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.linkedin.com/company/michi-in/")
            )
        )
    }
}

