package com.example.quranrecorder.record

import android.content.ContentValues
import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.IOException

class RecordService(private var context: Context) {


    private val createAudioFileUri = {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, "recording_${System.currentTimeMillis()}.m4a")
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp4")
            put(
                MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/Recordings"
            )
            put(MediaStore.Audio.Media.IS_PENDING, 1)
        }

        resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    private var uri: Uri? = null
    private var recorder: MediaRecorder? = null

    @RequiresApi(Build.VERSION_CODES.S)
    val handleRecord = {


        val start = {
            uri = createAudioFileUri()
            val fileDescriptor = uri?.let { context.contentResolver.openFileDescriptor(it, "w") }

            try {
                recorder = MediaRecorder(context).apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile(fileDescriptor?.fileDescriptor)
                    prepare()
                    start()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        val stop = {
            recorder?.apply {
                stop()
                release()
            }

            uri?.let {
                val values = ContentValues().apply {
                    put(MediaStore.Audio.Media.IS_PENDING, 0)
                }
                context.contentResolver.update(it, values, null, null)
            }
        }
        Pair(start, stop);
    }

}