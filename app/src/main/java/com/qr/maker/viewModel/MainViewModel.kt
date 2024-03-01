package com.qr.maker.viewModel

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import android.Manifest
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

class MainViewModel(private val context: Context) : EasyPermissions.PermissionCallbacks {

    private val REQUEST_CODE_WRITE_STORAGE = 123

    fun requestImageSavePermission() {
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (EasyPermissions.hasPermissions(context, *permissions)) {
            // Permissões já concedidas, prosseguir com a operação desejada.
            saveImageToGallery()
        }
        //        else {
//            // Permissões não concedidas, solicitar permissões.
//            EasyPermissions.requestPermissions(
//                EasyPermissions.Builder(context, REQUEST_CODE_WRITE_STORAGE, *permissions)
//                    .setPositiveButtonText("Permitir")
//                    .setNegativeButtonText("Cancelar")
//                    .setRationale("A permissão é necessária para salvar imagens.")
//                    .build()
//            )
//        }
    }

    private fun saveImageToGallery() {
        val imageName =
            "image_${System.currentTimeMillis()}.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
        }

        val contentResolver = context.contentResolver
        val imageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let {
            contentResolver.openOutputStream(it).use { outputStream ->
                // Aqui você pode salvar a imagem no outputStream
                // Exemplo: outputStream?.write(bytesDaSuaImagem)

                Toast.makeText(context, "Imagem salva com sucesso", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        TODO("Not yet implemented")
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        // Permissões concedidas, prosseguir com a operação desejada.
        if (requestCode == REQUEST_CODE_WRITE_STORAGE) {
            saveImageToGallery()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        // Permissões negadas, exibir um diálogo ou mensagem informando sobre a importância das permissões
//        if (EasyPermissions.somePermissionPermanentlyDenied(context, perms)) {
//            // Mostrar um diálogo para levar o usuário às configurações de aplicativos para conceder permissões manualmente.
//            EasyPermissions.openSettings(context)
//        }
    }
}
