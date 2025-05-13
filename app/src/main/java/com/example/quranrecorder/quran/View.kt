import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quranrecorder.quran.Quran

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuranView(context: Context, paddingValues: PaddingValues) {

    val quran: Quran = remember {
        loadQuran(context)
    }
    val surahs = quran.data.surahs;
    val pages = surahs.map { surah -> surah.ayahs }.flatten().groupBy { ayah -> ayah.page }.toList()
    val pagerState = rememberPagerState(pageCount = {
        pages.size
    })

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
                    ListItem(
                        headlineContent = {
                            Text(
                                text = ayah.text, textAlign = TextAlign.Right,
                                fontSize = 32.sp,
                                lineHeight = 64.sp,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        },

                        )
                    HorizontalDivider()

                }
            }

            FloatingActionButton(onClick = {}) {

                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Next")
            }
        }

    }


}

