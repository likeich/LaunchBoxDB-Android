package com.kyleichlin.launchboxdb

enum class ImageType {
    BOX_FRONT,
    BOX_BACK,
    GAMEPLAY,
    BACKGROUND,
    GAME_TITLE,
    CLEAR_LOGO,
    RECONSTRUCTED,
    THREE_D,
    DISC,
    UNKNOWN,
}

val imageTypeMap = mapOf(
    "Front Image" to ImageType.BOX_FRONT,
    "Back Image" to ImageType.BOX_BACK,
    "Gameplay Image" to ImageType.GAMEPLAY,
    "Background Image" to ImageType.BACKGROUND,
    "Game Title Image" to ImageType.GAME_TITLE,
    "Clear Logo Image" to ImageType.CLEAR_LOGO,
    "Reconstructed Image" to ImageType.RECONSTRUCTED,
    "3D Image" to ImageType.THREE_D,
    "Disc Image" to ImageType.DISC,
    "Unknown" to ImageType.UNKNOWN,
)

enum class Region {
    NORTH_AMERICA,
    UNITED_STATES,
    THE_NETHERLANDS,
    JAPAN,
    UNITED_KINGDOM,
    EUROPE,
    WORLD,
    SPAIN,
    RUSSIA,
    CANADA,
    SWEDEN,
    ASIA,
    CHINA,
    FINLAND,
    ITALY,
    AUSTRALIA,
    FRANCE,
    GERMANY,
    KOREA,
    UNKNOWN
}

val regionMap = mapOf(
    "North America" to Region.NORTH_AMERICA,
    "United States" to Region.UNITED_STATES,
    "The Netherlands" to Region.THE_NETHERLANDS,
    "Japan" to Region.JAPAN,
    "United Kingdom" to Region.UNITED_KINGDOM,
    "Europe" to Region.EUROPE,
    "World" to Region.WORLD,
    "Spain" to Region.SPAIN,
    "Russia" to Region.RUSSIA,
    "Canada" to Region.CANADA,
    "Sweden" to Region.SWEDEN,
    "Asia" to Region.ASIA,
    "China" to Region.CHINA,
    "Finland" to Region.FINLAND,
    "Italy" to Region.ITALY,
    "Australia" to Region.AUSTRALIA,
    "France" to Region.FRANCE,
    "Germany" to Region.GERMANY,
    "Korea" to Region.KOREA,
    "Unknown" to Region.UNKNOWN
)

enum class GameType {
    RELEASED,
    HOMEBREW,
    ROM_HACK,
    UNKNOWN
}

val gameTypeMap = mapOf(
    "Released" to GameType.RELEASED,
    "Homebrew" to GameType.HOMEBREW,
    "ROM Hack" to GameType.ROM_HACK,
)