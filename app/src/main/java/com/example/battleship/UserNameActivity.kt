package com.example.battleship

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts

class UserNameActivity : AppCompatActivity() {

    val app = MyApplication.getAppInstance()
    lateinit var name: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Делаем фул-скрин
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        supportActionBar?.hide()

        setContentView(R.layout.activity_user_name)

        name = findViewById(R.id.name)
        name.requestFocus()
    }

    fun saveName(view: View) {
        if (name.text.isNotBlank()) {
            app.userName = name.text.toString()
            startActivity(
                Intent(this, InstallBoatsActivity::class.java)
            )
        } else {
            showError()
        }

    }

    private fun showError() {
        AlertDialog.Builder(this)
            .setMessage("Имя не может быть пустым")
            .setPositiveButton(
                "Ok"
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}