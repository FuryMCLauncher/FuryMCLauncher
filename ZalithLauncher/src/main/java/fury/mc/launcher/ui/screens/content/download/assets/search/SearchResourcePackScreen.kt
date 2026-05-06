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

package fury.mc.launcher.ui.screens.content.download.assets.search

import androidx.compose.runtime.Composable
import fury.mc.launcher.game.download.assets.platform.Platform
import fury.mc.launcher.game.download.assets.platform.PlatformClasses
import fury.mc.launcher.game.download.assets.platform.curseforge.models.CurseForgeResourcePackCategory
import fury.mc.launcher.game.download.assets.platform.modrinth.models.ModrinthFeatures
import fury.mc.launcher.game.download.assets.platform.modrinth.models.ModrinthResourcePackCategory
import fury.mc.launcher.ui.screens.NormalNavKey
import fury.mc.launcher.ui.screens.TitledNavKey

@Composable
fun SearchResourcePackScreen(
    mainScreenKey: TitledNavKey?,
    downloadScreenKey: TitledNavKey?,
    downloadResourcePackScreenKey: TitledNavKey,
    downloadResourcePackScreenCurrentKey: TitledNavKey?,
    swapToDownload: (Platform, projectId: String, iconUrl: String?) -> Unit = { _, _, _ -> }
) {
    SearchAssetsScreen(
        mainScreenKey = mainScreenKey,
        parentScreenKey = downloadResourcePackScreenKey,
        parentCurrentKey = downloadScreenKey,
        screenKey = NormalNavKey.SearchResourcePack,
        currentKey = downloadResourcePackScreenCurrentKey,
        platformClasses = PlatformClasses.RESOURCE_PACK,
        initialPlatform = Platform.MODRINTH,
        getCategories = { platform ->
            when (platform) {
                Platform.CURSEFORGE -> CurseForgeResourcePackCategory.entries
                Platform.MODRINTH -> ModrinthResourcePackCategory.entries
            }
        },
        mapCategories = { platform, string ->
            when (platform) {
                Platform.MODRINTH -> {
                    ModrinthResourcePackCategory.entries.find { it.facetValue() == string }
                        ?: ModrinthFeatures.entries.find { it.facetValue() == string }
                }
                Platform.CURSEFORGE -> {
                    CurseForgeResourcePackCategory.entries.find { it.describe() == string }
                }
            }
        },
        swapToDownload = swapToDownload
    )
}