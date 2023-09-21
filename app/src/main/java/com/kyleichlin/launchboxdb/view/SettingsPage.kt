package com.kyleichlin.launchboxdb.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SettingsPage(modifier: Modifier, setTopBarTitle: (String) -> Unit) {
    LaunchedEffect(Unit) {
        setTopBarTitle("Settings")
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(.7f),
        ) {
            Text(
                text = "LaunchBox DB (Unofficial)",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
            Text(text = "Copyright © 2023 Kyle Eichlin", textAlign = TextAlign.Center)
            Text(text = "Designed with ✝️ in Hawaii", textAlign = TextAlign.Center)
            Text(
                text = "This app is in no way affiliated with LaunchBox or Unbroken Software, LLC.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}