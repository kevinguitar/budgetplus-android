package com.kevingt.moneybook.book

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Button
import androidx.compose.material.Text
import com.kevingt.moneybook.auth.ui.AuthActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Button(onClick = {
                startActivity(Intent(this, AuthActivity::class.java))
            }) {
                Text(text = "Open Auth")
            }
        }
    }
}