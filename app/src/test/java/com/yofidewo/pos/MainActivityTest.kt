package com.yofidewo.pos

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = PosApplication::class)
class MainActivityTest {
    @Test
    fun testActivityCrash() {
        try {
            Robolectric.buildActivity(MainActivity::class.java).setup()
            println("No crash on setup!")
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
