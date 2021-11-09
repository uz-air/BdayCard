package com.example.bdaycard


import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Button
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.math.absoluteValue


class MainActivity : AppCompatActivity(), Balloon.BalloonListener {
    private val mBalloonColors = IntArray(8)
    private val MIN_ANIMATION_DELAY = 500
    private val MAX_ANIMATION_DELAY = 1500
    private val MIN_ANIMATION_DURATION = 1000
    private val MAX_ANIMATION_DURATION = 8000
    private val BALLOONS_PER_LEVEL = 100
    private var mContentView: ViewGroup? = null
    private var mNextColor = 0
    private var mScreenWidth: Int = 10
    private var mScreenHeight: Int = 0
    private val mBalloons: MutableList<Balloon> = ArrayList()
    var mGoButton: Button? = null

    private var mPlaying = false
    private var mGameStopped = true
    private var mLevel = 4
    private var mScore: Int = 0
    private var mPinsUsed: Int = 0
    private var mBalloonsPopped = 0
    private val mSoundHelper: SoundHelper by lazy {
        SoundHelper(this).apply {
            prepareMusicPlayer(
                this@MainActivity
            )
        }
    }

    private fun gameOver() {
        mSoundHelper.pauseMusic()
        Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show()
        for (balloon in mBalloons) {
            mContentView?.removeView(balloon)
            balloon.setPopped(true)
        }
        mBalloons.clear()
        mPlaying = false
        mGameStopped = true
        mGoButton!!.text = "Start Game"
    }

    //private val mSoundHelper: SoundHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBalloonColors[0] = Color.argb(255, 255, 0, 0)
        mBalloonColors[1] = Color.argb(255, 0, 255, 0)
        mBalloonColors[2] = Color.argb(255, 0, 255, 100)
        mBalloonColors[3] = Color.argb(255, 100, 255, 0)
        mBalloonColors[4] = Color.argb(255, 0, 0, 255)
        mBalloonColors[5] = Color.argb(255, 100, 0, 255)
        mBalloonColors[6] = Color.argb(255, 0, 100, 255)
        mBalloonColors[7] = Color.argb(255, 100, 100, 255)
        mContentView = findViewById<View>(R.id.activity_main) as ViewGroup


        val viewTreeObserver = mContentView?.viewTreeObserver
        if (viewTreeObserver?.isAlive == true) {
            viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mContentView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    mScreenHeight = mContentView!!.height
                    mScreenWidth = mContentView!!.width
                }
            })
        }

    }


    override fun onStop() {
        super.onStop()
        gameOver()
    }

    override fun onResume() {
        super.onResume()

        val videoView = findViewById<View>(R.id.videoView) as VideoView

        //Creating MediaController

        //Creating MediaController
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)

        //specify the location of media file
        val path = "android.resource://" + packageName + "/" + R.raw.orange
        //specify the location of media file
        val uri = Uri.parse(path)

        //Setting MediaController and URI, then starting the videoView
        videoView.setOnClickListener {
            if (videoView.isPlaying) videoView.pause()
            else {
                videoView.start()
            }
        }
        videoView.setOnCompletionListener {
            val balloonLauncher: BalloonLauncher =
                BalloonLauncher()
            balloonLauncher.execute(mLevel)
        }
        //Setting MediaController and URI, then starting the videoView
        //videoView.setMediaController(mediaController)
        videoView.setVideoURI(uri)
        videoView.requestFocus()
        // videoView.start()
        mScore = 0
        mPinsUsed = 0
        mGameStopped = false
        mPlaying = true
        mBalloonsPopped = 0


    }

    private inner class BalloonLauncher : AsyncTask<Int?, Int?, Void?>() {
        override fun doInBackground(vararg params: Int?): Void? {
            val level = params[0] ?: 4
            val maxDelay: Int =
                MIN_ANIMATION_DELAY.coerceAtLeast(MAX_ANIMATION_DELAY - (level - 1) * 500)
            val minDelay = maxDelay / 2
            var balloonsLaunched = 0
            while (balloonsLaunched < BALLOONS_PER_LEVEL) {

//              Get a random horizontal  position for the next balloon
                val random = Random()
                val xPosition = random.nextInt(mScreenWidth.absoluteValue.minus(200).absoluteValue)
                publishProgress(xPosition)
                balloonsLaunched++

//              Wait a random number of milliseconds before looping
                val delay = random.nextInt(minDelay) + minDelay
                try {
                    Thread.sleep(delay.toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            return null
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            val xPosition = values[0]
            if (xPosition != null) {
                launchBalloon(xPosition)
            }
        }
    }

    private fun launchBalloon(x: Int) {
        val balloon = Balloon(this, mBalloonColors[mNextColor], 150)
        mBalloons.add(balloon)
        if (mNextColor + 1 == mBalloonColors.size) {
            mNextColor = 0
        } else {
            mNextColor++
        }

//      Set balloon vertical position and dimensions, add to container
        balloon.x = x.toFloat()
        balloon.y = (mScreenHeight + balloon.height).toFloat()
        mContentView?.addView(balloon)

//      Let 'er fly
        val duration: Int =
            MIN_ANIMATION_DURATION.coerceAtLeast(MAX_ANIMATION_DURATION - mLevel * 1000)
        balloon.releaseBalloon(mScreenHeight, duration)
    }

    override fun popBalloon(balloon: Balloon?, userTouch: Boolean) {
        mSoundHelper.playSound()
        mContentView?.removeView(balloon)
        mBalloons.remove(balloon)
    }


}