plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.kyleichlin.launchboxdb"
    compileSdk = 33

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("org.jsoup:jsoup:1.16.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

publishing {
    publications {
        create<MavenPublication>("releasePublication") {
            // Set the coordinates of the library
            groupId = "com.github.likeich"
            artifactId = "launchboxdb-android"
            version = "1.0.0"

            // Reference the release variant of the Android library
            afterEvaluate {
                val release = components.findByName("release")
                if (release != null) {
                    from(release)
                } else {
                    throw GradleException("Release component not found in project.")
                }
            }

            // Add POM information for the library
            pom {
                name.set("LaunchBoxDB Android")
                description.set("Easy access to the LaunchBoxDB website.")
                url.set("https://github.com/likeich/LaunchBoxDB-Android") // GitHub repo URL

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("likeich")
                        name.set("Kyle Eichlin")
                        email.set("k2launcher@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/likeich/LaunchBoxDB-Android.git")
                    developerConnection.set("scm:git:ssh://github.com:likeich/LaunchBoxDB-Android.git")
                    url.set("http://github.com/likeich/LaunchBoxDB-Android/")
                }
            }
        }
    }
}