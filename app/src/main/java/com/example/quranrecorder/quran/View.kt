import android.content.Context
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quranrecorder.quran.Quran

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuranView(context: Context, paddingValues: PaddingValues) {


    val quran: Quran = loadQuran(context)
    val surahList = quran.data.surahs;
    val pages =
        surahList.map { surah -> surah.ayahs }.flatten().groupBy { ayah -> ayah.page }.toList()

    val initialPage = 0;

    val pagerState = rememberPagerState(pageCount = {
        pages.size
    }, initialPage = initialPage)


    val firstAyah = pages.first().second.first().number;
    val lastAyah = pages.last().second.last().number;
    var selectedAyah by remember { mutableLongStateOf(firstAyah) }

    val currentPage = pagerState.currentPage + 1;

    fun onClickNext() {
        val nextAyah = selectedAyah + 1;
        if (nextAyah > lastAyah) {
            return
        }
        selectedAyah = nextAyah;

    }

    fun onClickPrev() {
        val prevAyah = selectedAyah - 1;
        if (prevAyah < firstAyah) {
            return
        }
        selectedAyah = prevAyah;
    }

    HorizontalPager(
        modifier = Modifier.fillMaxHeight(),
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
                text = page.first.toString(),

                )
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(page.second) { ayah ->
                    val isSelected = ayah.number == selectedAyah;
                    ListItem(
                        headlineContent = {
                            Text(
                                text = ayah.text, textAlign = TextAlign.Right,
                                fontSize = 32.sp,
                                lineHeight = 64.sp,
                                color = if (isSelected) Color.Blue else Color.Unspecified,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        },

                        )
                    HorizontalDivider()

                }
            }

            Row {
                FloatingActionButton(onClick = {
                    onClickPrev()
                }) {

                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Next")
                }

                FloatingActionButton(onClick = {
                    onClickNext()
                }) {

                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Next")
                }
            }
        }

    }


}

