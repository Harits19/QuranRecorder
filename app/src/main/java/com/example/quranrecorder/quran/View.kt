import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.quranrecorder.file.AudioFile
import com.example.quranrecorder.file.FileService
import com.example.quranrecorder.quran.footer.FooterView
import com.example.quranrecorder.record.RecordService
import com.example.quranrecorder.ui.theme.QuranRecorderTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuranView(paddingValues: PaddingValues) {

    val context = LocalContext.current;
    val quran = remember { QuranService.loadQuran(context) }
    val pages = quran.pages;
    val firstAyah = quran.firstAyah;
    val lastAyah = quran.lastAyah;


    val initialPage = 0;

    val pagerState = rememberPagerState(pageCount = {
        pages.size
    }, initialPage = initialPage)


    var selectedAyah by remember { mutableLongStateOf(firstAyah) }
    var files by remember { mutableStateOf(emptyList<AudioFile>()) }
    LaunchedEffect(selectedAyah) {
        files = FileService(context).getAudioFiles()
    }

    Log.i("files", "files ${files.size}")

    var isRecording by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val currentPage = pagerState.currentPage;
    val firstAyahCurrentPage = quran.firstAyahByPage(currentPage)
    val lastAyahCurrentPage = quran.lastAyahByPage(currentPage)

    val recordService = remember { RecordService(context) }


    suspend fun onClickNext() {
        recordService.stop();
        val nextAyah = selectedAyah + 1;
        if (nextAyah > lastAyah) {
            return
        }
        selectedAyah = nextAyah;
        recordService.start(nextAyah);

        if (lastAyahCurrentPage >= nextAyah) {
            return;
        }
        pagerState.animateScrollToPage(currentPage + 1)

    }

    suspend fun onClickPrev() {
        val prevAyah = selectedAyah - 1;
        if (prevAyah < firstAyah) {
            return
        }
        selectedAyah = prevAyah;

        if (firstAyahCurrentPage <= prevAyah) {
            return;
        }

        pagerState.animateScrollToPage(currentPage - 1)

    }

    fun startRecording() {
        isRecording = true;
        recordService.start(selectedAyah);
    }

    fun stopRecording() {
        recordService.stop();
        isRecording = false;
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startRecording();
        }
    }


    fun onClickStart() {
        val isGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED;
        when {
            isGranted -> {
                startRecording();
            }

            else -> {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }



    Column {
        HorizontalPager(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            state = pagerState,
            contentPadding = paddingValues
        ) { index ->
            val page = pages[index]

            Column(
                modifier = Modifier
                    .padding(PaddingValues(horizontal = 16.dp))
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top // posisi item di atas
            ) {
                Text(
                    text = (index + 1).toString(),

                    )
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(page) { ayah ->
                        val isSelected = ayah.number == selectedAyah;
                        ListItem(headlineContent = {
                            Text(
                                text = ayah.text, textAlign = TextAlign.Right,
                                fontSize = 32.sp,
                                lineHeight = 64.sp,
                                color = if (isSelected) Color.Blue else Color.Unspecified,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }, supportingContent = {
                            val recordFile = files.find { item ->
                                item.id == ayah.number
                            }
                            if (recordFile == null) return@ListItem;

                            Row {
                                Text(recordFile.file.name)
                                Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                            }

                        }

                        )
                        HorizontalDivider()

                    }
                }


            }

        }

        FooterView(
            isRecording = isRecording,
            onClickStart = {
                onClickStart()
            },
            onClickNext = {
                scope.launch {
                    onClickNext()
                }
            },
            onClickPrev = {
                scope.launch {
                    onClickPrev()
                }
            },
            onClickStop = ::stopRecording,
        )

    }

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QuranRecorderTheme {
        Scaffold { innerPadding ->
            QuranView(paddingValues = innerPadding)

        }
    }
}