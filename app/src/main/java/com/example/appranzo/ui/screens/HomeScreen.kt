package com.example.appranzo.ui.screens

import android.text.style.BackgroundColorSpan
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.appranzo.R
import com.example.appranzo.data.models.Category
import com.example.appranzo.data.models.Place
import com.example.appranzo.ui.navigation.Routes


fun onClickPlace(place: Place){
}


@Composable
fun HomeScreen(){
    val place = Place(1,"rest","Top","Via Bella","Ortona",null,"pizza",3.0,200.0)
    LazyColumn( modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ){
        item { SearchBar(modifier = Modifier.padding(horizontal = 16.dp)) }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item { CategoryRow(modifier=Modifier,listOf(Category("pizza"),Category("pizza"),Category("pizza"),Category("pizza"),Category("pizza"))) }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            MainTitle("Alta Valutazione", modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))
            HighlitedRestaurants(listOf(place,place,place,place),{place->onClickPlace(place)})
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically)
            {
                SecondaryTitle("Vicino a Me", modifier = Modifier.padding(horizontal = 16.dp))
                Icon(Icons.Filled.LocalDining ,"ArrowIcon")
            }
            Spacer(modifier = Modifier.height(8.dp))
            for(place in listOf(place,place,place,place)){
                PlaceWithDescription(place, modifier = Modifier , {placeB->onClickPlace(placeB)} )
                Spacer(modifier = Modifier.height(15.dp))
            }
        }


    }

}

@Composable
fun SearchBar(modifier: Modifier){
    var text by remember { mutableStateOf("") }
    OutlinedTextField(
        value = text,
        onValueChange = { newText:String ->
            text = newText
        },
        label = { Text("Cerca") },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(35.dp),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Ricerca"
            )
        }
    )
}

@Composable
fun CategoryRow(modifier: Modifier=Modifier, categories:List<Category>){
    LazyRow(modifier=modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        for (c in categories) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = "$c.name_photo",
                        contentDescription = "$c.name photo preview",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.restaurantplaceholder),
                        placeholder = painterResource(id = R.drawable.restaurantplaceholder)
                    )
                    Text("${c.name.capitalize()}", fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth() )
                }
            }
        }
    }
}

@Composable
fun MainTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
fun HighlitedRestaurants(places:List<Place>, onClickColumn:(Place)->Unit){
    LazyRow(
        modifier = Modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(places) {place->
            Column(modifier = Modifier.clickable { onClickColumn(place) }) {
                Card(modifier = Modifier.size(width = 280.dp, height = 180.dp)) {
                    AsyncImage(
                        model = place.photoUrl,
                        contentDescription = "${place.description}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.restaurantplaceholder),
                        placeholder = painterResource(id = R.drawable.restaurantplaceholder)
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text("${place.name.capitalize()}", fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth() )
                Spacer(modifier = Modifier.height(5.dp))
                Row {
                    for (i in 1..place.rating.toInt()) {
                        Icon(imageVector = Icons.Default.Star, "Rating icon")
                    }
                }
            }
        }
    }
}

@Composable
fun SecondaryTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
fun PlaceWithDescription(place:Place,modifier: Modifier=Modifier, OnClickPlace:(Place)->Unit){
    Card(modifier = modifier.fillMaxWidth().clickable { OnClickPlace(place) }) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = place.photoUrl,
                contentDescription = "${place.description}",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.restaurantplaceholder),
                placeholder = painterResource(id = R.drawable.restaurantplaceholder)
            )
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                Text(place.name.capitalize(), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Row {
                    for (i in 1..place.rating.toInt()){
                        Icon(imageVector = Icons.Default.Star,"Rating icon")
                    }
                }
                Text(place?.description ?: "Place", fontSize = 14.sp)
            }
        }
    }
}