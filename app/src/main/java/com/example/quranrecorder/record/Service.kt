package com.example.quranrecorder.record

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileOutputStream

class RecordService(private var context: Context) {

    companion object {
        const val EXTENSION = "wav"
    }


    private var thread: Thread? = null;

    fun start(currentAyah: Long) {

        Log.i("start", "start record ayah ${currentAyah}")

        val file = File(context.filesDir, "${currentAyah}.${RecordService.EXTENSION}")

        val sampleRate = 44100
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        val isGranted = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED;
        if (isGranted) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, bufferSize
        )

        val outputStream = FileOutputStream(file)
        val buffer = ByteArray(bufferSize)

        thread = Thread {
            try {
                audioRecord.startRecording()
                while (thread != null) {
                    val read = audioRecord.read(buffer, 0, buffer.size)
                    if (read > 0) outputStream.write(buffer, 0, read)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                audioRecord.stop()
                audioRecord.release()
                outputStream.close()
                val rawPcm = file.readBytes()
                val trimmedPcm = trimSilenceFromPCM(rawPcm, threshold = 200)
                Log.i("start", "save record ayah ${currentAyah}")

                saveWavFile(file, trimmedPcm, sampleRate)
            }
        }
        thread?.start();
    }

     fun stop() {
         Log.i("stop", "stop recorder")
        if (thread == null) return;
        thread = null

        Log.i("RecordService", "Recording stopped")
    }

    private fun trimSilenceFromPCM(pcmData: ByteArray, threshold: Int): ByteArray {
        fun isSampleLoud(i: Int): Boolean {
            val sample =
                ((pcmData[i + 1].toInt() shl 8) or (pcmData[i].toInt() and 0xFF)).toShort()
            return kotlin.math.abs(sample.toInt()) > threshold
        }

        var start = 0
        while (start < pcmData.size - 2) {
            if (isSampleLoud(start)) break
            start += 2
        }

        var end = pcmData.size - 2
        while (end > start) {
            if (isSampleLoud(end)) break
            end -= 2
        }

        return if (start >= end) ByteArray(0) else pcmData.copyOfRange(start, end + 2)
    }

    private fun saveWavFile(file: File, pcmData: ByteArray, sampleRate: Int) {
        val header = createWavHeader(pcmData.size, sampleRate, 1, 16)
        FileOutputStream(file).use { out ->
            out.write(header)
            out.write(pcmData)
        }
    }

    private fun createWavHeader(
        totalAudioLen: Int,
        sampleRate: Int,
        channels: Int,
        bitsPerSample: Int
    ): ByteArray {
        val totalDataLen = totalAudioLen + 36
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val header = ByteArray(44)

        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = 16
        header[16] = 0
        header[17] = 0
        header[18] = 0
        header[19] = 1
        header[20] = 0
        header[21] = channels.toByte()
        header[22] = 0
        header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = ((sampleRate shr 8) and 0xff).toByte()
        header[26] = ((sampleRate shr 16) and 0xff).toByte()
        header[27] = ((sampleRate shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = ((channels * bitsPerSample) / 8).toByte()
        header[33] = 0
        header[34] = bitsPerSample.toByte()
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = ((totalAudioLen shr 8) and 0xff).toByte()
        header[42] = ((totalAudioLen shr 16) and 0xff).toByte()
        header[43] = ((totalAudioLen shr 24) and 0xff).toByte()

        return header
    }
}