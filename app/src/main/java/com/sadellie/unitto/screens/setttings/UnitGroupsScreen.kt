/*
 * Unitto is a unit converter for Android
 * Copyright (c) 2022 Elshan Agaev
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

package com.sadellie.unitto.screens.setttings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sadellie.unitto.R
import com.sadellie.unitto.screens.common.Header
import com.sadellie.unitto.screens.common.UnittoLargeTopAppBar
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun UnitGroupsScreen(
    viewModel: SettingsViewModel,
    navigateUpAction: () -> Unit
) {
    UnittoLargeTopAppBar(
        title = stringResource(R.string.unit_groups_setting),
        navigateUpAction = navigateUpAction
    ) { paddingValues ->

        val shownUnits = viewModel.shownUnitGroups.collectAsState()
        val hiddenUnits = viewModel.hiddenUnitGroups.collectAsState()

        val state = rememberReorderableLazyListState(
            onMove = viewModel::onMove,
            canDragOver = viewModel::canDragOver,
            onDragEnd = { _, _ -> viewModel.onDragEnd() }
        )

        LazyColumn(
            state = state.listState,
            modifier = Modifier
                .padding(paddingValues)
                .reorderable(state)
        ) {
            item(key = "enabled") {
                Header(text = stringResource(R.string.enabled_label))
            }

            items(shownUnits.value, { it }) { item ->
                ReorderableItem(state, key = item) { isDragging ->
                    val background = animateColorAsState(
                        if (isDragging) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                    )
                    val cornerRadius = animateDpAsState(if (isDragging) 16.dp else 0.dp)

                    ListItem(
                        headlineText = { Text(stringResource(item.res)) },
                        modifier = Modifier
                            .padding(horizontal = cornerRadius.value)
                            .clip(RoundedCornerShape(cornerRadius.value))
                            .clickable { viewModel.hideUnitGroup(item) }
                            .detectReorderAfterLongPress(state),
                        colors = ListItemDefaults.colors(
                            containerColor = background.value
                        ),
                        leadingContent = {
                            Icon(
                                Icons.Default.RemoveCircle,
                                stringResource(R.string.disable_unit_group_description),
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(false),
                                    onClick = { viewModel.hideUnitGroup(item) }
                                )
                            )
                        },
                        trailingContent = {
                            Icon(
                                Icons.Default.DragIndicator,
                                stringResource(R.string.reorder_unit_group_description),
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = rememberRipple(false),
                                        onClick = {}
                                    )
                                    .detectReorder(state)
                            )
                        },
                    )
                }
            }

            item(key = "disabled") {
                Header(
                    text = stringResource(R.string.disabled_label),
                    modifier = Modifier.animateItemPlacement()
                )
            }

            items(hiddenUnits.value, { it }) {
                ListItem(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { viewModel.returnUnitGroup(it) }
                        .animateItemPlacement(),
                    headlineText = { Text(stringResource(it.res)) },
                    trailingContent = {
                        Icon(
                            Icons.Default.AddCircleOutline,
                            stringResource(R.string.enable_unit_group_description),
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(false),
                                onClick = { viewModel.returnUnitGroup(it) }
                            )
                        )
                    }
                )
            }
        }
    }
}
