package com.example.crdemo.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.crdemo.data.model.AnimalDataTable
import com.example.crdemo.data.model.ExhibitTable
import com.example.crdemo.data.model.PlantDataTable

@Composable
fun AnimalItem(
    animal: AnimalDataTable,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = animal.pic01Url,
            contentDescription = animal.nameChinese,
            modifier = Modifier
                .size(80.dp)
                .padding(end = 8.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = animal.nameChinese,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = animal.alsoKnown ?: "",
                fontSize = 14.sp
            )
            Text(
                text = animal.location,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun PlantItem(
    plant: PlantDataTable,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = plant.imageUrl,
            contentDescription = plant.nameChinese,
            modifier = Modifier
                .size(80.dp)
                .padding(end = 8.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = plant.nameChinese,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = plant.alsoKnown ?: "",
                fontSize = 14.sp
            )
            Text(
                text = plant.location ?: "",
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ExhibitItem(
    exhibit: ExhibitTable,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp)
            .background(Color.White)
            // .elevation ... handled by Card usually or shadow
    ) {
        AsyncImage(
            model = exhibit.e_pic_url,
            contentDescription = exhibit.e_name,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )

        Text(
            text = exhibit.e_name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = exhibit.e_info,
            fontSize = 14.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}
