package com.bunk.ui

import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module

class KoinTestRule(
    private val modules: List<Module> = emptyList()
) : TestWatcher() {

    constructor(module: Module) : this(listOf(module))

    override fun starting(description: Description?) {
        super.starting(description)
        stopKoin()
        startKoin {
            // no implementation
        }

        loadKoinModules(modules)
    }

    override fun finished(description: Description?) {
        super.finished(description)

        unloadKoinModules(modules)

        stopKoin()
    }
}