package com.funkymuse.aurora.search

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavBackStackEntry
import com.funkymuse.aurora.R
import com.funkymuse.aurora.ToasterViewModel
import com.funkymuse.aurora.extensions.hiltViewModel
import com.funkymuse.aurora.ui.theme.BottomSheetShapes
import kotlinx.coroutines.launch

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */

data class RadioButtonEntries(@StringRes val title: Int)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Search(navBackStackEntry: NavBackStackEntry, onInputText: (inputText: String) -> Unit = {}) {

    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    var searchInCheckedPosition by remember { mutableStateOf(0) }
    var searchInFieldsCheckedPosition by remember { mutableStateOf(0) }
    var searchWithMaskWord by remember { mutableStateOf(false) }

    val searchInEntries = listOf(
        RadioButtonEntries(R.string.libgen_sci_tech),
        RadioButtonEntries(R.string.scientific_articles),
        RadioButtonEntries(R.string.fiction),
        RadioButtonEntries(R.string.comics),
        RadioButtonEntries(R.string.standards),
        RadioButtonEntries(R.string.magazines),
    )

    val searchInFieldEntries = listOf(
        RadioButtonEntries(R.string.default_column),
        RadioButtonEntries(R.string.title),
        RadioButtonEntries(R.string.author),
        RadioButtonEntries(R.string.series),
        RadioButtonEntries(R.string.publisher),
        RadioButtonEntries(R.string.year),
        RadioButtonEntries(R.string.isbn),
        RadioButtonEntries(R.string.language),
        RadioButtonEntries(R.string.md5),
        RadioButtonEntries(R.string.tags),
        RadioButtonEntries(R.string.extension),
    )

    ModalBottomSheetLayout(
        modifier = Modifier.zIndex(1f),
        sheetState = state,
        sheetShape = BottomSheetShapes.large,
        sheetContent = {
            LazyColumn {
                item {
                    Text(
                        text = stringResource(R.string.search_in), modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                    )
                }

                itemsIndexed(searchInEntries) { index, item ->
                    RadioButtonWithText(
                        text = item.title,
                        isChecked = searchInCheckedPosition == index,
                        onRadioButtonClicked = {
                            searchInCheckedPosition = index
                        })
                }

                item {
                    Text(
                        text = stringResource(R.string.mask_word), modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                    )
                }

                item {
                    RadioButtonWithText(
                        text = R.string.search_with_mask_word,
                        isChecked = searchWithMaskWord,
                        onRadioButtonClicked = {
                            searchWithMaskWord = !searchWithMaskWord
                        })
                }

                item {
                    Text(
                        text = stringResource(R.string.search_in_fields), modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                    )
                }

                itemsIndexed(searchInFieldEntries) { index, item ->
                    RadioButtonWithText(
                        text = item.title,
                        isChecked = searchInFieldsCheckedPosition == index,
                        onRadioButtonClicked = {
                            searchInFieldsCheckedPosition = index
                        })
                }

                item {
                    Spacer(modifier = Modifier.padding(bottom = 64.dp))
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchInput(navBackStackEntry, onInputText)
            SearchInputExplained()
        }

        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp)
        ) {
            FloatingActionButton(
                onClick = { scope.launch { state.show() } },
            ) {
                Icon(
                    Icons.Filled.FilterList,
                    contentDescription = stringResource(id = R.string.filter),
                    tint = Color.White
                )
            }
        }
    }

}

@Composable
fun RadioButtonWithText(
    @StringRes text: Int,
    isChecked: Boolean,
    onRadioButtonClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        RadioButton(
            selected = isChecked, onClick = onRadioButtonClicked, modifier = Modifier.align(
                Alignment.CenterVertically
            )
        )
        Text(
            text = stringResource(id = text), modifier = Modifier
                .align(
                    Alignment.CenterVertically
                )
                .padding(start = 8.dp)
        )
    }
}

@Composable
fun SearchInputExplained() {
    Text(
        text = stringResource(id = R.string.search_text), fontSize = 12.sp,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 36.dp, end = 24.dp)
            .animateContentSize()
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchInput(
    navBackStackEntry: NavBackStackEntry,
    onInputText: (inputText: String) -> Unit = {}
) {
    val viewModel = hiltViewModel<ToasterViewModel>(navBackStackEntry)
    val keyboardController = LocalSoftwareKeyboardController.current
    /*migrate to rememberSavable when
    https://issuetracker.google.com/issues/180042685
    beta02 is released*/
    var inputText by remember { mutableStateOf("") }
    val invalidInput = inputText.isBlank() || inputText.length < 3

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp),
        isError = invalidInput,
        label = { Text(text = stringResource(id = R.string.search)) },
        value = inputText,
        onValueChange = { inputText = it },
        keyboardOptions = KeyboardOptions(
            KeyboardCapitalization.Words, autoCorrect = false,
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(onSearch = {
            if (invalidInput) {
                viewModel.shortToast(R.string.empty_or_short_input)
                return@KeyboardActions
            }
            keyboardController?.hideSoftwareKeyboard()
            onInputText(inputText)
        })
    )
}
