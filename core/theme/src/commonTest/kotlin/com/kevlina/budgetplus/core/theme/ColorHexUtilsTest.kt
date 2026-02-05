package com.kevlina.budgetplus.core.theme

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorHexUtilsTest {

    @Test
    fun convertHexToColorInt_works() {
        assertEquals(0xFFFF0000.toInt(), "FF0000".convertHexToColorInt())
        assertEquals(0xFFFF0000.toInt(), "#FF0000".convertHexToColorInt())
        assertEquals(0x7FFF0000, "7FFF0000".convertHexToColorInt())
        assertEquals(0x7FFF0000, "#7FFF0000".convertHexToColorInt())
    }

    @Test
    fun toHexCode_works() {
        assertEquals("FF0000", Color.Red.toHexCode())
        assertEquals("00FF00", Color.Green.toHexCode())
        assertEquals("0000FF", Color.Blue.toHexCode())
        assertEquals("FFFFFF", Color.White.toHexCode())
        assertEquals("000000", Color.Black.toHexCode())
    }
}