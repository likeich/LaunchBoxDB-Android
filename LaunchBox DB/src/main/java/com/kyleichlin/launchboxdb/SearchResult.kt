package com.kyleichlin.launchboxdb

import org.jsoup.nodes.Element

data class SearchResult(
    val title: String,
    val imageUrl: String,
    val platform: String,
    val description: String,
    val gameDetailsUrl: String,
    val gameImagesUrl: String,
) {
    val gameType: GameType
        get() = gameTypeMap[platform.split(" - ")[1]]
            ?: GameType.UNKNOWN

    fun getDetails(): GameDetails {
        return LaunchBoxDB().getGame(gameDetailsUrl)
    }

    fun getImages(): List<GameImage> {
        return LaunchBoxDB().getGameImages(gameImagesUrl)
    }

    companion object {
        fun fromElement(baseUrl: String, element: Element): SearchResult {
            val detailsUrl = baseUrl + element.select(".list-item").attr("href")

            return SearchResult(
                title = element.select("h3").text(),
                imageUrl = element.select(".img-responsive").attr("src"),
                platform = element.select(".sub").text(),
                description = element.select("p").last()!!.text(),
                gameDetailsUrl = detailsUrl,
                gameImagesUrl = detailsUrl.replace("/details/", "/images/")
            )
        }
    }
}