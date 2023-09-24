package com.kyleichlin.launchboxdb.model

import com.kyleichlin.launchboxdb.ImageType
import com.kyleichlin.launchboxdb.Region
import com.kyleichlin.launchboxdb.extractLastTextInParentheses
import com.kyleichlin.launchboxdb.regionMap
import org.jsoup.nodes.Element

data class LaunchBoxImage(
    val url: String,
    val type: ImageType,
    val region: Region,
    val altText: String,
) {
    companion object {
        fun fromElement(element: Element): LaunchBoxImage {
            val imageUrl = element.attr("href")
            val imageType = element.select("img").attr("alt")
                .split(" - ")
                .lastOrNull() ?: "Unknown"
            val region = element.attr("data-title").extractLastTextInParentheses() ?: "Unknown"

            return LaunchBoxImage(
                url = imageUrl,
                type = ImageType.values().find { it.dbName == imageType } ?: ImageType.UNKNOWN,
                region = regionMap[region] ?: Region.UNKNOWN,
                altText = element.select("img").attr("alt")
            )
        }
    }
}