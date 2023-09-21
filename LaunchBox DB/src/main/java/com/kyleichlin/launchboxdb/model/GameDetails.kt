package com.kyleichlin.launchboxdb.model

import com.kyleichlin.launchboxdb.GameType

data class GameDetails(
    val name: String,
    val imageUrl: String?,
    val platform: String,
    val releaseDate: String,
    val overview: String,
    val gameType: GameType,
    val extraDetails: Map<String, String>
)