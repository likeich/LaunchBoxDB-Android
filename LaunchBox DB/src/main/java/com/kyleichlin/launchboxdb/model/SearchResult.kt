package com.kyleichlin.launchboxdb.model

import com.kyleichlin.launchboxdb.GameType
import com.kyleichlin.launchboxdb.LaunchBoxDB
import com.kyleichlin.launchboxdb.gameTypeMap
import org.jsoup.nodes.Element

data class SearchResult(
    val title: String,
    val imageUrl: String,
    val details: String,
    val description: String,
    val databaseId: Int
) {
    val gameDetailsUrl: String = "${LaunchBoxDB.Webpage.GAME_DETAILS.url}$databaseId"
    val gameImagesUrl: String = "${LaunchBoxDB.Webpage.GAME_IMAGES.url}$databaseId"

    val gameType: GameType
        get() = GameType.values().find { details.contains(it.dbName, true) }
            ?: GameType.UNKNOWN
    val platform: String
        get() = details.substringBefore(" - ")

    fun getImages(): List<LaunchBoxImage> {
        return LaunchBoxDB().getImages(databaseId, LaunchBoxDB.ContentType.GAME)
    }

    companion object {
        fun fromElement(baseUrl: String, element: Element): SearchResult {
            val detailsUrl = baseUrl + element.select(".list-item").attr("href")

            return SearchResult(
                title = element.select("h3").text(),
                imageUrl = element.select(".img-responsive").attr("src"),
                details = element.select(".sub").text(),
                description = element.select("p").last()!!.text(),
                databaseId = LaunchBoxDB.extractDatabaseId(detailsUrl)
            )
        }
    }
}