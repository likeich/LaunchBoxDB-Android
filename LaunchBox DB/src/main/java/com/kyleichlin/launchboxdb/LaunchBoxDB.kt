package com.kyleichlin.launchboxdb

import com.kyleichlin.launchboxdb.model.Details
import com.kyleichlin.launchboxdb.model.GameDetails
import com.kyleichlin.launchboxdb.model.LaunchBoxImage
import com.kyleichlin.launchboxdb.model.PlatformDetails
import com.kyleichlin.launchboxdb.model.PlatformPreview
import com.kyleichlin.launchboxdb.model.SearchResult
import org.jsoup.Jsoup
import java.util.regex.Pattern

fun main() {
    val db = LaunchBoxDB()
    db.searchQuery("cave story 3d", "ds", "nintendo ds")
        .forEach {
            println(it.platform)
            println(
                it.getImages().filter {
                    it.type == ImageType.BOX_FRONT
                }
            )
        }
}

class LaunchBoxDB {
    companion object {
        const val BASE_URL = "https://gamesdb.launchbox-app.com"

        fun extractDatabaseId(url: String): Int {
            return url.split("/").last().substringBefore("-").toInt()
        }
    }

    enum class Webpage(val url: String) {
        PLATFORMS("$BASE_URL/page/1"),
        GAME_SEARCH("$BASE_URL/games/results/"),
        GAME_DETAILS("$BASE_URL/games/details/"),
        GAME_IMAGES("$BASE_URL/games/images/"),
        PLATFORM_DETAILS("$BASE_URL/platforms/details/"),
        PLATFORM_GAMES("$BASE_URL/platforms/games/"),
        PLATFORM_IMAGES("$BASE_URL/platforms/images/"),
    }

    fun searchQuery(gameQuery: String, vararg platforms: String): List<SearchResult> {
        val results = searchQuery(gameQuery)

        return results.filter { result ->
            platforms.any {
                it.distanceTo(result.platform, true) > 0.9f ||
                        result.platform.contains(it, true) ||
                        it.contains(result.platform, true)
            }
        }.sortedBy {
            val distances = platforms.map { platform -> platform.distanceTo(it.platform, true) }

            println(it.platform + " " + distances.max())
            (1f - distances.max()) // Makes closer matches appear first
        }
    }

    fun searchQuery(query: String): List<SearchResult> {
        val url = Webpage.GAME_SEARCH.url + query

        val doc = Jsoup.connect(url).get()
        val elements = doc.select(".list-item-wrapper")

        val results = mutableListOf<SearchResult>()
        elements.forEach {
            results.add(SearchResult.fromElement(BASE_URL, it))
        }

        return results.sortedBy {
            it.gameType.ordinal
        }
    }

    fun getDetails(databaseId: Int, type: ContentType): Details? {
        val url = "$BASE_URL/${type.urlText}/details/$databaseId"

        val doc = try {
            Jsoup.connect(url).get()
        } catch (e: Exception) {
            return null
        }

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

        return if (type == ContentType.GAME) {
            GameDetails(
                name = name,
                imageUrl = doc.select(".header-art").attr("src").ifBlank { null },
                releaseDate = releaseDate,
                overview = overview,
                extraDetails = extraDetails,
                databaseId = databaseId
            )
        } else {
            PlatformDetails(
                name = name,
                imageUrl = doc.select(".header-device").attr("src").ifBlank { null },
                releaseDate = releaseDate,
                overview = overview,
                extraDetails = extraDetails,
                databaseId = databaseId
            )
        }
    }

    enum class ContentType(val urlText: String) {
        PLATFORM("platforms"),
        GAME("games")
    }

    fun getImages(databaseId: Int, type: ContentType): List<LaunchBoxImage> {
        val url = "$BASE_URL/${type.urlText}/images/$databaseId"

        return getLaunchBoxImages(url)
    }

    private fun getLaunchBoxImages(url: String): List<LaunchBoxImage> {
        val doc = Jsoup.connect(url).get()

        val images = arrayListOf<LaunchBoxImage>()

        doc.select(".image-list")
            .select("a")
            .forEach {
                images.add(
                    LaunchBoxImage.fromElement(it)
                )
            }

        return images
    }

    fun getPlatforms(): List<PlatformPreview> {
        var url = Webpage.PLATFORMS.url
        val platforms = mutableListOf<PlatformPreview>()

        while (true) {
            val doc = Jsoup.connect(url).get()

            val listElements = doc.select(".list-item")
            if (listElements.isEmpty()) return platforms

            listElements
                .forEach {
                    platforms.add(
                        PlatformPreview.fromElement(it)
                    )
                }

            val nextPageNum = url.last().toString().toInt() + 1
            url = url.replace(url.last().toString(), nextPageNum.toString())
        }
    }
}