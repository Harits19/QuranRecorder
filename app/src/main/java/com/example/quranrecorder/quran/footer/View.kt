package com.example.quranrecorder.quran.footer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@Composable
fun FooterView(
    isRecording: Boolean,
    onClickNext: () -> Unit,
    onClickPrev: () -> Unit,
    onClickStop: () -> Unit,
    onClickStart: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {


        if (!isRecording) Button(onClick = onClickStart) {
            Text("Start Record")
        } else {
            Button(onClick = onClickPrev) {
                Text("Previous")

            }
            Button(onClick = onClickNext) {

                Text("Next")
            }


            Button(onClick = onClickStop) {
                Text("Stop Record")
            }
        }


    }
}


@Preview(showBackground = true)
@Composable
fun PreviewFooter() {
    FooterView(
        isRecording = true,
        onClickStart = {},
        onClickNext = {},
        onClickPrev = {},
        onClickStop = {})
}