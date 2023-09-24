package com.kyleichlin.launchboxdb.model

import com.kyleichlin.launchboxdb.LaunchBoxDB
import org.jsoup.nodes.Element

data class PlatformPreview(
    val name: String,
    val imageUrl: String,
    val description: String,
    val databaseId: Int
) {
    val gamesUrl: String = "${LaunchBoxDB.Webpage.PLATFORM_GAMES.url}$databaseId"
    val detailsUrl: String = "${LaunchBoxDB.Webpage.PLATFORM_DETAILS.url}$databaseId"
    val imagesUrl: String = "${LaunchBoxDB.Webpage.PLATFORM_IMAGES.url}$databaseId"

    companion object {
        fun fromElement(element: Element): PlatformPreview {
            val gamesUrl = LaunchBoxDB.BASE_URL + element.attr("href")

            return PlatformPreview(
                name = element.select("h3").text(),
                imageUrl = element.select(".img-responsive").attr("src"),
                description = element.select("p").last()!!.text(),
                databaseId = LaunchBoxDB.extractDatabaseId(gamesUrl)
            )
        }
    }
}