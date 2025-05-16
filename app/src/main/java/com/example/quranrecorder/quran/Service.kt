import android.content.Context
import android.util.Log
import com.example.quranrecorder.quran.Ayah
import com.example.quranrecorder.quran.Quran
import com.google.gson.Gson
import java.io.InputStreamReader


class QuranService(var quran: Quran) {


    companion object {
        fun loadQuran(context: Context): QuranService {
            val filename = "QuranUthmani.json"
            val assetManager = context.assets
            val inputStream = assetManager.open(filename)
            val inputStreamReader = InputStreamReader(inputStream)
            val json = inputStreamReader.readText()
            val quran = Gson().fromJson(json, Quran::class.java)

            return QuranService(quran);
        }
    }


    val pages: List<List<Ayah>>
        get() {
            val surahList = quran.data.surahs;
            val pages =
                surahList.map { surah -> surah.ayahs }.flatten()
                    .groupBy { ayah -> ayah.page }.values.toList();

            return pages;
        }

    val firstAyah = pages.first().first().number;
    val lastAyah = pages.last().last().number;


    val firstAyahByPage = { currentPage: Int ->
        pages[currentPage].first().number;
    }

    val lastAyahByPage = { currentPage: Int ->
        pages[currentPage].last().number;
    }
}