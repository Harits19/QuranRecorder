package com.example.quranrecorder.file

import android.content.Context
import android.util.Log
import com.example.quranrecorder.record.RecordService
import java.io.File

data class AudioFile(val id: Long, val file: File);

class FileService(private var context: Context) {

    fun getAudioFiles(): List<AudioFile> {
        val folder =
            File(context.filesDir, "")

        if (!folder.exists() || !folder.isDirectory) {
            return emptyList();
        }
        val files = folder.listFiles() ?: return emptyList()

        return files.filter { item -> item.extension == RecordService.EXTENSION }
            .mapNotNull { item ->
                try {
                    val id = item.name.split(".").first().toLong();
                    Log.i("getAudioFiles", "id ${id}")
                    Log.i("getAudioFiles", "name ${item.name}")

                    AudioFile(id = id, file = item)
                } catch (e: NumberFormatException) {
                    e.printStackTrace();
                    null

                }

            };
    }
}