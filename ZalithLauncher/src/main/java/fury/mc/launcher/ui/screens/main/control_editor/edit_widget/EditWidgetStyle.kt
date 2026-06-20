/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package fury.mc.launcher.ui.screens.main.control_editor.edit_widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fury.mc.layer_controller.layout.RendererStyleBox
import fury.mc.layer_controller.observable.ObservableButtonStyle
import fury.mc.layer_controller.observable.ObservableNormalData
import fury.mc.layer_controller.observable.ObservableTextData
import fury.mc.layer_controller.observable.ObservableWidget
import fury.mc.launcher.R
import fury.mc.launcher.setting.enums.isLauncherInDarkTheme
import fury.mc.launcher.ui.base.BaseScreen
import fury.mc.launcher.ui.components.MarqueeText
import fury.mc.launcher.ui.screens.TitledNavKey
import fury.mc.launcher.ui.screens.main.control_editor.InfoLayoutItem
import fury.mc.launcher.ui.screens.main.control_editor.InfoLayoutTextItem
import fury.mc.launcher.utils.string.isNotEmptyOrBlank

/**
 * 为控件选择外观
 */
@Composable
fun EditWidgetStyle(
    screenKey: TitledNavKey,
    currentKey: TitledNavKey?,
    data: ObservableWidget,
    styles: List<ObservableButtonStyle>,
    openStyleList: () -> Unit
) {
    BaseScreen(
        screenKey = screenKey,
        currentKey = currentKey
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (data) {
                is ObservableTextData -> {
                    MainContent(
                        styles = styles,
                        buttonStyle = data.buttonStyle,
                        onButtonStyleChanged = { data.buttonStyle = it },
                        openStyleList = openStyleList
                    )
                }
                is ObservableNormalData -> {
                    MainContent(
                        styles = styles,
                        buttonStyle = data.buttonStyle,
                        onButtonStyleChanged = { data.buttonStyle = it },
                        openStyleList = openStyleList
                    )
                }
            }
        }
    }
}

@Composable
private fun MainContent(
    styles: List<ObservableButtonStyle>,
    buttonStyle: String?,
    onButtonStyleChanged: (String?) -> Unit,
    openStyleList: () -> Unit
) {
    if (styles.isNotEmpty()) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Adaptive(minSize = 120.dp)
        ) {
            items(styles) { style ->
                ChoseStyleItem(
                    modifier = Modifier.padding(all = 8.dp),
                    style = style,
                    selected = buttonStyle == style.uuid,
                    onSelectedChange = { selected ->
                        onButtonStyleChanged(if (selected) style.uuid else null)
                    }
                )
            }
        }
    } else {
        InfoLayoutTextItem(
            modifier = Modifier.padding(all = 24.dp),
            title = stringResource(R.string.control_editor_edit_style_config_empty),
            onClick = openStyleList
        )
    }
}

@Composable
private fun ChoseStyleItem(
    style: ObservableButtonStyle,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    InfoLayoutItem(
        modifier = modifier,
        onClick = {
            onSelectedChange(!selected)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RendererStyleBox(
                modifier = Modifier.size(50.dp),
                style = style,
                text = "abc",
                isDark = isLauncherInDarkTheme(),
                isPressed = false
            )
            Spacer(modifier = Modifier.height(4.dp))
            MarqueeText(
                modifier = Modifier.fillMaxWidth(),
                text = style.name.takeIf { it.isNotEmptyOrBlank() } ?: stringResource(R.string.generic_unspecified),
                textAlign = TextAlign.Center
            )
            RadioButton(
                selected = selected,
                onClick = {
                    onSelectedChange(!selected)
                }
            )
        }
    }
}