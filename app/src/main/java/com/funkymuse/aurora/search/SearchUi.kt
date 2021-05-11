package com.funkymuse.aurora.search

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import com.funkymuse.aurora.R
import com.funkymuse.aurora.ToasterViewModel
import com.funkymuse.aurora.ui.theme.BottomSheetShapes
import com.google.accompanist.insets.navigationBarsPadding
import kotlinx.coroutines.launch

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */

data class RadioButtonEntries(@StringRes val title: Int)

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun Search(
    onInputText: (inputText: String, searchInFieldsCheckedPosition: Int, searchWithMaskWord: Boolean) -> Unit = { _, _, _ -> }
) {

    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    var searchInFieldsCheckedPosition by rememberSaveable { mutableStateOf(0) }
    var searchWithMaskWord by rememberSaveable { mutableStateOf(false) }
    val searchViewModel = hiltNavGraphViewModel<SearchViewModel>()


    val zIndex = if (state.targetValue == ModalBottomSheetValue.Hidden) {
        0f
    } else {
        2f
    }

    ModalBottomSheetLayout(
        modifier = Modifier
            .navigationBarsPadding()
            .zIndex(zIndex),
        sheetState = state,
        sheetShape = BottomSheetShapes.large,
        sheetContent = {
            LazyColumn {
                item {
                    Text(
                        text = stringResource(R.string.search_in_fields), modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                    )
                }

                itemsIndexed(searchViewModel.searchInFieldEntries) { index, item ->
                    RadioButtonWithText(
                        text = item.title,
                        isChecked = searchInFieldsCheckedPosition == index,
                        onRadioButtonClicked = {
                            searchInFieldsCheckedPosition = index
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
                    Spacer(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(bottom = 36.dp)
                    )
                }
            }
        }
    ) {

        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (searchInput, searchInputExplanation, filter) = createRefs()
            Box(modifier = Modifier.constrainAs(searchInput) {
                centerHorizontallyTo(parent)
                centerVerticallyTo(parent)
            }) {
                SearchInput() {
                    onInputText(
                        it,
                        searchInFieldsCheckedPosition,
                        searchWithMaskWord
                    )
                }
            }
            Box(modifier = Modifier.constrainAs(searchInputExplanation) {
                top.linkTo(searchInput.bottom)
                centerHorizontallyTo(parent)
            }) {
                SearchInputExplained()
            }

            Box(
                modifier = Modifier
                    .constrainAs(filter) {
                        bottom.linkTo(parent.bottom)
                        centerHorizontallyTo(parent)
                    }
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
            selected = isChecked,
            onClick = onRadioButtonClicked,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Text(
            text = stringResource(id = text), modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp)
                .clickable { onRadioButtonClicked() }
        )
    }
}

@Composable
fun RadioButtonWithTextNotClickable(
    @StringRes text: Int,
    isChecked: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Image(
            imageVector = if (isChecked) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
            modifier = Modifier.align(Alignment.CenterVertically),
            contentDescription = null
        )
        Text(
            text = stringResource(id = text), modifier = Modifier
                .align(Alignment.CenterVertically)
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
    onInputText: (inputText: String) -> Unit = {}
) {
    val viewModel = hiltNavGraphViewModel<ToasterViewModel>()

    val keyboardController = LocalSoftwareKeyboardController.current
    var inputText by rememberSaveable { mutableStateOf("") }
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
            keyboardController?.hide()
            onInputText(inputText)
        })
    )
}
