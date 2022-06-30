package com.development.draganddrop

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.development.draganddrop.domain.Country
import com.development.draganddrop.presentation.CountriesViewModel
import com.development.draganddrop.presentation.CountryListUIState
import com.development.draganddrop.ui.theme.DragAndDropTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: CountriesViewModel by viewModels()

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DragAndDropTheme {
                LaunchedEffect(mainViewModel) {
                    mainViewModel.loadCountries()
                }
                CountryScreen(mainViewModel)
            }
        }
    }

}


@Composable
fun CountryScreen(viewModel: CountriesViewModel) {
    val viewState = viewModel.countryListUIState.collectAsState()

    // A surface container using the 'background' color from the theme
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        when (viewState.value) {
            is CountryListUIState.Loading -> Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
            is CountryListUIState.Empty -> Column(Modifier.fillMaxWidth()) {
                Text(text = "Data not available")
            }
            is CountryListUIState.Error -> Column(Modifier.fillMaxWidth()) {
                Text(text = (viewState.value as CountryListUIState.Error).ex.toString())
            }
            is CountryListUIState.Loaded -> CountryListContent((
                viewState.value as CountryListUIState.Loaded).countries,
                onFlagMoved = viewModel::onMove
            )
        }
    }
}

@Composable
fun CountryListContent(
    viewItems: List<Country>,
    gridState : LazyGridState = rememberLazyGridState(),
    onFlagMoved: (Int, Int) -> Unit
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var startPosition by remember {  mutableStateOf(Offset.Zero) }
    var currentIndexOfDraggedItem by remember { mutableStateOf<Int?>(null) }


    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 108.dp),
        contentPadding = PaddingValues(8.dp),
        state = gridState,
        modifier = Modifier.pointerInput(Unit) {
            detectDragGesturesAfterLongPress(
                onDragStart = { offset ->
                    gridState.layoutInfo.visibleItemsInfo.firstOrNull { itemInfo ->
                        offset.y.toInt() in itemInfo.offset.y..(itemInfo.offset.y + itemInfo.size.height) &&
                            offset.x.toInt() in itemInfo.offset.x..(itemInfo.offset.x + itemInfo.size.width)
                    }?.also {
                        if (currentIndexOfDraggedItem == null) {
                            startPosition = Offset(it.offset.x.toFloat().plus(it.size.width / 2), it.offset.y.toFloat().plus(it.size.height / 2))
                            dragOffset = Offset.Zero
                            currentIndexOfDraggedItem = it.index
                        }
                    }
                },
                onDrag = { change, dragAmount ->
                    dragOffset += Offset(dragAmount.x, dragAmount.y)
                },
                onDragEnd = {
                    val draggingIndex = currentIndexOfDraggedItem
                    val endPosition = startPosition + dragOffset
                    gridState.layoutInfo.visibleItemsInfo.firstOrNull { itemInfo ->
                        endPosition.y.toInt() in itemInfo.offset.y..(itemInfo.offset.y + itemInfo.size.height) &&
                            endPosition.x.toInt() in itemInfo.offset.x..(itemInfo.offset.x + itemInfo.size.width)
                    }?.also { gridItem ->
                        draggingIndex?.let {
                            onFlagMoved(it, gridItem.index)
                        }
                    }
                    startPosition = Offset.Zero
                    currentIndexOfDraggedItem = null
                }
            )
        }
    ) {
        this.items(
            items = viewItems
        ) {
            val country = it
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .graphicsLayer {
                        if (country.index == currentIndexOfDraggedItem) {
                            translationY = dragOffset.y
                            translationX = dragOffset.x
                        }
                    }
                    .zIndex(
                        when (country.index == currentIndexOfDraggedItem) {
                            true -> 4f
                            false -> 1f
                        }
                    )
            ) {
                CountryCard(url = country.flag, name = country.name)
            }
        }
    }

}

@Composable
fun CountryCard(url: String, name: String) {
    Column (Modifier.border(border = BorderStroke(width = 1.dp, color = Color.Gray))) {
        Box(modifier = Modifier
            .size(100.dp)
            .align(Alignment.CenterHorizontally)) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription =  name
            )
        }
        Text(text = name, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DragAndDropTheme {
        CountryCard(url ="https://flagcdn.com/ao.svg", name = "Angola")
    }
}