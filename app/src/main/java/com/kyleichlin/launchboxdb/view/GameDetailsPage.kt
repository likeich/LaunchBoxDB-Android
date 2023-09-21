package com.kyleichlin.launchboxdb.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kyleichlin.launchboxdb.LaunchBoxDB
import com.kyleichlin.launchboxdb.LoadingAnimation
import com.kyleichlin.launchboxdb.R
import com.kyleichlin.launchboxdb.model.GameDetails
import com.kyleichlin.launchboxdb.openUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GameDetailsPage(modifier: Modifier, url: String, setTopBarTitle: (String) -> Unit) {
    val db = remember { LaunchBoxDB() }
    var gameDetails: GameDetails? by remember { mutableStateOf(null) }
    var loadingJob: Job? by remember { mutableStateOf(null) }

    LaunchedEffect(url) {
        setTopBarTitle("Loading...")
        loadingJob = CoroutineScope(Dispatchers.IO).launch {
            val details = db.getGameDetails(url)

            withContext(Dispatchers.Main) {
                gameDetails = details
                setTopBarTitle(details?.name ?: "Loading Failed")
                loadingJob = null
            }
        }
    }

    LoadingAnimation(loading = loadingJob != null) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = modifier
                .padding(start = 10.dp, end = 10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            gameDetails?.let {
                GameDetailsView(it)
            }
        }
    }
}

@Composable
fun GameDetailsView(gameDetails: GameDetails) {
    Card {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(10.dp).fillMaxWidth()
        ) {
            AsyncImage(
                model = gameDetails.imageUrl,
                fallback = painterResource(id = R.drawable.videogame_asset_off_24px),
                contentDescription = null,
                modifier = Modifier.height(125.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(gameDetails.platform)
                Text(gameDetails.releaseDate)
                Text(gameDetails.gameType.name)
            }
        }
    }
    var maxLines by remember { mutableStateOf(5) }
    Text(
        gameDetails.overview,
        style = MaterialTheme.typography.bodySmall,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .clickable {
                maxLines = if (maxLines == 5) Int.MAX_VALUE else 5
            }
            .animateContentSize()
    )

    gameDetails.extraDetails.entries.forEach {
        Row {
            Text(it.key, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))

            val context = LocalContext.current
            val isUrl = it.value.startsWith("http")
            val modifier = if (isUrl) {
                Modifier.clickable {
                    openUrl(it.value, context)
                }
            } else {
                Modifier
            }
            Text(
                it.value,
                textAlign = TextAlign.End,
                modifier = modifier
            )
        }
    }
}
