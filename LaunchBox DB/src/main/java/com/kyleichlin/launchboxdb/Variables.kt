package com.kyleichlin.launchboxdb

import java.util.regex.Pattern

enum class ImageType(val dbName: String) {
    BOX_FRONT("Front Image"),
    BOX_BACK("Back Image"),
    GAMEPLAY("Gameplay Image"),
    BACKGROUND("Background Image"),
    GAME_TITLE("Game Title Image"),
    CLEAR_LOGO("Clear Logo Image"),
    RECONSTRUCTED("Reconstructed Image"),
    THREE_D("3D Image"),
    DISC("Disc Image"),
    UNKNOWN("Unknown"),
}

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

enum class GameType(val dbName: String) {
    RELEASED("Released"),
    UNLICENSED("Unlicensed"),
    HOMEBREW("Homebrew"),
    ROM_HACK("ROM Hack"),
    UNKNOWN("Unknown")
}

val gameTypeMap = mapOf(
    "Released" to GameType.RELEASED,
    "Homebrew" to GameType.HOMEBREW,
    "ROM Hack" to GameType.ROM_HACK,
)

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

fun String.distanceTo(other: String, ignoreCase: Boolean = false): Float {
    val len1 = this.length
    val len2 = other.length

    val maxLen = maxOf(len1, len2)

    if (maxLen == 0) {
        return 1.0f // Both strings are empty, consider them as a perfect match.
    }

    val distance = levenshteinDistance(
        if (ignoreCase) this.lowercase() else this,
        if (ignoreCase) other.lowercase() else other
    )
    return 1.0f - (distance.toFloat() / maxLen)
}

fun levenshteinDistance(str1: String, str2: String): Int {
    val len1 = str1.length
    val len2 = str2.length

    val dp = Array(len1 + 1) { IntArray(len2 + 1) }

    for (i in 0..len1) {
        for (j in 0..len2) {
            when {
                i == 0 -> dp[i][j] = j
                j == 0 -> dp[i][j] = i
                str1[i - 1] == str2[j - 1] -> dp[i][j] = dp[i - 1][j - 1]
                else -> dp[i][j] = 1 + minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1])
            }
        }
    }

    return dp[len1][len2]
}

// Updated 23SEP2023
val platforms = hashSetOf(
    "3DO Interactive Multiplayer",
    "Aamber Pegasus",
    "Acorn Archimedes",
    "Acorn Atom",
    "Acorn Electron",
    "Amstrad CPC",
    "Amstrad GX4000",
    "Android",
    "APF Imagination Machine",
    "Apogee BK-01",
    "Apple II",
    "Apple IIGS",
    "Apple iOS",
    "Apple Mac OS",
    "Arcade",
    "Atari 2600",
    "Atari 5200",
    "Atari 7800",
    "Atari 800",
    "Atari Jaguar",
    "Atari Jaguar CD",
    "Atari Lynx",
    "Atari ST",
    "Atari XEGS",
    "Bally Astrocade",
    "BBC Microcomputer System",
    "Camputers Lynx",
    "Casio Loopy",
    "Casio PV-1000",
    "Coleco ADAM",
    "ColecoVision",
    "Commodore 128",
    "Commodore 64",
    "Commodore Amiga",
    "Commodore Amiga CD32",
    "Commodore CDTV",
    "Commodore MAX Machine",
    "Commodore PET",
    "Commodore Plus 4",
    "Commodore VIC-20",
    "Dragon 32/64",
    "EACA EG2000 Colour Genie",
    "Elektronika BK",
    "Emerson Arcadia 2001",
    "Enterprise",
    "Entex Adventure Vision",
    "Epoch Game Pocket Computer",
    "Epoch Super Cassette Vision",
    "Exelvision EXL 100",
    "Exidy Sorcerer",
    "Fairchild Channel F",
    "Fujitsu FM Towns Marty",
    "Fujitsu FM-7",
    "Funtech Super Acan",
    "Game Wave Family Entertainment System",
    "GamePark GP32",
    "GameWave",
    "GCE Vectrex",
    "Hartung Game Master",
    "Hector HRX",
    "Interton VC 4000",
    "Jupiter Ace",
    "Linux",
    "Lviv PC-01",
    "Magnavox Odyssey",
    "Magnavox Odyssey 2",
    "Matra and Hachette Alice",
    "Mattel Aquarius",
    "Mattel HyperScan",
    "Mattel HyperScan",
    "Mattel Intellivision",
    "Mega Duck",
    "Memotech MTX512",
    "Microsoft MSX",
    "Microsoft MSX2",
    "Microsoft MSX2+",
    "Microsoft Xbox",
    "Microsoft Xbox 360",
    "Microsoft Xbox One",
    "MS-DOS",
    "MUGEN",
    "Namco System 22",
    "NEC PC-8801",
    "NEC PC-9801",
    "NEC PC-FX",
    "NEC TurboGrafx-16",
    "NEC TurboGrafx-CD",
    "Nintendo 3DS",
    "Nintendo 64",
    "Nintendo 64DD",
    "Nintendo DS",
    "Nintendo Entertainment System",
    "Nintendo Famicom Disk System",
    "Nintendo Game & Watch",
    "Nintendo Game Boy",
    "Nintendo Game Boy Advance",
    "Nintendo Game Boy Color",
    "Nintendo GameCube",
    "Nintendo Pokemon Mini",
    "Nintendo Satellaview",
    "Nintendo Switch",
    "Nintendo Virtual Boy",
    "Nintendo Wii",
    "Nintendo Wii U",
    "Nokia N-Gage",
    "Nuon",
    "OpenBOR",
    "Oric Atmos",
    "Othello Multivision",
    "Ouya",
    "PC Engine SuperGrafx",
    "Philips CD-i",
    "Philips VG 5000",
    "Philips Videopac+",
    "Pinball",
    "Radio-86RK Mikrosha",
    "RCA Studio II",
    "SAM Coupé",
    "Sammy Atomiswave",
    "ScummVM",
    "Sega 32X",
    "Sega CD",
    "Sega CD 32X",
    "Sega Dreamcast",
    "Sega Dreamcast VMU",
    "Sega Game Gear",
    "Sega Genesis",
    "Sega Hikaru",
    "Sega Master System",
    "Sega Model 1",
    "Sega Model 2",
    "Sega Model 3",
    "Sega Naomi",
    "Sega Naomi 2",
    "Sega Pico",
    "Sega Saturn",
    "Sega SC-3000",
    "Sega SG-1000",
    "Sega ST-V",
    "Sega System 16",
    "Sega System 32",
    "Sega Triforce",
    "Sharp MZ-2500",
    "Sharp X1",
    "Sharp X68000",
    "Sinclair ZX Spectrum",
    "Sinclair ZX-81",
    "SNK Neo Geo AES",
    "SNK Neo Geo CD",
    "SNK Neo Geo MVS",
    "SNK Neo Geo Pocket",
    "SNK Neo Geo Pocket Color",
    "Sony Playstation",
    "Sony Playstation 2",
    "Sony Playstation 3",
    "Sony Playstation 4",
    "Sony Playstation 5",
    "Sony Playstation Vita",
    "Sony PocketStation",
    "Sony PSP",
    "Sony PSP Minis",
    "Sord M5",
    "Spectravideo",
    "Super Nintendo Entertainment System",
    "Taito Type X",
    "Taito Type X",
    "Tandy TRS-80",
    "Tapwave Zodiac",
    "Texas Instruments TI 99/4A",
    "Tiger Game.com",
    "Tomy Tutor",
    "Touhou Project",
    "TRS-80 Color Computer",
    "Vector-06C",
    "VTech CreatiVision",
    "VTech Socrates",
    "Watara Supervision",
    "Web Browser",
    "Windows",
    "Windows 3.X",
    "WonderSwan",
    "WonderSwan Color",
    "WoW Action Max",
    "XaviXPORT",
    "XaviXPORT",
    "ZiNc",
)

fun String.replaceDigitsWithText(): String {
    val digitToTextMap = mapOf(
        '0' to "zero",
        '1' to "one",
        '2' to "two",
        '3' to "three",
        '4' to "four",
        '5' to "five",
        '6' to "six",
        '7' to "seven",
        '8' to "eight",
        '9' to "nine"
    )

    val result = StringBuilder()

    for (char in this) {
        val replacement = digitToTextMap[char]
        if (replacement != null) {
            result.append(replacement)
        } else {
            result.append(char)
        }
    }

    return result.toString()
}