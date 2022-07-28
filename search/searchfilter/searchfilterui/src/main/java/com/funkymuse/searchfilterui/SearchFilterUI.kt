package com.funkymuse.searchfilterui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.funkymuse.aurora.radiobutton.RadioButtonWithText
import com.funkymuse.aurora.searchdata.SearchViewModel


@Composable
fun SearchFilterUI(
    searchViewModel: SearchViewModel = hiltViewModel()
) {
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
                isChecked = searchViewModel.searchInFieldsCheckedPosition == index,
                onRadioButtonClicked = {
                    searchViewModel.argumentSearchInFieldsCheckedPosition = index
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
                isChecked = searchViewModel.searchWithMaskWord,
                onRadioButtonClicked = {
                    searchViewModel.argumentSearchWithMaskWord = !searchViewModel.argumentSearchWithMaskWord
                })
        }

        item {
            Spacer(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 46.dp)
            )
        }
    }
}