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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.kyleichlin.launchboxdb.model.PlatformPreview
import com.kyleichlin.launchboxdb.model.SearchResult
import com.kyleichlin.launchboxdb.ui.theme.LaunchBoxDBTheme
import com.kyleichlin.launchboxdb.view.GameDetailsPage
import com.kyleichlin.launchboxdb.view.GamePage
import com.kyleichlin.launchboxdb.view.PlatformPage
import com.kyleichlin.launchboxdb.view.SettingsPage

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LaunchBoxDBTheme {
                val navController = rememberNavController()
                val currentRoute = currentRouteTracker(navController)
                var topBarTitle by remember { mutableStateOf("Games") }
                val setTopBarTitle = { title: String ->
                    topBarTitle = title
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(topBarTitle) },
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
                                selected = currentRoute == Page.GAMES.name,
                                onClick = { navController.navigate(Page.GAMES.name) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Games,
                                        contentDescription = null
                                    )
                                },
                                label = { Text(text = "Games") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == Page.PLATFORMS.name,
                                onClick = { navController.navigate(Page.PLATFORMS.name) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.VideogameAsset,
                                        contentDescription = null
                                    )
                                },
                                label = { Text(text = "Platforms") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == Page.SETTINGS.name,
                                onClick = { navController.navigate(Page.SETTINGS.name) },
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
                    NavHost(
                        navController = navController,
                        startDestination = Page.GAMES.name
                    ) {
                        composable(Page.GAMES.name) {
                            GamePage(
                                modifier = Modifier.padding(paddingValues),
                                navController,
                                setTopBarTitle
                            )
                        }
                        composable(Page.PLATFORMS.name) {
                            PlatformPage(
                                modifier = Modifier.padding(paddingValues),
                                navController,
                                setTopBarTitle
                            )
                        }
                        composable(Page.SETTINGS.name) {
                            SettingsPage(
                                modifier = Modifier.padding(paddingValues),
                                setTopBarTitle
                            )
                        }
                        composable("${Page.DETAILS.name}?url={url}") { backStackEntry: NavBackStackEntry ->
                            val url = backStackEntry.arguments?.getString("url")
                            if (url != null) {
                                GameDetailsPage(
                                    modifier = Modifier.padding(paddingValues),
                                    url = url,
                                    setTopBarTitle = setTopBarTitle
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun currentRouteTracker(navController: NavHostController): String? {
        // Use remember to make the currentRoute variable observable
        val currentRoute = remember { mutableStateOf<String?>(null) }

        // Create a listener that updates the currentRoute whenever the destination changes
        val listener = { controller: NavController, destination: NavDestination, bundle: Bundle? ->
            currentRoute.value = destination.route
        }

        // Add the listener to the NavController
        navController.addOnDestinationChangedListener(listener)

        // Return the currentRoute
        return currentRoute.value
    }
}

enum class Page {
    GAMES,
    DETAILS,
    PLATFORMS,
    SETTINGS
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
fun SearchResultView(searchResult: SearchResult, navController: NavHostController) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .height(90.dp)
            .clickable {
                navController.navigate("${Page.DETAILS.name}?url=${searchResult.gameDetailsUrl}")
            }
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            AsyncImage(
                model = searchResult.imageUrl.ifEmpty { null },
                fallback = painterResource(id = R.drawable.videogame_asset_off_24px),
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
fun PlatformPreviewView(platformPreview: PlatformPreview, onDetailsClicked: () -> Unit, onImagesClicked: () -> Unit) {
    Card {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val context = LocalContext.current
            AsyncImage(
                model = platformPreview.imageUrl.ifEmpty { null },
                contentDescription = null,
                fallback = painterResource(id = R.drawable.videogame_asset_off_24px),
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
                    onDetailsClicked()
                }),
                MultiButtonData(text = "Images", onClick = {
                    onImagesClicked()
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
    OutlinedCard {
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
