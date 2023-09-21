package com.kyleichlin.launchboxdb.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kyleichlin.launchboxdb.ImageType
import com.kyleichlin.launchboxdb.R
import com.kyleichlin.launchboxdb.Region
import com.kyleichlin.launchboxdb.model.GameImage

@Composable
fun GameImageView(image: GameImage) {
    ElevatedCard {
        Row {
            AsyncImage(
                modifier = Modifier.height(75.dp),
                model = image.url,
                contentDescription = image.altText,
                fallback = painterResource(id = R.drawable.videogame_asset_off_24px)
            )

            Column {
                Text(text = image.type.name)
                Text(text = image.region.name)
            }
        }
    }
}

@Preview
@Composable
fun GameImageViewPreview() {
    GameImageView(
        image = GameImage(
            url = "https://images.launchbox-app.com/ab1607eb-e5a7-4867-921c-4021d5cc2041.jpg",
            type = ImageType.BOX_FRONT,
            region = Region.NORTH_AMERICA,
            altText = "Box Front"
        )
    )
}