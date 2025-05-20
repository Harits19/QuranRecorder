package com.example.quranrecorder.quran.ayah

import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.quranrecorder.file.AudioFile
import com.example.quranrecorder.quran.Ayah

@Composable
fun AyahView(audio: AudioFile?, selectedAyah: Long, ayah: Ayah) {

    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    val isPlaying = mediaPlayer != null;

    fun onClick() {
        val file= audio?.file ?: return

        if (isPlaying) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                start()
                setOnCompletionListener {
                    release()
                    mediaPlayer = null;
                }
            }
        } else {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    fun onLongPress() {
        val file= audio?.file

        Log.i("onLongPress", "$file on ayah ${ayah.text}")
        if (file == null) return
        file.delete();
        Log.i("onLongPress", "success delete")
        Toast.makeText(context, "Success delete ${ayah.number}", Toast.LENGTH_SHORT).show();
    }


    val isSelected = ayah.number == selectedAyah;
    ListItem(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                Log.i("detectTapGestures", "onTap")
                onClick()
            }, onLongPress = {
                onLongPress()
                Log.i("detectTapGestures", "onLongPress")

            })
        },
        headlineContent = {
            Text(
                text = ayah.text, textAlign = TextAlign.Right,
                fontSize = 32.sp,
                lineHeight = 64.sp,
                color = if (isSelected) Color.Blue else Color.Unspecified,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        supportingContent = {

            if (audio == null) return@ListItem;

            Row {
                Text(audio.file.name)
                Icon(Icons.Default.PlayArrow, contentDescription = "Play")
            }

        },

        )
    HorizontalDivider()
}


@Preview
@Composable
fun AyahViewPreview() {

    AyahView(
        audio = null,
        ayah = Ayah(
            ruku = 10,
            text = "AAAAA",
            page = 1,
            juz = 1,
            manzil = 1,
            numberInSurah = 1,
            number = 1,
            hizbQuarter = 1,
            sajda = 1,
        ),
        selectedAyah = 1,
    )
}