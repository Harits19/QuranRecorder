import android.content.Context
import android.util.Log
import com.example.quranrecorder.quran.Quran
import com.google.gson.Gson
import java.io.InputStreamReader



fun loadQuran(context: Context): Quran {
    val filename = "QuranUthmani.json"
    val assetManager = context.assets
    val inputStream = assetManager.open(filename)
    val inputStreamReader = InputStreamReader(inputStream)
    val json =  inputStreamReader.readText()
    val quran = Gson().fromJson(json, Quran::class.java)
    return quran;

}