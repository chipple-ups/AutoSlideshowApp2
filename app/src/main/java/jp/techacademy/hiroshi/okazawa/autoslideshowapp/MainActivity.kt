package jp.techacademy.hiroshi.okazawa.autoslideshowapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.database.Cursor
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener  {


    private var mcursor: Cursor? = null
    private var mTimer: Timer? = null
    // タイマー用の時間のための変数
    private var mTimerSec = 0.0
    private var mHandler = Handler()


    private val PERMISSIONS_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        next_button.setOnClickListener(this)
        prev_button.setOnClickListener(this)
        startstop_button.setOnClickListener(this)


        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }
    }
    override fun onClick(v: View) {
        when (v.id) {
            R.id.next_button -> show_Next()
            R.id.prev_button -> show_Prev()
            R.id.startstop_button -> startStop()
        }
    }

    private fun show_Next(){
        Log.d("ANDROID","tap_showNext")


        val fieldIndex = mcursor!!.getColumnIndex(MediaStore.Images.Media._ID)
        val id = mcursor!!.getLong(fieldIndex)
        val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

        if(mcursor!!.isLast()){
                mcursor!!.moveToFirst()
            } else {
            mcursor!!.moveToNext()
        }
        imageView.setImageURI(imageUri)
        Log.d("ANDROID","imageUri")
    }
    private fun show_Prev(){

        val fieldIndex = mcursor!!.getColumnIndex(MediaStore.Images.Media._ID)
        val id = mcursor!!.getLong(fieldIndex)
        val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

        if(mcursor!!.isFirst()){
            mcursor!!.moveToLast()
        } else {
            mcursor!!.moveToPrevious()
        }
        imageView.setImageURI(imageUri)
        

    }
    private fun startStop(){

        //var fieldIndex = mcursor!!.getColumnIndex(MediaStore.Images.Media._ID)
        //var id = mcursor!!.getLong(fieldIndex)
        //var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

        if (mTimer == null){
            mTimer = Timer()
            mTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    mHandler.post {
                        //ここに記入
                        if(mcursor!!.isLast()){
                            mcursor!!.moveToFirst()
                        } else {
                            mcursor!!.moveToNext()
                        }
                        var fieldIndex = mcursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                        var id = mcursor!!.getLong(fieldIndex)
                        var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                        imageView.setImageURI(imageUri)
                        Log.d("ANDROID","imageUri")

                        next_button.isEnabled = false
                        prev_button.isEnabled = false
                        startstop_button.text = "停止"

                    }
                }
            }, 2000, 2000) // 最初に始動させるまで100ミリ秒、ループの間隔を100ミリ秒 に設定
        } else {
            mTimer!!.cancel()
            mTimer = null

            next_button.isEnabled = true
            prev_button.isEnabled = true

            startstop_button.text = "再生"

        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        mcursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目（null = 全項目）
                null, // フィルタ条件（null = フィルタなし）
                null, // フィルタ用パラメータ
                null // ソート (nullソートなし）
        )


        if (mcursor!!.moveToFirst()) {
            //do {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = mcursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = mcursor!!.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)


                imageView.setImageURI(imageUri)
                Log.d("ANDROID", "URI : " + imageUri.toString())
            //} while (mcursor.moveToNext())
        }


    }

    //nullのばあい」
    //mcursor.close()

}