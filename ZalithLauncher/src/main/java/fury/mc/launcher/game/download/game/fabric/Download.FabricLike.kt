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

package fury.mc.launcher.game.download.game.fabric

import fury.mc.launcher.coroutine.Task
import fury.mc.launcher.game.addons.mirror.mapBMCLMirrorUrls
import fury.mc.launcher.game.addons.modloader.fabriclike.FabricLikeVersion
import fury.mc.launcher.utils.file.ensureParentDirectory
import fury.mc.launcher.utils.network.fetchStringFromUrls
import kotlinx.coroutines.Dispatchers
import java.io.File

const val FABRIC_LIKE_DOWNLOAD_ID = "Download.FabricLike"

fun getFabricLikeDownloadTask(
    fabricLikeVersion: FabricLikeVersion,
    tempVersionJson: File
): Task {
    return Task.runTask(
        id = FABRIC_LIKE_DOWNLOAD_ID,
        dispatcher = Dispatchers.IO,
        task = {
            //下载版本 Json
            val loaderJson = fetchStringFromUrls(fabricLikeVersion.loaderJsonUrl.mapBMCLMirrorUrls())
            tempVersionJson
                .ensureParentDirectory()
                .writeText(loaderJson)
        }
    )
}