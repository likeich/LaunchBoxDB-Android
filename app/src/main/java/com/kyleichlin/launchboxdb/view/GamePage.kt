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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kyleichlin.launchboxdb.LaunchBoxDB
import com.kyleichlin.launchboxdb.LoadingAnimation
import com.kyleichlin.launchboxdb.SearchBar
import com.kyleichlin.launchboxdb.SearchResultView
import com.kyleichlin.launchboxdb.model.SearchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GamePage(modifier: Modifier, navController: NavHostController, setTopBarTitle: (String) -> Unit) {
    LaunchedEffect(Unit) {
        setTopBarTitle("Games")
    }

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
                SearchResultView(searchResult = it, navController)
            }
        }
    }
}