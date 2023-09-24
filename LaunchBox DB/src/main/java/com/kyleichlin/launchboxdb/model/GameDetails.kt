package com.kyleichlin.launchboxdb.model

import com.kyleichlin.launchboxdb.GameType
import com.kyleichlin.launchboxdb.gameTypeMap

class GameDetails(
    name: String,
    imageUrl: String?,
    releaseDate: String,
    overview: String,
    extraDetails: Map<String, String>,
    databaseId: Int
) : Details(
    name,
    imageUrl,
    releaseDate,
    overview,
    extraDetails,
    databaseId
) {
    val gameType: GameType = extraDetails["Game Type"]?.let { gameTypeMap[it] } ?: GameType.UNKNOWN
    val platform: String = extraDetails["Platform"] ?: ""
}