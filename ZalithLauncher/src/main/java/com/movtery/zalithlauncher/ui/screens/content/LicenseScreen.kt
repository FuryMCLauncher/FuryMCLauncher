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

package com.movtery.zalithlauncher.ui.screens.content

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.theme.backgroundColor
import com.movtery.zalithlauncher.ui.theme.onBackgroundColor
import com.movtery.zalithlauncher.utils.animation.swapAnimateDpAsState
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel

@Composable
fun LicenseScreen(
    key: NormalNavKey.License,
    backStackViewModel: ScreenBackStackViewModel
) {
    val context = LocalContext.current
    var licenseState by remember { mutableStateOf(LicenseState.LOADING) }
    val license = remember { mutableStateListOf<String>() }

    LaunchedEffect(key, context) {
        licenseState = LicenseState.LOADING
        license.clear()
        license.addAll(key.raw.readRawLicenseLines(context))
        licenseState = LicenseState.FINE
    }

    BaseScreen(
        screenKey = key,
        currentKey = backStackViewModel.mainScreen.currentKey
    ) { isVisible ->
        val yOffset by swapAnimateDpAsState(
            targetValue = (-40).dp,
            swapIn = isVisible
        )

        Surface(
            modifier = Modifier.fillMaxSize()
                .offset { IntOffset(x = 0, y = yOffset.roundToPx())
            },
            color = backgroundColor(),
            contentColor = onBackgroundColor()
        ) {
            when (licenseState) {
                LicenseState.LOADING -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                LicenseState.FINE -> {
                    if (license.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            items(license) { line ->
                                Text(
                                    text = line,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private enum class LicenseState {
    /**
     * 加载中
     */
    LOADING,

    /**
     * 加载完成
     */
    FINE
}

/**
 * 读取协议文本内容
 */
private fun Int.readRawLicenseLines(context: Context): List<String> {
    return context.resources.openRawResource(this)
        .bufferedReader()
        .readLines()
}