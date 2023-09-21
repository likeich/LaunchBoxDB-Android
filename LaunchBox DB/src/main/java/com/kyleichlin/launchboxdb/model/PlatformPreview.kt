package com.kyleichlin.launchboxdb.model

import org.jsoup.nodes.Element

data class PlatformPreview(
    val name: String,
    val imageUrl: String,
    val description: String,
    val gamesUrl: String,
    val detailsUrl: String,
    val imagesUrl: String
) {
    companion object {
        fun fromElement(baseUrl: String, element: Element): PlatformPreview {
            val gamesUrl = baseUrl + element.attr("href")

            return PlatformPreview(
                name = element.select("h3").text(),
                imageUrl = element.select(".img-responsive").attr("src"),
                description = element.select("p").last()!!.text(),
                gamesUrl = gamesUrl,
                detailsUrl = gamesUrl.replace("/games/", "/details/"),
                imagesUrl = gamesUrl.replace("/games/", "/images/")
            )
        }
    }
}