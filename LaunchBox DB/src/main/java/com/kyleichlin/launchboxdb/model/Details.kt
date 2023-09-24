package com.kyleichlin.launchboxdb.model

open class Details(
    val name: String,
    val imageUrl: String?,
    val releaseDate: String,
    val overview: String,
    val extraDetails: Map<String, String>,
    val databaseId: Int
) {
    override fun toString(): String {
        return "Details(name='$name', imageUrl=$imageUrl, releaseDate='$releaseDate', overview='$overview', extraDetails=$extraDetails, databaseId=$databaseId)"
    }
}