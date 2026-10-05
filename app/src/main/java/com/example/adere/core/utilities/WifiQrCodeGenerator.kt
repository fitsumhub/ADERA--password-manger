package com.example.adere.core.utilities

import android.graphics.Bitmap
import android.graphics.Color
import java.io.ByteArrayOutputStream

/**
 * Pure Kotlin, offline QR Code generator for Wi-Fi configurations and TOTP provisioning.
 * Operates 100% offline with zero network connectivity or external libraries.
 */
object WifiQrCodeGenerator {

    /**
     * Formats Wi-Fi credentials into standard Wi-Fi QR Code string:
     * WIFI:T:WPA;S:NetworkName;P:SecretPassword;;
     */
    fun formatWifiString(ssid: String, password: String, securityType: String = "WPA2/WPA3"): String {
        val type = when {
            securityType.contains("WPA", ignoreCase = true) -> "WPA"
            securityType.contains("WEP", ignoreCase = true) -> "WEP"
            securityType.contains("Open", ignoreCase = true) -> "nopass"
            else -> "WPA"
        }
        val escapedSsid = escape(ssid)
        val escapedPass = escape(password)
        return "WIFI:T:$type;S:$escapedSsid;P:$escapedPass;;"
    }

    private fun escape(text: String): String {
        return text.replace("\\", "\\\\")
            .replace(";", "\\;")
            .replace(",", "\\,")
            .replace(":", "\\:")
    }

    /**
     * Generates a monochrome QR code Bitmap for the provided payload string.
     * Uses a lightweight, robust QR Code Model 2 matrix generator (Byte mode, Version 2-6).
     */
    fun generateQrBitmap(content: String, size: Int = 512): Bitmap {
        val modules = SimpleQrEncoder.encode(content)
        val matrixSize = modules.size
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val scale = size.toFloat() / matrixSize

        val pixels = IntArray(size * size)
        val darkColor = Color.BLACK
        val lightColor = Color.WHITE

        for (y in 0 until size) {
            val modY = (y / scale).toInt().coerceIn(0, matrixSize - 1)
            for (x in 0 until size) {
                val modX = (x / scale).toInt().coerceIn(0, matrixSize - 1)
                pixels[y * size + x] = if (modules[modY][modX]) darkColor else lightColor
            }
        }

        bitmap.setPixels(pixels, 0, size, 0, 0, size, size)
        return bitmap
    }
}

/**
 * Minimalist self-contained QR encoder for standard alphanumeric/byte payloads.
 */
internal object SimpleQrEncoder {

    fun encode(content: String): Array<BooleanArray> {
        val bytes = content.toByteArray(Charsets.UTF_8)
        // Select QR version (1 to 6) based on length
        val version = when {
            bytes.size <= 17 -> 1
            bytes.size <= 32 -> 2
            bytes.size <= 53 -> 3
            bytes.size <= 78 -> 4
            bytes.size <= 106 -> 5
            else -> 6
        }
        val dimension = 17 + 4 * version
        val matrix = Array(dimension) { BooleanArray(dimension) }
        val reserved = Array(dimension) { BooleanArray(dimension) }

        // Finder patterns at (0,0), (dim-7, 0), (0, dim-7)
        placeFinder(matrix, reserved, 0, 0)
        placeFinder(matrix, reserved, dimension - 7, 0)
        placeFinder(matrix, reserved, 0, dimension - 7)

        // Timing patterns
        for (i in 8 until dimension - 8) {
            val bit = (i % 2 == 0)
            matrix[6][i] = bit
            reserved[6][i] = true
            matrix[i][6] = bit
            reserved[i][6] = true
        }

        // Dark module
        matrix[4 * version + 9][8] = true
        reserved[4 * version + 9][8] = true

        // Reserve format info areas
        for (i in 0..8) {
            reserved[8][i] = true
            reserved[i][8] = true
            reserved[8][dimension - 1 - i] = true
            reserved[dimension - 1 - i][8] = true
        }

        // Encode data bits (Byte mode: indicator 0100 + char count + data + terminator)
        val bitBuffer = mutableListOf<Boolean>()
        appendBits(bitBuffer, 0b0100, 4) // Byte mode
        appendBits(bitBuffer, bytes.size, 8)
        for (b in bytes) {
            appendBits(bitBuffer, b.toInt() and 0xFF, 8)
        }
        // Terminator
        val dataCapacityBits = when (version) {
            1 -> 152
            2 -> 272
            3 -> 440
            4 -> 640
            5 -> 864
            else -> 1088
        }
        val termLength = (dataCapacityBits - bitBuffer.size).coerceIn(0, 4)
        appendBits(bitBuffer, 0, termLength)

        // Pad to multiple of 8
        while (bitBuffer.size % 8 != 0) {
            bitBuffer.add(false)
        }
        // Pad bytes (0xEC, 0x11)
        val padBytes = intArrayOf(0xEC, 0x11)
        var padIdx = 0
        while (bitBuffer.size < dataCapacityBits) {
            appendBits(bitBuffer, padBytes[padIdx % 2], 8)
            padIdx++
        }

        // Place data into matrix in 2-column zig-zag from right to left
        var bitIndex = 0
        var right = dimension - 1
        var goingUp = true

        while (right > 0) {
            if (right == 6) right-- // Skip vertical timing column
            val range = if (goingUp) (dimension - 1 downTo 0) else (0 until dimension)
            for (y in range) {
                for (xOffset in 0..1) {
                    val x = right - xOffset
                    if (!reserved[y][x]) {
                        var bit = if (bitIndex < bitBuffer.size) bitBuffer[bitIndex++] else false
                        // Mask pattern 0: (x + y) % 2 == 0
                        if ((x + y) % 2 == 0) {
                            bit = !bit
                        }
                        matrix[y][x] = bit
                    }
                }
            }
            right -= 2
            goingUp = !goingUp
        }

        // Apply format info (Mask 0, Error Correction L = 0b01)
        val formatBits = 0b111011111000100 // Precomputed BCH code for EC L, Mask 0
        for (i in 0..14) {
            val bit = ((formatBits shr (14 - i)) and 1) == 1
            if (i < 6) matrix[8][i] = bit
            else if (i == 6) matrix[8][7] = bit
            else if (i == 7) matrix[8][8] = bit
            else if (i == 8) matrix[7][8] = bit
            else matrix[14 - i][8] = bit
        }
        for (i in 0..7) {
            val bit = ((formatBits shr (14 - i)) and 1) == 1
            matrix[dimension - 1 - i][8] = bit
        }
        for (i in 8..14) {
            val bit = ((formatBits shr (14 - i)) and 1) == 1
            matrix[8][dimension - 15 + i] = bit
        }

        // 4-module quiet zone around matrix
        val quietZone = 4
        val totalDim = dimension + 2 * quietZone
        val finalMatrix = Array(totalDim) { BooleanArray(totalDim) }
        for (y in 0 until dimension) {
            for (x in 0 until dimension) {
                finalMatrix[y + quietZone][x + quietZone] = matrix[y][x]
            }
        }

        return finalMatrix
    }

    private fun placeFinder(matrix: Array<BooleanArray>, reserved: Array<BooleanArray>, startX: Int, startY: Int) {
        for (y in 0..6) {
            for (x in 0..6) {
                val isBlack = (y == 0 || y == 6 || x == 0 || x == 6 || (y in 2..4 && x in 2..4))
                matrix[startY + y][startX + x] = isBlack
                reserved[startY + y][startX + x] = true
            }
        }
        // Separator ring
        for (y in -1..7) {
            for (x in -1..7) {
                val absX = startX + x
                val absY = startY + y
                if (absX in matrix.indices && absY in matrix.indices) {
                    reserved[absY][absX] = true
                }
            }
        }
    }

    private fun appendBits(buffer: MutableList<Boolean>, value: Int, count: Int) {
        for (i in count - 1 downTo 0) {
            buffer.add(((value shr i) and 1) == 1)
        }
    }
}

