/*
 * Unitto is a unit converter for Android
 * Copyright (c) 2022-2023 Elshan Agaev
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

package com.sadellie.unitto.feature.settings

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sadellie.unitto.core.ui.R
import com.sadellie.unitto.core.ui.common.Header
import com.sadellie.unitto.core.ui.common.NavigateUpButton
import com.sadellie.unitto.core.ui.common.SegmentedButton
import com.sadellie.unitto.core.ui.common.SegmentedButtonRow
import com.sadellie.unitto.core.ui.common.UnittoListItem
import com.sadellie.unitto.core.ui.common.UnittoScreenWithLargeTopBar
import com.sadellie.unitto.feature.settings.components.ColorSelector
import com.sadellie.unitto.feature.settings.components.MonetModeSelector
import io.github.sadellie.themmo.MonetMode
import io.github.sadellie.themmo.ThemingMode
import io.github.sadellie.themmo.ThemmoController

private val colorSchemes: List<Color> by lazy {
    listOf(
        Color(0xFFF6A1BC),
        Color(0xFFFDA387),
        Color(0xFFEAAF60),
        Color(0xFFC2BD64),
        Color(0xFF94C78A),
        Color(0xFF73C9B5),
        Color(0xFF72C6DA),
        Color(0xFF8FBEF2),
        Color(0xFFB6B3F6),
        Color(0xFFDCA8E4),
    )
}

@Composable
internal fun ThemesRoute(
    navigateUpAction: () -> Unit = {},
    themmoController: ThemmoController,
    viewModel: SettingsViewModel
) {
    ThemesScreen(
        navigateUpAction = navigateUpAction,
        currentThemingMode = themmoController.currentThemingMode,
        onThemeChange = {
            themmoController.setThemingMode(it)
            viewModel.updateThemingMode(it)
        },
        isDynamicThemeEnabled = themmoController.isDynamicThemeEnabled,
        onDynamicThemeChange = {
            // Prevent old devices from using other monet modes when dynamic theming is on
            if (it) {
                themmoController.setMonetMode(MonetMode.TONAL_SPOT)
                viewModel.updateMonetMode(MonetMode.TONAL_SPOT)
            }
            themmoController.enableDynamicTheme(it)
            viewModel.updateDynamicTheme(it)
        },
        isAmoledThemeEnabled = themmoController.isAmoledThemeEnabled,
        onAmoledThemeChange = {
            themmoController.enableAmoledTheme(it)
            viewModel.updateAmoledTheme(it)
        },
        selectedColor = themmoController.currentCustomColor,
        onColorChange = {
            themmoController.setCustomColor(it)
            viewModel.updateCustomColor(it)
        },
        monetMode = themmoController.currentMonetMode,
        onMonetModeChange = {
            themmoController.setMonetMode(it)
            viewModel.updateMonetMode(it)
        }
    )
}

@Composable
private fun ThemesScreen(
    navigateUpAction: () -> Unit,
    currentThemingMode: ThemingMode,
    onThemeChange: (ThemingMode) -> Unit,
    isDynamicThemeEnabled: Boolean,
    onDynamicThemeChange: (Boolean) -> Unit,
    isAmoledThemeEnabled: Boolean,
    onAmoledThemeChange: (Boolean) -> Unit,
    selectedColor: Color,
    onColorChange: (Color) -> Unit,
    monetMode: MonetMode,
    onMonetModeChange: (MonetMode) -> Unit
) {
    val themingModes by remember {
        mutableStateOf(
            mapOf(
                ThemingMode.AUTO to R.string.force_auto_mode,
                ThemingMode.FORCE_LIGHT to R.string.force_light_mode,
                ThemingMode.FORCE_DARK to R.string.force_dark_mode
            )
        )
    }

    UnittoScreenWithLargeTopBar(
        title = stringResource(R.string.theme_setting),
        navigationIcon = { NavigateUpButton(navigateUpAction) }
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {

            item {
                ListItem(
                    leadingContent = {
                        Icon(
                            Icons.Default.Palette,
                            stringResource(R.string.color_theme),
                        )
                    },
                    headlineContent = { Text(stringResource(R.string.color_theme)) },
                    supportingContent = { Text(stringResource(R.string.color_theme_support)) },
                )
            }

            item {
                SegmentedButtonRow(
                    modifier = Modifier.padding(56.dp, 8.dp, 24.dp, 2.dp)
                ) {
                    themingModes.forEach { (mode, stringRes) ->
                        SegmentedButton(
                            onClick = { onThemeChange(mode) },
                            selected = currentThemingMode == mode,
                            content = { Text(stringResource(stringRes)) }
                        )
                    }
                }
            }

            item {
                AnimatedVisibility(
                    visible = currentThemingMode != ThemingMode.FORCE_LIGHT,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut(),
                ) {
                    UnittoListItem(
                        leadingContent = {
                            Icon(
                                Icons.Default.DarkMode,
                                stringResource(R.string.force_amoled_mode),
                            )
                        },
                        label = stringResource(R.string.force_amoled_mode),
                        supportContent = stringResource(R.string.force_amoled_mode_support),
                        switchState = isAmoledThemeEnabled,
                        onSwitchChange = onAmoledThemeChange
                    )
                }
            }

            item { Header(stringResource(R.string.color_scheme)) }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                item {
                    UnittoListItem(
                        leadingContent = {
                            Icon(
                                Icons.Default.Colorize,
                                stringResource(R.string.enable_dynamic_colors),
                            )
                        },
                        label = stringResource(R.string.enable_dynamic_colors),
                        supportContent = stringResource(R.string.enable_dynamic_colors_support),
                        switchState = isDynamicThemeEnabled,
                        onSwitchChange = onDynamicThemeChange
                    )
                }
            }

            item {
                AnimatedVisibility(
                    visible = !isDynamicThemeEnabled,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut(),
                ) {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.selected_color)) },
                        supportingContent = {
                            ColorSelector(
                                modifier = Modifier.padding(top = 12.dp),
                                selected = selectedColor,
                                onItemClick = onColorChange,
                                colorSchemes = colorSchemes,
                                defaultColor = Color(0xFF186c31)
                            )
                        },
                        modifier = Modifier.padding(start = 40.dp)
                    )
                }
            }

            item {
                AnimatedVisibility(
                    visible = (!isDynamicThemeEnabled) and (selectedColor != Color.Unspecified),
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut(),
                ) {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.monet_mode)) },
                        supportingContent = {
                            MonetModeSelector(
                                modifier = Modifier.padding(top = 12.dp),
                                selected = monetMode,
                                onItemClick = onMonetModeChange,
                                monetModes = remember { MonetMode.values().toList() },
                                customColor = selectedColor,
                                themingMode = currentThemingMode,
                                amoledThemeEnabled = isAmoledThemeEnabled,
                            )
                        },
                        modifier = Modifier.padding(start = 40.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ThemesScreen(
        navigateUpAction = {},
        currentThemingMode = ThemingMode.AUTO,
        onThemeChange = {},
        isDynamicThemeEnabled = false,
        onDynamicThemeChange = {},
        isAmoledThemeEnabled = false,
        onAmoledThemeChange = {},
        selectedColor = Color.Unspecified,
        onColorChange = {},
        monetMode = MonetMode.TONAL_SPOT,
        onMonetModeChange = {}
    )
}
