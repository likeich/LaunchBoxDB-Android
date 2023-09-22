package com.kyleichlin.launchboxdb.model

class PlatformDetails(
    name: String,
    imageUrl: String?,
    releaseDate: String,
    overview: String,
    extraDetails: Map<String, String>
) : Details(
    name,
    imageUrl,
    releaseDate,
    overview,
    extraDetails
) {

}