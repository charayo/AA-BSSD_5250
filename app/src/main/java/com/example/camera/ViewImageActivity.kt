package com.example.camera

import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.android.material.button.MaterialButton

class ViewImageActivity : AppCompatActivity() {
    private  lateinit var imagePreview: ImageView
    private  lateinit var asciiAlert: TextView

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fileUri = Uri.parse(intent.getStringExtra("filePath"))
        imagePreview = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1.8f
            ).apply {
                setPadding(0,10,0,30)
            }

            setImageURI(fileUri)
        }

        val saveButton = Button(this).apply {
            text = "Save"
            setOnClickListener {
                MediaScannerConnection.scanFile(applicationContext,arrayOf(fileUri.toString()),null, null)
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            ).apply {
                setMargins(5,5,5,5)
            }
        }
        val closeButton = MaterialButton(this).apply {
            text = "Discard"
            setOnClickListener {
                finish()
            }
            setBackgroundColor(Color.RED)
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            ).apply {
                setMargins(5,5,5,5)
            }
        }
        val asciiButton = MaterialButton(this).apply {
            text = "View ASCII"
            setOnClickListener {
                editBitmap(intent.getStringExtra("filePath"))
                android.app.AlertDialog.Builder(context).apply {
                    setTitle("ASCII")
                    setMessage(asciiAlert.text)
                    setNegativeButton("Close", null) // do nothing if they say no
                    create()
                    show()
                }
            }

            setBackgroundColor(Color.BLACK)
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            ).apply {
                setMargins(5,5,5,5)
            }

        }
        val buttonGroup = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                0.2f
            )
            weightSum = 3f
            orientation = LinearLayout.HORIZONTAL
            addView(saveButton)
            addView(asciiButton)
            addView(closeButton)
        }
        val mainLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
            )
            weightSum = 2f
            orientation = LinearLayout.VERTICAL
            addView(buttonGroup)
            addView(imagePreview)
        }

        setContentView(mainLayout)
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun editBitmap(filePath: String?) {
        val orig = BitmapFactory.decodeFile(filePath)
        val bmp = Bitmap.createScaledBitmap(
            orig, orig.width / 32,
            orig.height / 32, true
        )
        val w = bmp.width
        val h = bmp.height
        var outputString = ""
        for (y in 0..h - 1) { //for all the pixels in the bmp
            for (x in 0..w - 1) {
                var currColor: Int = (bmp.getColor(x, y).red() * 255).toInt()
                currColor += (bmp.getColor(x, y).blue() * 255).toInt()
                currColor += (bmp.getColor(x, y).green() * 255).toInt()
                currColor /= 3 //average of r,g,b
                if (currColor < 255 / 4) {
                    outputString += "_"
                } else if (currColor < (255 / 4) * 2) {
                    outputString += "+"
                } else if (currColor < (255 / 4) * 3) {
                    outputString += "!"
                } else {
                    outputString += "@"
                }

            }
            outputString += "\n"
        }
        Log.d("Mact", outputString)

        val tv = TextView(this).apply {
            text = outputString
            typeface = Typeface.MONOSPACE
        }
        asciiAlert = tv
    }

}