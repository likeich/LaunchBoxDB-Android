package com.kyleichlin.launchboxdb.view

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kyleichlin.launchboxdb.ImageType
import com.kyleichlin.launchboxdb.LaunchBoxDB
import com.kyleichlin.launchboxdb.LoadingAnimation
import com.kyleichlin.launchboxdb.MultiButton
import com.kyleichlin.launchboxdb.MultiButtonData
import com.kyleichlin.launchboxdb.R
import com.kyleichlin.launchboxdb.model.Details
import com.kyleichlin.launchboxdb.model.GameDetails
import com.kyleichlin.launchboxdb.model.GameImage
import com.kyleichlin.launchboxdb.openUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GameDetailsPage(modifier: Modifier, url: String, setTopBarTitle: (String) -> Unit) {
    val db = remember { LaunchBoxDB() }
    var details: Details? by remember { mutableStateOf(null) }
    val images: SnapshotStateList<GameImage> = remember { mutableStateListOf() }
    var loadingJob: Job? by remember { mutableStateOf(null) }

    LaunchedEffect(url) {
        setTopBarTitle("Loading...")
        loadingJob = CoroutineScope(Dispatchers.IO).launch {
            val loadedDetails = db.getDetails(url)
            val gameImages = db.getGameImages(url.replace("/details/", "/images/"))
            images.clear()

            withContext(Dispatchers.Main) {
                details = loadedDetails
                images.addAll(gameImages)
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
        ) {
            details?.let {
                DetailsView(it, images)
            }
        }
    }
}

@Composable
fun DetailsView(details: Details, images: List<GameImage> = emptyList()) {
    Card {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = details.imageUrl,
                fallback = painterResource(id = R.drawable.videogame_asset_off_24px),
                contentDescription = null,
                modifier = Modifier.height(125.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (details is GameDetails) {
                    Text(details.platform)
                }
                Text(details.releaseDate)
                if (details is GameDetails) {
                    Text(details.gameType.name)
                }
            }
        }
    }

    var showDetails by remember { mutableStateOf(true) }

    MultiButton(
        data = listOf(
            MultiButtonData(
                text = "Details",
                onClick = {
                    showDetails = true
                }
            ),
            MultiButtonData(
                text = "Images",
                onClick = {
                    showDetails = false
                }
            )
        )
    )

    Crossfade(targetState = showDetails, label = "") {
        if (it) {
            DetailsView(details = details)
        } else {
            ImagesView(images = images)
        }
    }
}

@Composable
fun DetailsView(details: Details) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        var maxLines by remember { mutableStateOf(5) }
        Text(
            details.overview,
            style = MaterialTheme.typography.bodySmall,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .clickable {
                    maxLines = if (maxLines == 5) Int.MAX_VALUE else 5
                }
                .animateContentSize()
        )

        details.extraDetails.entries.forEach {
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
}

@Composable
fun ImagesView(images: List<GameImage>) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(75.dp),
        verticalItemSpacing = 10.dp,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(images) {
            ImageView(image = it)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageView(image: GameImage) {
    val context = LocalContext.current
    AsyncImage(
        model = image.url,
        contentDescription = image.altText,
        modifier = Modifier
            .combinedClickable(
                onLongClick = {
                    Toast.makeText(context, image.altText, Toast.LENGTH_SHORT).show()
                }
            ) {}
    )
}
