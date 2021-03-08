package com.funkymuse.aurora.search

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import com.funkymuse.aurora.R
import com.funkymuse.aurora.ToasterViewModel
import com.funkymuse.aurora.extensions.hiltViewModel

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */
@Composable
fun Search(navBackStackEntry: NavBackStackEntry, onInputText: (inputText: String) -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchInput(navBackStackEntry, onInputText)
        SearchInputExplained()
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
