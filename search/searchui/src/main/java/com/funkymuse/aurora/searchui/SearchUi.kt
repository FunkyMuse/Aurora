package com.funkymuse.aurora.searchui

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
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.funkymuse.aurora.navigator.NavigatorViewModel
import com.funkymuse.aurora.radiobutton.RadioButtonWithText
import com.funkymuse.aurora.searchdata.SearchViewModel
import com.funkymuse.aurora.searchresultdestination.SearchResultDestination
import com.funkymuse.aurora.toaster.ToasterViewModel
import com.funkymuse.style.shape.BottomSheetShapes
import com.google.accompanist.insets.navigationBarsPadding
import kotlinx.coroutines.launch

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */


@Composable
@OptIn(ExperimentalMaterialApi::class)
fun Search() {
    val navigatorViewModel: NavigatorViewModel = hiltViewModel()
    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    var searchInFieldsCheckedPosition by rememberSaveable { mutableStateOf(0) }
    var searchWithMaskWord by rememberSaveable { mutableStateOf(false) }
    val searchViewModel = hiltViewModel<SearchViewModel>()


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
                    navigatorViewModel.navigate { SearchResultDestination.createSearchRoute(it.trim(), searchInFieldsCheckedPosition, searchWithMaskWord) }
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
    val viewModel = hiltViewModel<ToasterViewModel>()

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
