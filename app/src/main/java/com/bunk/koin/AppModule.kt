package com.bunk.koin

import android.content.ContentResolver
import android.content.Context
import android.net.wifi.WifiManager
import com.bunk.call.storage.*
import com.bunk.permission.PermissionHelper
import com.bunk.server.KtorServer
import com.bunk.server.Server
import com.bunk.server.service.GetCallStatusUseCase
import com.bunk.server.service.ServiceFactory
import com.bunk.ui.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

fun appModule() = module {

    factory {
        ServiceFactory(
            callLogStorage = get(),
            getCallStatusStatusUseCase = get()
        )
    }

    single {
        KtorServer(
            wifiManager = get(),
            serviceFactory = get()
        )
    } bind Server::class

    single {
        androidContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    single { OngoingCallStorageInMemory() } bind OngoingCallStorage::class

    factory { PermissionHelper(applicationContext = androidContext().applicationContext) }

    viewModel { MainViewModel(server = get(), callLogStorage = get()) }

    single { NumberOfCallStorageSharedPreferences(sharedPreferencesProvider = get()) } bind NumberOfCallStorage::class

    single<ContentResolver> { androidContext().contentResolver }

    single {
        CallLogStorageContentResolver(
            contentResolver = get(),
            permissionHelper = get(),
            numberOfCallStorage = get()
        )
    } bind CallLogStorage::class

    factory { SharedPreferencesProvider(applicationContext = androidContext().applicationContext) }

    factory {
        GetCallStatusUseCase(
            ongoingCallStorage = get(),
            numberOfCallStorage = get(),
            callLogStorage = get()
        )
    }
}