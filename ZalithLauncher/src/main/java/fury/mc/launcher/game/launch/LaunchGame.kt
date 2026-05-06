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

package fury.mc.launcher.game.launch

import android.content.Context
import fury.mc.launcher.R
import fury.mc.launcher.coroutine.Task
import fury.mc.launcher.coroutine.TaskSystem
import fury.mc.launcher.game.account.Account
import fury.mc.launcher.game.account.AccountsManager
import fury.mc.launcher.game.account.auth_server.ResponseException
import fury.mc.launcher.game.account.isLocalAccount
import fury.mc.launcher.game.account.microsoft.MinecraftProfileException
import fury.mc.launcher.game.account.microsoft.NotPurchasedMinecraftException
import fury.mc.launcher.game.account.microsoft.XboxLoginException
import fury.mc.launcher.game.account.microsoft.toLocal
import fury.mc.launcher.game.version.download.DownloadMode
import fury.mc.launcher.game.version.download.MinecraftDownloader
import fury.mc.launcher.game.version.installed.Version
import fury.mc.launcher.game.version.installed.VersionFolders
import fury.mc.launcher.game.version.mod.AllModReader
import fury.mc.launcher.ui.activities.runGame
import fury.mc.launcher.utils.logging.Logger.lError
import fury.mc.launcher.utils.network.isNetworkAvailable
import fury.mc.launcher.viewmodel.ErrorViewModel
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.HttpStatusCode
import java.net.ConnectException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException

object LaunchGame {
    var isLaunching: Boolean = false
        private set

    fun launchGame(
        context: Context,
        version: Version,
        exitActivity: () -> Unit,
        submitError: (ErrorViewModel.ThrowableMessage) -> Unit
    ) {
        if (isLaunching) return
        val account = AccountsManager.currentAccountFlow.value ?: return
        isLaunching = true

        //检查是否联网，根据这个条件决定是否登录账号
        //以及，没有联网时，让微软账号、外置账号作为离线账号登录
        val hasNetwork = isNetworkAvailable(context)

        val downloadTask = createDownloadTask(
            context = context,
            version = version,
            exitActivity = exitActivity,
            submitError = submitError
        )
        fun startDownloadTask() {
            TaskSystem.submitTask(downloadTask) { isLaunching = false }
        }

        val loginTask = createLoginTask(
            context = context,
            hasNetwork = hasNetwork,
            account = account,
            submitError = submitError
        ) {
            startDownloadTask()
        }

        if (loginTask != null) {
            TaskSystem.submitTask(loginTask)
        } else {
            if (!hasNetwork && !account.isLocalAccount()) {
                //没联网时作为离线账号登录
                version.offlineAccountLogin = true
            }
            startDownloadTask()
        }
    }

    private fun createDownloadTask(
        context: Context,
        version: Version,
        exitActivity: () -> Unit,
        submitError: (ErrorViewModel.ThrowableMessage) -> Unit
    ): Task {
        return MinecraftDownloader(
            context = context,
            version = version.getVersionInfo()?.minecraftVersion ?: version.getVersionName(),
            customName = version.getVersionName(),
            verifyIntegrity = !version.skipGameIntegrityCheck(),
            mode = DownloadMode.VERIFY_AND_REPAIR,
            onCompletion = {
                checkEnableTouchProxy(version)
                runGame(context, version)
                exitActivity()
            },
            onError = { message ->
                submitError(
                    ErrorViewModel.ThrowableMessage(
                        title = context.getString(R.string.minecraft_download_failed),
                        message = message
                    )
                )
            }
        ).getDownloadTask()
    }

    /**
     * 检查是否安装了 TouchController，安装后开启控制代理
     */
    private suspend fun checkEnableTouchProxy(version: Version) {
        val modsDir = VersionFolders.MOD.getDir(version.getGameDir())
        val reader = AllModReader(modsDir)
        for (mod in reader.readAllLocals()) {
            if (mod.id == "touchcontroller") {
                version.enableTouchProxy = true
                break
            }
        }
    }

    private fun createLoginTask(
        context: Context,
        hasNetwork: Boolean,
        account: Account,
        submitError: (ErrorViewModel.ThrowableMessage) -> Unit,
        onFinally: () -> Unit
    ): Task? {
        val needsRefresh = hasNetwork && System.currentTimeMillis() > account.expiresAt - 5 * 60 * 1000

        return if (needsRefresh) {
            AccountsManager.performLoginTask(
                context = context,
                account = account,
                onSuccess = { acc, _ ->
                    AccountsManager.suspendSaveAccount(acc)
                },
                onFailed = { error ->
                    val message: String = when (error) {
                        is NotPurchasedMinecraftException -> toLocal(context)
                        is MinecraftProfileException -> error.toLocal(context)
                        is XboxLoginException -> error.toLocal(context)
                        is ResponseException -> error.responseMessage
                        is HttpRequestTimeoutException -> context.getString(R.string.error_timeout)
                        is UnknownHostException, is UnresolvedAddressException -> context.getString(R.string.error_network_unreachable)
                        is ConnectException -> context.getString(R.string.error_connection_failed)
                        is io.ktor.client.plugins.ResponseException -> {
                            val statusCode = error.response.status
                            val res = when (statusCode) {
                                HttpStatusCode.Unauthorized -> R.string.error_unauthorized
                                HttpStatusCode.NotFound -> R.string.error_notfound
                                else -> R.string.error_client_error
                            }
                            context.getString(res, statusCode)
                        }
                        else -> {
                            lError("An unknown exception was caught!", error)
                            val errorMessage = error.localizedMessage ?: error.message ?: error::class.qualifiedName ?: "Unknown error"
                            context.getString(R.string.error_unknown, errorMessage)
                        }
                    }

                    submitError(
                        ErrorViewModel.ThrowableMessage(
                            title = context.getString(R.string.account_logging_in_failed),
                            message = message
                        )
                    )
                },
                onFinally = onFinally
            )
        } else {
            null
        }
    }
}
