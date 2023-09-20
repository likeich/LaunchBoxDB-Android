package com.kyleichlin.launchboxdb

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kyleichlin.launchboxdb.ui.theme.LaunchBoxDBTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LaunchBoxDBTheme {
                var currentPage by remember { mutableStateOf(Page.GAMES) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("LaunchBox DB") },
                            actions = {
                                IconButton(
                                    onClick = {

                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = null
                                    )
                                }
                            },
                            scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentPage == Page.GAMES,
                                onClick = { currentPage = Page.GAMES },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Games,
                                        contentDescription = null
                                    )
                                },
                                label = { Text(text = "Games") }
                            )
                            NavigationBarItem(
                                selected = currentPage == Page.PLATFORMS,
                                onClick = { currentPage = Page.PLATFORMS },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.VideogameAsset,
                                        contentDescription = null
                                    )
                                },
                                label = { Text(text = "Platforms") }
                            )
                            NavigationBarItem(
                                selected = currentPage == Page.SETTINGS,
                                onClick = { currentPage = Page.SETTINGS },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = null
                                    )
                                },
                                label = { Text(text = "Settings") }
                            )
                        }
                    }
                ) { paddingValues ->
                    when (currentPage) {
                        Page.GAMES -> GamePage(modifier = Modifier.padding(paddingValues))
                        Page.PLATFORMS -> PlatformPage(modifier = Modifier.padding(paddingValues))
                        Page.SETTINGS -> SettingsPage(modifier = Modifier.padding(paddingValues))
                    }
                }
            }
        }
    }

    @Composable
    fun SettingsPage(modifier: Modifier) {
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
                Text(text = "Copyright © 2023 Kyle Eichlin")
                Text(text = "Designed with ✝️ in Hawaii")
                Text(
                    text = "This app is in no way affiliated with LaunchBox or Unbroken Software, LLC.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

enum class Page {
    GAMES,
    PLATFORMS,
    SETTINGS
}

@Composable
fun GamePage(modifier: Modifier) {
    val db = LaunchBoxDB()
    val results: SnapshotStateList<SearchResult> =
        remember { mutableStateListOf() }
    var searchJob: Job? by remember { mutableStateOf(null) }

    LoadingAnimation(loading = searchJob?.isActive == true) {
        LazyColumn(
            modifier = modifier
                .padding(start = 10.dp, end = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            userScrollEnabled = true
        ) {
            item {
                SearchBar(onSearch = {
                    results.clear()

                    searchJob?.cancel()
                    searchJob = CoroutineScope(Dispatchers.IO).launch {
                        val query = db.searchQuery(it)

                        withContext(Dispatchers.Main) {
                            results.addAll(query)
                            searchJob = null
                        }
                    }
                })
            }

            items(results) {
                SearchResultView(searchResult = it)
            }
        }
    }
}

@Composable
fun LoadingAnimation(loading: Boolean, content: @Composable () -> Unit) {
    Crossfade(targetState = loading, label = "") {
        if (it) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(onSearch: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current


    TextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("Search") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            onSearch(text)
            // Hide the keyboard after submitting the search
            keyboardController?.hide()
            //or hide keyboard
            focusManager.clearFocus()

        })
    )
}

@Composable
fun SearchResultView(searchResult: SearchResult) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .height(90.dp)
            .clickable {
                openUrl(searchResult.gameDetailsUrl, context)
            }
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            AsyncImage(
                model = searchResult.imageUrl.ifEmpty { null },
                fallback = painterResource(id = R.drawable.stadia_controller_24px),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxHeight()
            )
            
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1f),
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = searchResult.title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = searchResult.platform,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun PlatformPage(modifier: Modifier) {
    val platforms = remember { mutableStateListOf<PlatformPreview>() }
    var loadingJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(Unit) {
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
                PlatformPreviewView(it)
            }
        }
    }
}

@Composable
fun PlatformPreviewView(platformPreview: PlatformPreview) {
    Card {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val context = LocalContext.current
            AsyncImage(
                model = platformPreview.imageUrl,
                contentDescription = null,
                fallback = painterResource(id = R.drawable.stadia_controller_24px),
                modifier = Modifier.height(75.dp)
            )
            Text(
                text = platformPreview.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = platformPreview.description,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall
            )
            MultiButton(data = listOf(
                MultiButtonData(text = "Games", onClick = {
                    openUrl(platformPreview.gamesUrl, context)
                }),
                MultiButtonData(text = "Details", onClick = {
                    openUrl(platformPreview.detailsUrl, context)
                }),
                MultiButtonData(text = "Images", onClick = {
                    openUrl(platformPreview.imagesUrl, context)
                })
            ))
        }
    }
}

data class MultiButtonData(
    val text: String,
    val onClick: () -> Unit
)

@Composable
fun MultiButton(data: List<MultiButtonData>) {
    ElevatedCard {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            data.forEach {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(45.dp)
                        .clickable { it.onClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = it.text,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

// Function to open a URL
fun openUrl(url: String, context: Context) {
    val webpage: Uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, webpage)

    try {
        context.startActivity(intent)
    } catch (e: Exception) {

    }
}
