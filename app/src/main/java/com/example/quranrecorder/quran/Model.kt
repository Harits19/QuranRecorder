package com.example.quranrecorder.quran

data class Quran(
    val code: Long,
    val status: String,
    val data: QuranData,
)

data class QuranData(
    val surahs: List<Surah>,
    val edition: Edition,
)

data class Surah(
    val number: Long,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val revelationType: String,
    val ayahs: List<Ayah>,
)

data class Ayah(
    val number: Long,
    val text: String,
    val numberInSurah: Long,
    val juz: Long,
    val manzil: Long,
    val page: Long,
    val ruku: Long,
    val hizbQuarter: Long,
    val sajda: Any?,
)

data class Edition(
    val identifier: String,
    val language: String,
    val name: String,
    val englishName: String,
    val format: String,
    val type: String,
)
