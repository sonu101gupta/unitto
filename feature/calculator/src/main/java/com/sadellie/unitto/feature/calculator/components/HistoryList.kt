/*
 * Unitto is a unit converter for Android
 * Copyright (c) 2023 Elshan Agaev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.sadellie.unitto.feature.calculator.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sadellie.unitto.core.ui.Formatter
import com.sadellie.unitto.core.ui.common.textfield.UnittoTextToolbar
import com.sadellie.unitto.core.ui.common.textfield.copyWithoutGrouping
import com.sadellie.unitto.core.ui.theme.NumbersTextStyleDisplayMedium
import com.sadellie.unitto.data.model.HistoryItem
import com.sadellie.unitto.feature.calculator.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
internal fun HistoryList(
    modifier: Modifier,
    historyItems: List<HistoryItem>,
    historyItemHeightCallback: (Int) -> Unit,
) {
    val verticalArrangement by remember(historyItems) {
        derivedStateOf {
            if (historyItems.isEmpty()) {
                Arrangement.Center
            } else {
                Arrangement.spacedBy(16.dp, Alignment.Bottom)
            }
        }
    }

    LazyColumn(
        modifier = modifier,
        reverseLayout = true,
        verticalArrangement = verticalArrangement
    ) {
        if (historyItems.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .onPlaced { historyItemHeightCallback(it.size.height) }
                        .fillParentMaxWidth()
                        .padding(vertical = 32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.History, null)
                    Text(stringResource(R.string.calculator_no_history))
                }
            }
        } else {
            // We do this so that callback for items height is called only once
            item {
                HistoryListItem(
                    modifier = Modifier.onPlaced { historyItemHeightCallback(it.size.height) },
                    historyItem = historyItems.first()
                )
            }
            items(historyItems.drop(1)) { historyItem ->
                HistoryListItem(
                    modifier = Modifier,
                    historyItem = historyItem
                )
            }
        }
    }
}

@Composable
private fun HistoryListItem(
    modifier: Modifier = Modifier,
    historyItem: HistoryItem,
) {
    val clipboardManager = LocalClipboardManager.current
    val expression = Formatter.format(historyItem.expression)
    var expressionValue by remember(expression) {
        mutableStateOf(TextFieldValue(expression, TextRange(expression.length)))
    }
    val result = Formatter.format(historyItem.result)
    var resultValue by remember(result) {
        mutableStateOf(TextFieldValue(result, TextRange(result.length)))
    }

    Column(modifier = modifier) {
        CompositionLocalProvider(
            LocalTextInputService provides null,
            LocalTextToolbar provides UnittoTextToolbar(
                view = LocalView.current,
                copyCallback = { clipboardManager.copyWithoutGrouping(expressionValue) }
            )
        ) {
            BasicTextField(
                value = expressionValue,
                onValueChange = { expressionValue = it },
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .horizontalScroll(rememberScrollState(), reverseScrolling = true),
                textStyle = NumbersTextStyleDisplayMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.End),
                readOnly = true
            )
        }

        CompositionLocalProvider(
            LocalTextInputService provides null,
            LocalTextToolbar provides UnittoTextToolbar(
                view = LocalView.current,
                copyCallback = { clipboardManager.copyWithoutGrouping(resultValue) }
            )
        ) {
            BasicTextField(
                value = resultValue,
                onValueChange = { resultValue = it },
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .horizontalScroll(rememberScrollState(), reverseScrolling = true),
                textStyle = NumbersTextStyleDisplayMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), textAlign = TextAlign.End),
                readOnly = true
            )
        }
    }
}

@Preview
@Composable
private fun PreviewHistoryList() {
    val dtf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())

    val historyItems = listOf(
        "13.06.1989 23:59:15",
        "13.06.1989 23:59:16",
        "13.06.1989 23:59:17",
        "14.06.1989 23:59:17",
        "14.06.1989 23:59:18",
        "14.07.1989 23:59:18",
        "14.07.1989 23:59:19",
        "14.07.2005 23:59:19",
    ).map {
        HistoryItem(
            date = dtf.parse(it)!!,
            expression = "12345".repeat(10),
            result = "67890"
        )
    }

    HistoryList(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .fillMaxSize(),
        historyItems = historyItems
    ) {}
}
