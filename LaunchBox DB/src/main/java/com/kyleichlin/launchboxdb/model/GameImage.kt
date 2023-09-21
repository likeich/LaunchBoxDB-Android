package com.kyleichlin.launchboxdb.model

import com.kyleichlin.launchboxdb.ImageType
import com.kyleichlin.launchboxdb.Region

data class GameImage(
    val url: String,
    val type: ImageType,
    val region: Region,
    val altText: String,
)