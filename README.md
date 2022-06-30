# compose_drag_and_drop
Drag and drop compose cards 

A simple class to drag and drop cards in Jetpack Compose. Major Code for drag and drop lies in 
```

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
    }
}
```



https://user-images.githubusercontent.com/51356900/176582915-55ec54e6-a249-4209-b9b8-bed2246e4c52.mp4

