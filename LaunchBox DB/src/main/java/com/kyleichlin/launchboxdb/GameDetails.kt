package com.kyleichlin.launchboxdb

data class GameDetails(
    val name: String,
    val platform: String,
    val releaseDate: String,
    val overview: String,
    val gameType: GameType
)