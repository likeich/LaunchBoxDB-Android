package com.kyleichlin.launchboxdb.model

open class Details(
    val name: String,
    val imageUrl: String?,
    val releaseDate: String,
    val overview: String,
    val extraDetails: Map<String, String>
)