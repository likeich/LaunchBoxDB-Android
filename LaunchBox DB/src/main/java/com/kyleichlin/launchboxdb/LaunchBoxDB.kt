package com.kyleichlin.launchboxdb

import android.util.Log
import com.kyleichlin.launchboxdb.model.Details
import com.kyleichlin.launchboxdb.model.GameDetails
import com.kyleichlin.launchboxdb.model.GameImage
import com.kyleichlin.launchboxdb.model.PlatformDetails
import com.kyleichlin.launchboxdb.model.PlatformPreview
import com.kyleichlin.launchboxdb.model.SearchResult
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern

fun main() {
    val db = LaunchBoxDB()
    db.searchQuery("spectrobes beyond the portals")
        .forEach {
            println(
                it.getImages()
                    .filter { it.type == ImageType.BOX_FRONT }
            )
        }
}

class LaunchBoxDB {
    val BASE_URL = "https://gamesdb.launchbox-app.com"
    val PLATFORM_URL = "$BASE_URL/page/1"
    val SEARCH_URL = "$BASE_URL/games/results/"

    fun searchQuery(query: String): List<SearchResult> {
        val url = SEARCH_URL + query

        val doc = Jsoup.connect(url).get()
        val elements = doc.select(".list-item-wrapper")

        val results = mutableListOf<SearchResult>()
        elements.forEach {
            results.add(SearchResult.fromElement(BASE_URL, it))
        }

        return results
    }

    fun getDetails(url: String): Details? {
        val doc = try {
            Jsoup.connect(url).get()
        } catch (e: Exception) {
            return null
        }

        val isGame = url.contains("/games/")

        var name = ""
        var releaseDate = ""
        var overview = ""
        val extraDetails = mutableMapOf<String, String>()

        doc.select(".table")
            .select("tr")
            .forEach {
                val header = it.select(".row-header").text()
                val text = it.select(".view").text()

                when (header) {
                    "Name" -> name = text
                    "Release Date" -> releaseDate = text
                    "Overview" -> overview = text
                    "Add Alternate Name" -> {} // Skip
                    else -> {
                        if (header.isNotBlank() && text.isNotBlank()) {
                            extraDetails[header] = text
                        }
                    }
                }
            }

        return if (isGame) {
            GameDetails(
                name = name,
                releaseDate = releaseDate,
                overview = overview,
                extraDetails = extraDetails,
                imageUrl = doc.select(".header-art").attr("src").ifBlank { null }
            )
        } else {
            PlatformDetails(
                name = name,
                releaseDate = releaseDate,
                overview = overview,
                extraDetails = extraDetails,
                imageUrl = doc.select(".header-device").attr("src").ifBlank { null }
            )
        }
    }

    fun getGameImages(url: String): List<GameImage> {
        val doc = Jsoup.connect(url).get()

        val images = arrayListOf<GameImage>()

        doc.select(".image-list")
            .select("a")
            .forEach {
                val imageUrl = it.attr("href")
                val imageType = it.select("img").attr("alt")
                    .split(" - ")
                    .lastOrNull() ?: "Unknown"
                val region = it.attr("data-title").extractLastTextInParentheses() ?: "Unknown"

                images.add(
                    GameImage(
                        url = imageUrl,
                        type = imageTypeMap[imageType] ?: ImageType.UNKNOWN,
                        region = regionMap[region] ?: Region.UNKNOWN,
                        altText = it.select("img").attr("alt")
                    )
                )
            }

        return images
    }

    fun getPlatforms(): List<PlatformPreview> {
        var url = PLATFORM_URL
        val platforms = mutableListOf<PlatformPreview>()

        while (true) {
            val doc = Jsoup.connect(url).get()

            val listElements = doc.select(".list-item")
            if (listElements.isEmpty()) return platforms

            listElements
                .forEach {
                    platforms.add(
                        PlatformPreview.fromElement(BASE_URL, it)
                    )
                }

            url = url.replace(url.last().toString(), (url.last().toString().toInt() + 1).toString())
        }
    }

    fun String.extractLastTextInParentheses(): String? {
        val pattern = Pattern.compile("\\([^)]*\\)")
        val matcher = pattern.matcher(this)
        var lastMatch: String? = null

        while (matcher.find()) {
            lastMatch = matcher.group()
        }

        if (lastMatch != null) {
            // Remove the parentheses to get the text
            return lastMatch.substring(1, lastMatch.length - 1)
        }

        return null
    }
}