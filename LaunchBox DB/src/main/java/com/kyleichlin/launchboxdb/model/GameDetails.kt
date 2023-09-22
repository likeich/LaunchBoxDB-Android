package com.kyleichlin.launchboxdb.model

import com.kyleichlin.launchboxdb.GameType
import com.kyleichlin.launchboxdb.gameTypeMap

class GameDetails(
    name: String,
    imageUrl: String?,
    releaseDate: String,
    overview: String,
    extraDetails: Map<String, String>
) : Details(
    name,
    imageUrl,
    releaseDate,
    overview,
    extraDetails
) {
    val gameType: GameType = extraDetails["Game Type"]?.let { gameTypeMap[it] } ?: GameType.UNKNOWN
    val platform: String = extraDetails["Platform"] ?: ""
}