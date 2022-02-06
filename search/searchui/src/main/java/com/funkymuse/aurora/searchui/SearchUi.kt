package com.funkymuse.aurora.searchui

import android.util.Log
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.funkymuse.aurora.navigator.AuroraNavigatorViewModel
import com.funkymuse.aurora.radiobutton.RadioButtonWithText
import com.funkymuse.aurora.searchdata.SearchViewModel
import com.funkymuse.aurora.searchresultdestination.SearchResultDestination
import com.funkymuse.aurora.toaster.ToasterViewModel
import com.funkymuse.searchfilterdestination.SearchFilterDestination
import com.funkymuse.style.shape.BottomSheetShapes
import com.google.accompanist.insets.navigationBarsPadding
import kotlinx.coroutines.launch

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun Search(searchViewModel: SearchViewModel = hiltViewModel()) {
    val navigatorViewModel: AuroraNavigatorViewModel = hiltViewModel()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            SearchInput() { text ->
                navigatorViewModel.navigate(
                    SearchResultDestination.createSearchRoute(
                        text.trim(),
                        searchViewModel.searchInFieldsCheckedPosition,
                        searchViewModel.searchWithMaskWord
                    )
                )
            }
            SearchInputExplained()
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    navigatorViewModel.navigate(
                        SearchFilterDestination.createRoute(
                            searchViewModel.searchInFieldsCheckedPosition,
                            searchViewModel.searchWithMaskWord
                        )
                    )
                },
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
