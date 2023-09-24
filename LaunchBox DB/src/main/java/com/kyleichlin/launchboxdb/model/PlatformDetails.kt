package com.kyleichlin.launchboxdb.model

class PlatformDetails(
    name: String,
    imageUrl: String?,
    releaseDate: String,
    overview: String,
    extraDetails: Map<String, String>,
    databaseId: Int
) : Details(
    name,
    imageUrl,
    releaseDate,
    overview,
    extraDetails,
    databaseId
) {

}