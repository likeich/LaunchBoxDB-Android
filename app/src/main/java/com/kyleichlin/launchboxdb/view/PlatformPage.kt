package com.kyleichlin.launchboxdb.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kyleichlin.launchboxdb.LaunchBoxDB
import com.kyleichlin.launchboxdb.LoadingAnimation
import com.kyleichlin.launchboxdb.Page
import com.kyleichlin.launchboxdb.PlatformPreviewView
import com.kyleichlin.launchboxdb.model.PlatformPreview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PlatformPage(
    modifier: Modifier, navController: NavHostController, setTopBarTitle: (String) -> Unit
) {
    val platforms = remember { mutableStateListOf<PlatformPreview>() }
    var loadingJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(Unit) {
        setTopBarTitle("Platforms")
        loadingJob?.cancel()

        loadingJob = CoroutineScope(Dispatchers.IO).launch {
            val loaded = LaunchBoxDB().getPlatforms()

            withContext(Dispatchers.Main) {
                platforms.addAll(loaded)
                loadingJob = null
            }
        }
    }

    LoadingAnimation(loading = loadingJob?.isActive == true) {
        LazyColumn(
            modifier.padding(start = 10.dp, end = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(platforms) {
                PlatformPreviewView(
                    it,
                    onDetailsClicked = {
                        navController.navigate("${Page.DETAILS.name}?url=${it.detailsUrl}")
                    },
                    onImagesClicked = {
                        navController.navigate("${Page.DETAILS.name}?url=${it.detailsUrl}")
                    }
                )
            }
        }
    }
}