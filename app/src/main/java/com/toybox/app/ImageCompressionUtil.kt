package com.toybox.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageCompressionUtil {
    
    /**
     * Compress image to max 800x800px, JPEG quality 60
     * Returns the File path of the compressed image
     */
    fun compressImage(context: Context, uri: Uri): String {
        try {
            // Open input stream from URI
            val inputStream = context.contentResolver.openInputStream(uri) 
                ?: throw Exception("Cannot open input stream")
            
            // Decode bitmap with inSampleSize to reduce memory
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()
            
            // Calculate inSampleSize
            val inSampleSize = calculateInSampleSize(options, 800, 800)
            
            // Decode actual bitmap
            val inputStream2 = context.contentResolver.openInputStream(uri) 
                ?: throw Exception("Cannot open input stream")
            val finalOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
            }
            val bitmap = BitmapFactory.decodeStream(inputStream2, null, finalOptions) 
                ?: throw Exception("Failed to decode bitmap")
            inputStream2.close()
            
            // Scale bitmap to max 800x800
            val scaledBitmap = scaleBitmap(bitmap, 800, 800)
            
            // Save as JPEG quality 60
            val toyDir = File(context.getExternalFilesDir("Toy_Images"), "").apply { mkdirs() }
            val outputFile = File(toyDir, "toy_${System.currentTimeMillis()}.jpg")
            
            FileOutputStream(outputFile).use { fos ->
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, fos)
                fos.flush()
            }
            
            // Clean up
            if (!bitmap.isRecycled) bitmap.recycle()
            if (scaledBitmap != bitmap && !scaledBitmap.isRecycled) scaledBitmap.recycle()
            
            return outputFile.absolutePath
        } catch (e: Exception) {
            throw Exception("Failed to compress image: ${e.message}", e)
        }
    }
    
    /**
     * Calculate optimal inSampleSize for bitmap subsampling
     */
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        
        return inSampleSize
    }
    
    /**
     * Scale bitmap to fit within max dimensions
     */
    private fun scaleBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        if (width <= maxWidth && height <= maxHeight) {
            return bitmap
        }
        
        val scale = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    
    /**
     * Delete image file from storage
     */
    fun deleteImageFile(imagePath: String?): Boolean {
        if (imagePath.isNullOrEmpty()) return false
        
        return try {
            val file = File(imagePath)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}

