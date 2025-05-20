import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.quranrecorder.file.AudioFile
import com.example.quranrecorder.file.FileService
import com.example.quranrecorder.quran.ayah.AyahView
import com.example.quranrecorder.quran.footer.FooterView
import com.example.quranrecorder.record.RecordService
import com.example.quranrecorder.ui.theme.QuranRecorderTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuranView(paddingValues: PaddingValues) {

    val context = LocalContext.current;
    val quran = QuranService.loadQuran(context)
    val pages = quran.pages;
    val firstAyah = quran.firstAyah;
    val lastAyah = quran.lastAyah;


    val initialPage = 0;

    val pagerState = rememberPagerState(pageCount = {
        pages.size
    }, initialPage = initialPage)


    var selectedAyah by remember { mutableLongStateOf(firstAyah) }
    fun getFiles(): List<AudioFile> {
        return FileService(context).getAudioFiles()
    }

    var files by remember { mutableStateOf(getFiles()) }



    Log.i("files", "files ${files.size}")

    var isRecording by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val currentPage = pagerState.currentPage;
    val firstAyahCurrentPage = quran.firstAyahByPage(currentPage)
    val lastAyahCurrentPage = quran.lastAyahByPage(currentPage)

    val recordService = remember { RecordService(context) }

    fun refreshFiles() {
        files = getFiles();
    }


    suspend fun onClickNextPrev(isNext: Boolean) {
        val addValue = if (isNext) 1 else -1
        recordService.stop();
        val nextAyah = selectedAyah + addValue;
        if (isNext && nextAyah > lastAyah) {
            return
        }

        if (!isNext && nextAyah < firstAyah) {
            return
        }
        selectedAyah = nextAyah;
        recordService.start(nextAyah);
        refreshFiles();

        if (isNext && lastAyahCurrentPage >= nextAyah) {
            return;
        }

        if (!isNext && firstAyahCurrentPage <= nextAyah) {
            return
        }
        pagerState.animateScrollToPage(currentPage + addValue)

    }

    suspend fun onClickNext() {

        onClickNextPrev(true)

    }

    suspend fun onClickPrev() {
        onClickNextPrev(false)

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



    Log.i("QuranView", "files ${files.size}")

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

                    items(page, key = { it.number }) { ayah ->
                        val recordFile = files.find { item ->
                            item.id == ayah.number
                        }

                        Log.i("items", "recordFile ${recordFile}")

                        AyahView(audio = recordFile, ayah = ayah, selectedAyah = selectedAyah)

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