package moe.tabidachi.game.ui.emulator

import android.app.Activity
import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.PivotOffsets
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ListItem
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import moe.tabidachi.game.data.Emulator
import moe.tabidachi.game.data.SettingsProvider
import moe.tabidachi.game.ui.components.Back
import moe.tabidachi.game.ui.components.ChooseDpadHorizontal
import moe.tabidachi.game.ui.components.ExitDialog
import moe.tabidachi.game.ui.components.GamepadIndicatorBar
import moe.tabidachi.game.ui.components.Search
import moe.tabidachi.game.ui.components.SearchDialog
import moe.tabidachi.game.ui.components.Select
import moe.tabidachi.game.ui.components.StatusBar
import moe.tabidachi.game.ui.theme.EmulatorBorderColor
import java.io.File

private const val PageCount = 500
private const val InitialPage = PageCount / 2

private const val TAG = "EmulatorScreen"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmulatorScreen(
    settings: SettingsProvider,
    onEmulatorSelected: (Emulator) -> Unit,
    //onStartButtonPressed: () -> Unit = {}
) {
    val InitialPage = if (Emulator.enables.size <= 6) 0 else PageCount / 2
    val PageCount = if (Emulator.enables.size <= 6) Emulator.enables.size else PageCount
    val pagerState = rememberPagerState(
        initialPage = InitialPage,
        pageCount = {
            PageCount
        }
    )

    val listState = rememberTvLazyListState(initialFirstVisibleItemIndex = InitialPage)
    var lastFocusedItemIndex by rememberSaveable { mutableIntStateOf(InitialPage) }
    val scope = rememberCoroutineScope()
    var emulator by remember {
        mutableStateOf(Emulator.Auto_Allgames)
    }
    var searchDialogVisible by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val gameCount by settings.getGameCount(emulator).collectAsState(initial = 0)

    var isExitDialogVisible by remember {
        mutableStateOf(false)
    }
    BackHandler {
        isExitDialogVisible = true
    }
    Box(
        modifier = Modifier.onPreviewKeyEvent {
            //Log.d(TAG, "onKeyEvent: $it")
            when (it.nativeKeyEvent.keyCode) {
                KeyEvent.KEYCODE_BUTTON_START -> {
                    searchDialogVisible = true
                    //onStartButtonPressed()
                    true
                }

                else -> false
            }
        }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val index = page % Emulator.enables.size
            AsyncImage(
                model = File(settings.artDir, Emulator.enables[index].art),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            TvLazyRow(
                state = listState,
                pivotOffsets = PivotOffsets(parentFraction = 0.15f),
                modifier = Modifier,
            ) {
                items(PageCount) { index ->
                    val currentEmulator = Emulator.enables[index % Emulator.enables.size]
                    Card(
                        onClick = {
                            onEmulatorSelected(emulator)
                        },
                        colors = CardDefaults.colors(
                            containerColor = Color.Transparent
                        ),
                        scale = CardDefaults.scale(
                            scale = 1f, focusedScale = 1.1f, pressedScale = 1f
                        ),
                        border = CardDefaults.border(
                            focusedBorder = Border(
                                border = EmulatorBorderStroke,
                                shape = EmulatorBorderShape
                            )
                        ),
                        modifier = Modifier
                            .fillMaxHeight(0.3f)
                            .aspectRatio(1f)
                            .padding(16.dp)
                            .onFocusChanged {
                                if (it.isFocused) {
                                    emulator = currentEmulator
                                    lastFocusedItemIndex = index
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                    settings.play()
                                }
                            }
                            .focusRequester(currentEmulator.focusRequester)
                            .onGloballyPositioned {
                                if (lastFocusedItemIndex == index) {
                                    currentEmulator.focusRequester.requestFocus()
                                }
                            },
                    ) {
                        AsyncImage(
                            model = File(settings.logoDir, currentEmulator.logo),
                            contentDescription = null
                        )
                    }
                }
            }
            GamepadIndicatorBar(
                leadingContent = {
                    Text(
                        text = "$gameCount GAMES"
                    )
                }, items = IndicatorItems
            )
        }
        StatusBar(
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

    SearchDialog(
        visible = searchDialogVisible,
        settings = settings,
        onDismissRequest = {
            searchDialogVisible = false
        }, onGameStart = { (file, emulator) ->
            settings.launch(context, emulator, file)
        }
    )
    ExitDialog(
        visible = isExitDialogVisible,
        onDismissRequest = {
            isExitDialogVisible = false
        }, onConfirm = {
            runCatching {
                (context as Activity).finish()
            }.onFailure {
                it.printStackTrace()
            }
        }, onDismiss = {
            isExitDialogVisible = false
        }
    )
}

val EmulatorBorderShape = RoundedCornerShape(4.dp)

val EmulatorBorderStroke = BorderStroke(
    width = 4.dp,
    color = EmulatorBorderColor
)

private val IndicatorItems = listOf(
    ChooseDpadHorizontal,
    Search,
    Select,
    Back
)

@Composable
fun Expanded(
    emulator: Emulator,
    files: List<File>
) {
    val context = LocalContext.current
    var focusedItem by remember {
        mutableStateOf<File?>(null)
    }

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        TvLazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 32.dp)
        ) {
            items(files) { file ->
                ListItem(
                    selected = false,
                    onClick = {
                        //settings.launch(emulator, file)
                    }, headlineContent = {
                        Text(text = file.name)
                    }, modifier = Modifier.onFocusChanged {
                        if (it.isFocused) {
                            focusedItem = file
                        }
                    }
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            focusedItem?.let {
                AsyncImage(
                    model = File(it.parent, "images/${it.nameWithoutExtension}.png"),
                    contentDescription = null
                )
            }
        }
    }
}





