package com.work.news.view.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.work.news.R
import com.work.news.view.news.main.NewsActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_main_activity)

        start()

    }

    private fun start() {

        Handler().postDelayed({

            val nextIntent = Intent(this, NewsActivity::class.java)

            startActivity(nextIntent)

            this@SplashActivity.finish()

        }, DELAY_MILLIS)

    }

    companion object {

        private const val DELAY_MILLIS = 1300L

    }

}