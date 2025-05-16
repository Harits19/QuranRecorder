package com.example.quranrecorder.scaffold
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold

import androidx.compose.runtime.Composable

@Composable
fun ScaffoldView(
    content: @Composable (PaddingValues) -> Unit
) {

    Scaffold(
    ) { padding ->
        content(padding)
    }
}

