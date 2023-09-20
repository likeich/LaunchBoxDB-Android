package com.kyleichlin.launchboxdb

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kyleichlin.launchboxdb.ui.theme.LaunchBoxDBTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LaunchBoxDBTheme {
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
                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = true,
                                onClick = { /*TODO*/ },
                                icon = { Icon(imageVector = Icons.Default.Games, contentDescription = null) },
                                label = { Text(text = "Games") }
                            )
                            NavigationBarItem(
                                selected = false,
                                onClick = { /*TODO*/ },
                                icon = { Icon(imageVector = Icons.Default.VideogameAsset, contentDescription = null) },
                                label = { Text(text = "Platforms") }
                            )
                            NavigationBarItem(
                                selected = false,
                                onClick = { /*TODO*/ },
                                icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = null) },
                                label = { Text(text = "Settings") }
                            )
                        }
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .consumeWindowInsets(paddingValues),
                        verticalArrangement = Arrangement.Center
                    ) {
                        val db = LaunchBoxDB()
                        val results: SnapshotStateList<SearchResult> =
                            remember { mutableStateListOf() }
                        var searchJob: Job? by remember { mutableStateOf(null) }


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

                        Crossfade(targetState = searchJob?.isActive == true, label = "") {
                            if (it) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .padding(start = 10.dp, end = 10.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    userScrollEnabled = true
                                ) {
                                    items(results) {
                                        SearchResultView(searchResult = it)
                                    }
                                }
                            }
                        }
                    }
                }
            }
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
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
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
                model = searchResult.imageUrl,
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

// Function to open a URL
fun openUrl(url: String, context: Context) {
    val webpage: Uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, webpage)


    try {
        context.startActivity(intent)
    } catch (e: Exception) {

    }
}
