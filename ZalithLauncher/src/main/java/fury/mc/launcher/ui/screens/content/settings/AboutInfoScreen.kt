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

package fury.mc.launcher.ui.screens.content.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import fury.mc.launcher.BuildConfig
import fury.mc.launcher.R
import fury.mc.launcher.game.plugin.ApkPlugin
import fury.mc.launcher.game.plugin.PluginLoader
import fury.mc.launcher.game.plugin.appCacheIcon
import fury.mc.launcher.info.InfoDistributor
import fury.mc.launcher.library.LibraryInfo
import fury.mc.launcher.library.libraryData
import fury.mc.launcher.path.URL_COMMUNITY
import fury.mc.launcher.path.URL_MCMOD
import fury.mc.launcher.path.URL_PROJECT
import fury.mc.launcher.path.URL_SUPPORT
import fury.mc.launcher.path.URL_WEBLATE
import fury.mc.launcher.ui.base.BaseScreen
import fury.mc.launcher.ui.components.AnimatedLazyColumn
import fury.mc.launcher.ui.components.CardTitleLayout
import fury.mc.launcher.ui.screens.NestedNavKey
import fury.mc.launcher.ui.screens.NormalNavKey
import fury.mc.launcher.ui.screens.TitledNavKey
import fury.mc.launcher.ui.screens.content.settings.layouts.CardPosition
import fury.mc.launcher.ui.screens.content.settings.layouts.SettingsCard
import fury.mc.launcher.ui.theme.itemColor
import fury.mc.launcher.ui.theme.onItemColor

@Composable
fun AboutInfoScreen(
    key: NestedNavKey.Settings,
    settingsScreenKey: TitledNavKey?,
    mainScreenKey: TitledNavKey?,
    checkUpdate: () -> Unit,
    openLicense: (raw: Int) -> Unit,
    openLink: (url: String) -> Unit
) {
    BaseScreen(
        Triple(key, mainScreenKey, false),
        Triple(NormalNavKey.Settings.AboutInfo, settingsScreenKey, false)
    ) { isVisible ->
        AnimatedLazyColumn(
            modifier = Modifier.fillMaxSize(),
            isVisible = isVisible,
            contentPadding = PaddingValues(12.dp)
        ) { scope ->
            
            // CUSTOM CARD 1: Main App Info & Actions (Polished for MD3E)
            animatedItem(scope) { yOffset ->
                SettingsCard(
                    modifier = Modifier.offset { IntOffset(0, yOffset.roundToPx()) },
                    position = CardPosition.Single
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(80.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFE53935)
                            ) {
                                Image(
                                    modifier = Modifier.padding(12.dp),
                                    painter = painterResource(R.drawable.img_launcher),
                                    contentDescription = null
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PillButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Check For Updates",
                                    onClick = checkUpdate,
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    PillButton(
                                        modifier = Modifier.weight(1f),
                                        text = "Repo",
                                        onClick = { openLink("https://github.com/FuryMCLauncher/FuryMCLauncher") },
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                    PillButton(
                                        modifier = Modifier.weight(1f),
                                        text = "Wiki",
                                        onClick = { openLink("https://github.com/FuryMCLauncher/FuryMCLauncher/wiki") },
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Text(
                                modifier = Modifier.padding(12.dp),
                                text = "FuryMC Launcher is a high-performance fork of Zalith Launcher 2, optimized for offline play and unrestricted access.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 16.dp),
                                    text = "Made by UnTamed-Fury",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Button(
                                    onClick = { openLink(URL_SUPPORT) },
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text("Support Project")
                                }
                            }
                        }
                    }
                }
            }

            // CARD 2: Credits & Original Project (Standard MD3E Layout)
            animatedItem(scope) { yOffset ->
                ChunkLayout(
                    modifier = Modifier.offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                    title = "Original Project"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ButtonIconItem(
                            icon = painterResource(R.drawable.img_zalith_launcher), // Restored Zalith Logo
                            title = "Zalith Launcher Team",
                            text = "Original Project Author: Movtery",
                            button = {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(onClick = { openLink("https://github.com/ZalithLauncher/ZalithLauncher2") }) {
                                        Icon(painterResource(R.drawable.ic_link), contentDescription = null)
                                    }
                                }
                            }
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PillButton(
                                modifier = Modifier.weight(1f),
                                text = "Website",
                                onClick = { openLink("https://zalith.movtery.com") },
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                            PillButton(
                                modifier = Modifier.weight(1f),
                                text = "Sponsor",
                                onClick = { openLink(URL_SUPPORT) },
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        }
                    }
                }
            }

            // RESTORED: Acknowledgements Section
            animatedItem(scope) { yOffset ->
                ChunkLayout(
                    modifier = Modifier.offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                    title = stringResource(R.string.about_acknowledgements_title)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ButtonIconItem(
                            icon = painterResource(R.drawable.img_bangbang93),
                            title = "bangbang93",
                            text = stringResource(R.string.about_acknowledgements_bangbang93_text, InfoDistributor.LAUNCHER_SHORT_NAME),
                            button = {
                                Button(onClick = { openLink("https://afdian.com/a/bangbang93") }) {
                                    Text(text = stringResource(R.string.about_sponsor))
                                }
                            }
                        )
                        LinkIconItem(
                            icon = painterResource(R.drawable.img_launcher_fcl),
                            title = "Fold Craft Launcher",
                            text = stringResource(R.string.about_acknowledgements_fcl_text, InfoDistributor.LAUNCHER_SHORT_NAME),
                            openLicense = { openLicense(R.raw.fcl_license) },
                            openLink = { openLink("https://github.com/FCL-Team/FoldCraftLauncher") }
                        )
                        LinkIconItem(
                            icon = painterResource(R.drawable.img_launcher_hmcl),
                            title = "Hello Minecraft! Launcher",
                            text = stringResource(R.string.about_acknowledgements_hmcl_text, InfoDistributor.LAUNCHER_SHORT_NAME),
                            openLicense = { openLicense(R.raw.hmcl_license) },
                            openLink = { openLink("https://github.com/HMCL-dev/HMCL") }
                        )
                        LinkIconItem(
                            icon = painterResource(R.drawable.img_platform_mcmod),
                            title = stringResource(R.string.about_acknowledgements_mcmod),
                            text = stringResource(R.string.about_acknowledgements_mcmod_text, InfoDistributor.LAUNCHER_SHORT_NAME),
                            openLink = { openLink(URL_MCMOD) }
                        )
                        LinkIconItem(
                            icon = painterResource(R.drawable.img_launcher_pcl2),
                            title = "Plain Craft Launcher 2",
                            text = stringResource(R.string.about_acknowledgements_pcl_text, InfoDistributor.LAUNCHER_SHORT_NAME),
                            openLink = { openLink("https://github.com/Meloong-Git/PCL") }
                        )
                        LinkIconItem(
                            icon = painterResource(R.drawable.img_launcher_pojav),
                            title = "PojavLauncher",
                            text = stringResource(R.string.about_acknowledgements_pojav_text, InfoDistributor.LAUNCHER_SHORT_NAME),
                            openLicense = { openLicense(R.raw.lgpl_3_license) },
                            openLink = { openLink("https://github.com/PojavLauncherTeam/PojavLauncher") }
                        )
                        LinkIconItem(
                            icon = painterResource(R.drawable.ic_github),
                            title = stringResource(R.string.about_acknowledgements_github_community),
                            text = stringResource(R.string.about_acknowledgements_github_community_text),
                            openLink = { openLink(URL_COMMUNITY) },
                            useImage = false
                        )
                        LinkIconItem(
                            icon = painterResource(R.drawable.img_weblate),
                            title = stringResource(R.string.about_acknowledgements_weblate_community),
                            text = stringResource(R.string.about_acknowledgements_weblate_community_text),
                            openLink = { openLink(URL_WEBLATE) }
                        )
                    }
                }
            }

            // RESTORED: Library Section
            animatedItem(scope) { yOffset ->
                ChunkLayout(
                    modifier = Modifier.offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                    title = stringResource(R.string.about_library_title)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        libraryData.forEach { info ->
                            LibraryInfoItem(info = info, openLicense = openLicense, openLink = openLink)
                        }
                    }
                }
            }

            // RESTORED: Plugin Section
            PluginLoader.allPlugins.takeIf { it.isNotEmpty() }?.let { allPlugins ->
                animatedItem(scope) { yOffset ->
                    ChunkLayout(
                        modifier = Modifier.offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                        title = stringResource(R.string.about_plugin_title)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            allPlugins.forEach { apkPlugin ->
                                PluginInfoItem(apkPlugin = apkPlugin)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChunkLayout(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit
) {
    SettingsCard(
        modifier = modifier,
        position = CardPosition.Single
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            CardTitleLayout {
                Text(
                    modifier = Modifier.padding(all = 16.dp),
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 12.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun LinkIconItem(
    modifier: Modifier = Modifier,
    icon: Painter,
    title: String,
    text: String,
    openLicense: (() -> Unit)? = null,
    openLink: (() -> Unit)? = null,
    color: Color = itemColor(),
    contentColor: Color = onItemColor(),
    useImage: Boolean = true
) {
    Surface(
        modifier = modifier,
        color = color,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.large,
        onClick = {}
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconModifier = Modifier
                .size(34.dp)
                .clip(shape = RoundedCornerShape(6.dp))
            if (useImage) {
                Image(
                    modifier = iconModifier,
                    painter = icon,
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
            } else {
                Icon(
                    modifier = iconModifier,
                    painter = icon,
                    contentDescription = null
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    modifier = Modifier.alpha(0.7f),
                    text = text,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row {
                openLicense?.let {
                    IconButton(
                        onClick = it
                    ) {
                        Icon(
                            modifier = Modifier.size(22.dp),
                            painter = painterResource(R.drawable.ic_copyright_outlined),
                            contentDescription = "License"
                        )
                    }
                }
                openLink?.let {
                    IconButton(
                        onClick = it
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_link),
                            contentDescription = stringResource(R.string.generic_open_link)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ButtonIconItem(
    modifier: Modifier = Modifier,
    icon: Painter,
    title: String,
    text: String,
    button: @Composable RowScope.() -> Unit,
    color: Color = itemColor(),
    contentColor: Color = onItemColor(),
) {
    Surface(
        modifier = modifier,
        color = color,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.large,
        onClick = {}
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .size(34.dp)
                    .clip(shape = RoundedCornerShape(6.dp)),
                painter = icon,
                contentDescription = null,
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    modifier = Modifier.alpha(0.7f),
                    text = text,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            button()
        }
    }
}

@Composable
private fun LibraryInfoItem(
    info: LibraryInfo,
    modifier: Modifier = Modifier,
    color: Color = itemColor(),
    contentColor: Color = onItemColor(),
    openLicense: (Int) -> Unit,
    openLink: (url: String) -> Unit
) {
    Surface(
        modifier = modifier,
        color = color,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.large,
        onClick = {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = info.name,
                    style = MaterialTheme.typography.titleSmall
                )
                Column(
                    modifier = Modifier.alpha(0.7f)
                ) {
                    info.copyrightInfo?.let { copyrightInfo ->
                        Text(
                            text = copyrightInfo,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Text(
                        modifier = Modifier.clickable(
                            onClick = {
                                openLicense(info.license.raw)
                            }
                        ),
                        text = "Licensed under the ${info.license.name}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }
            }
            IconButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = {
                    openLink(info.webUrl)
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_link),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun PluginInfoItem(
    apkPlugin: ApkPlugin,
    modifier: Modifier = Modifier,
    color: Color = itemColor(),
    contentColor: Color = onItemColor(),
) {
    Surface(
        modifier = modifier,
        color = color,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.large,
        onClick = {}
    ) {
        val context = LocalContext.current
        Row(
            modifier = Modifier
                .padding(all = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val iconFile = appCacheIcon(apkPlugin.packageName)
            if (iconFile.exists()) {
                val model = remember(context, iconFile) {
                    ImageRequest.Builder(context)
                        .data(iconFile)
                        .build()
                }
                AsyncImage(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(shape = RoundedCornerShape(8.dp)),
                    model = model,
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
            } else {
                Image(
                    modifier = Modifier.size(34.dp),
                    painter = painterResource(R.drawable.ic_unknown_icon),
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(
                    text = apkPlugin.appName,
                    style = MaterialTheme.typography.titleSmall
                )
                Row(
                    modifier = Modifier.alpha(0.7f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = apkPlugin.packageName,
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (apkPlugin.appVersion.isNotEmpty()) {
                        Text(
                            text = apkPlugin.appVersion,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    Button(
        modifier = modifier.height(40.dp),
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}
