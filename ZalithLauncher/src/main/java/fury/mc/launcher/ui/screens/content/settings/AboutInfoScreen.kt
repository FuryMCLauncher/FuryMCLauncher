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
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
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
import fury.mc.launcher.path.URL_MCMOD
import fury.mc.launcher.path.URL_PROJECT
import fury.mc.launcher.path.URL_SUPPORT
import fury.mc.launcher.ui.base.BaseScreen
import fury.mc.launcher.ui.components.AnimatedLazyColumn
import fury.mc.launcher.ui.screens.NestedNavKey
import fury.mc.launcher.ui.screens.NormalNavKey
import fury.mc.launcher.ui.screens.TitledNavKey
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
            contentPadding = PaddingValues(16.dp)
        ) { scope ->
            
            // CARD 1: Main App Info & Actions
            animatedItem(scope) { yOffset ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(0, yOffset.roundToPx()) },
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        // Top Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Logo (Red Square)
                            Surface(
                                modifier = Modifier.size(80.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFE53935) // Red
                            ) {
                                Image(
                                    modifier = Modifier.padding(12.dp),
                                    painter = painterResource(R.drawable.img_launcher),
                                    contentDescription = null
                                )
                            }

                            // Action Column
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

                        // Middle (Description)
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        ) {
                            Text(
                                modifier = Modifier.padding(16.dp),
                                text = "FuryMC Launcher is based on Zalith Launcher 2, providing a high-performance experience with full offline support and regional independence. Built with the community in mind.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Bottom (Split Pill)
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
                                        .padding(start = 20.dp),
                                    text = "Made by UnTamed-Fury",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Button(
                                    onClick = { openLink(URL_SUPPORT) },
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    contentPadding = PaddingValues(horizontal = 24.dp)
                                ) {
                                    Text("Support the project")
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // CARD 2: Credits & Original Project
            animatedItem(scope) { yOffset ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(0, yOffset.roundToPx()) },
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        // Top Area (Grey/Variant rounded)
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.size(50.dp),
                                    shape = CircleShape,
                                    color = Color(0xFF4CAF50) // Green
                                ) {
                                    Image(
                                        modifier = Modifier.padding(8.dp),
                                        painter = painterResource(R.drawable.img_launcher), // Fallback to launcher icon
                                        contentDescription = null
                                    )
                                }
                                Text(
                                    text = "The original Project created by Zalith launcher team. Author : Movtery",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Bottom Area (3 Buttons)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PillButton(
                                modifier = Modifier.weight(1f),
                                text = "Website",
                                onClick = { openLink("https://github.com/ZalithLauncher/ZalithLauncher2") },
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                            PillButton(
                                modifier = Modifier.weight(1f),
                                text = "Repository",
                                onClick = { openLink("https://github.com/ZalithLauncher/ZalithLauncher2") },
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

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // CARD 3: Projects Used
            animatedItem(scope) { yOffset ->
                InformationCard(
                    modifier = Modifier.offset { IntOffset(0, yOffset.roundToPx()) },
                    title = "THE PROJECTS THAT BEEN USED TO CREATE THIS APP.",
                    content = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Example items
                            ProjectItem("PojavLauncher", "Core engine", openLink = { openLink("https://github.com/PojavLauncherTeam/PojavLauncher") })
                            ProjectItem("HMCL", "Architecture inspiration", openLink = { openLink("https://github.com/HMCL-dev/HMCL") })
                            ProjectItem("PCL2", "Implementation reference", openLink = { openLink("https://github.com/Meloong-Git/PCL") })
                        }
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // CARD 4: Libraries Used
            animatedItem(scope) { yOffset ->
                InformationCard(
                    modifier = Modifier.offset { IntOffset(0, yOffset.roundToPx()) },
                    title = "THE LIBRARIES USED TO CREATE THIS PROJECT.",
                    content = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            libraryData.take(5).forEach { info ->
                                LibraryMiniItem(info = info, openLink = openLink)
                            }
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .alpha(0.5f),
                                text = "And many more open-source libraries...",
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                )
            }

            // CARD 5: Renderers (if any)
            PluginLoader.allPlugins.takeIf { it.isNotEmpty() }?.let { allPlugins ->
                item { Spacer(modifier = Modifier.height(16.dp)) }
                animatedItem(scope) { yOffset ->
                    InformationCard(
                        modifier = Modifier.offset { IntOffset(0, yOffset.roundToPx()) },
                        title = "The extra renderers that shows that u have installed.",
                        content = {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                allPlugins.forEach { apkPlugin ->
                                    PluginMiniItem(apkPlugin = apkPlugin)
                                }
                            }
                        }
                    )
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

@Composable
private fun InformationCard(
    title: String,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

@Composable
private fun ProjectItem(name: String, desc: String, openLink: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = openLink)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(painterResource(R.drawable.ic_link), contentDescription = null, modifier = Modifier.size(18.dp))
        Column {
            Text(name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(desc, style = MaterialTheme.typography.labelSmall, modifier = Modifier.alpha(0.7f))
        }
    }
}

@Composable
private fun LibraryMiniItem(info: LibraryInfo, openLink: (url: String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { openLink(info.webUrl) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(info.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        Icon(painterResource(R.drawable.ic_link), contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun PluginMiniItem(apkPlugin: ApkPlugin) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(modifier = Modifier.size(24.dp), shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
            val context = LocalContext.current
            val iconFile = appCacheIcon(apkPlugin.packageName)
            if (iconFile.exists()) {
                val model = remember(context, iconFile) {
                    ImageRequest.Builder(context).data(iconFile).build()
                }
                AsyncImage(model = model, contentDescription = null)
            } else {
                Icon(painterResource(R.drawable.ic_unknown_icon), contentDescription = null, modifier = Modifier.padding(4.dp))
            }
        }
        Text(apkPlugin.appName, style = MaterialTheme.typography.bodySmall)
    }
}
