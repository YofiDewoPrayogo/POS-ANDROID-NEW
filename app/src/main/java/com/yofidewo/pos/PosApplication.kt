package com.yofidewo.pos

import android.app.Application
import com.yofidewo.pos.data.PosDatabase
import com.yofidewo.pos.data.PosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class PosApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { PosDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { PosRepository(database, this) }
}
