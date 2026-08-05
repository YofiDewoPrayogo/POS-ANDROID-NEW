package com.yofidewo.pos.util

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import com.yofidewo.pos.data.TransactionEntity
import com.yofidewo.pos.data.TransactionItemEntity
import java.io.OutputStream
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object EscPosPrinterHelper {

    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    // ESC/POS Command Constants
    private val ESC_INIT = byteArrayOf(0x1B, 0x40)
    private val ESC_ALIGN_LEFT = byteArrayOf(0x1B, 0x61, 0x00)
    private val ESC_ALIGN_CENTER = byteArrayOf(0x1B, 0x61, 0x01)
    private val ESC_ALIGN_RIGHT = byteArrayOf(0x1B, 0x61, 0x02)
    private val ESC_BOLD_ON = byteArrayOf(0x1B, 0x45, 0x01)
    private val ESC_BOLD_OFF = byteArrayOf(0x1B, 0x45, 0x00)
    private val ESC_DOUBLE_HEIGHT_ON = byteArrayOf(0x1B, 0x21, 0x10)
    private val ESC_DOUBLE_HEIGHT_OFF = byteArrayOf(0x1B, 0x21, 0x00)
    private val ESC_KICK_DRAWER = byteArrayOf(0x1B, 0x70, 0x00, 0x19, 0xFA.toByte())

    // Multi-brand ESC/POS Auto-Cutter Commands (GS V 66 0 + GS V 0 + ESC i + ESC m)
    private val ESC_FEED_PAPER = "\n\n\n\n\n".toByteArray()
    private val CUT_GS_V_66 = byteArrayOf(0x1D, 0x56, 0x42, 0x00) // GS V 66 0
    private val CUT_GS_V_0  = byteArrayOf(0x1D, 0x56, 0x00)       // GS V 0
    private val CUT_ESC_i   = byteArrayOf(0x1B, 0x69)             // ESC i
    private val CUT_ESC_m   = byteArrayOf(0x1B, 0x6D)             // ESC m

    private fun writeAutoCutter(outputStream: OutputStream) {
        outputStream.write(ESC_FEED_PAPER)
        outputStream.write(CUT_GS_V_66)
        outputStream.write(CUT_GS_V_0)
        outputStream.write(CUT_ESC_i)
        outputStream.write(CUT_ESC_m)
    }

    data class BluetoothDeviceInfo(val name: String, val address: String)

    @SuppressLint("MissingPermission")
    fun getPairedDevices(): List<BluetoothDeviceInfo> {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter() ?: return emptyList()
        if (!bluetoothAdapter.isEnabled) return emptyList()

        val paired = bluetoothAdapter.bondedDevices ?: return emptyList()
        return paired.map { device ->
            BluetoothDeviceInfo(
                name = device.name ?: "Unknown Device",
                address = device.address
            )
        }
    }

    /**
     * Generates a plain text representation of the receipt suitable for 58mm (32 chars) or 80mm (48 chars)
     */
    fun buildTextReceipt(
        transaction: TransactionEntity,
        items: List<TransactionItemEntity>,
        storeName: String = "WARUNGKU POS",
        storeAddress: String = "Jl. Merdeka No. 123",
        paperWidthMm: Int = 58,
        formatMoney: (Double) -> String
    ): String {
        val maxCols = if (paperWidthMm == 80) 48 else 32
        val lineSeparator = "-".repeat(maxCols)
        val doubleLine = "=".repeat(maxCols)

        val sb = StringBuilder()

        fun centerText(text: String): String {
            if (text.length >= maxCols) return text.take(maxCols)
            val padding = (maxCols - text.length) / 2
            return " ".repeat(padding) + text
        }

        fun justifyRow(left: String, right: String): String {
            val spaceNeeded = maxCols - left.length - right.length
            return if (spaceNeeded > 0) {
                left + " ".repeat(spaceNeeded) + right
            } else {
                left.take(maxCols - right.length - 1) + " " + right
            }
        }

        // Header
        sb.appendLine(centerText(storeName))
        if (storeAddress.isNotBlank()) sb.appendLine(centerText(storeAddress))
        sb.appendLine(lineSeparator)

        // Meta Info
        val df = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        sb.appendLine(justifyRow("Faktur:", transaction.invoiceNumber))
        sb.appendLine(justifyRow("Tanggal:", df.format(Date(transaction.timestamp))))
        sb.appendLine(justifyRow("Kasir:", transaction.cashierName))
        sb.appendLine(justifyRow("Pelanggan:", transaction.customerName))
        sb.appendLine(lineSeparator)

        // Items
        items.forEach { item ->
            sb.appendLine(item.productName)
            val qtyPriceStr = "${item.quantity} x ${formatMoney(item.price)}"
            val subtotalStr = formatMoney(item.subtotal)
            sb.appendLine(justifyRow("  $qtyPriceStr", subtotalStr))
        }

        sb.appendLine(lineSeparator)

        // Financials
        sb.appendLine(justifyRow("Subtotal:", formatMoney(transaction.subTotalAmount)))
        if (transaction.discountAmount > 0) {
            sb.appendLine(justifyRow("Diskon:", "-${formatMoney(transaction.discountAmount)}"))
        }
        sb.appendLine(doubleLine)
        sb.appendLine(justifyRow("TOTAL:", formatMoney(transaction.totalAmount)))
        sb.appendLine(justifyRow("Bayar (${transaction.paymentMethod}):", formatMoney(transaction.paidAmount)))
        sb.appendLine(justifyRow("Kembali:", formatMoney(transaction.changeAmount)))
        sb.appendLine(lineSeparator)

        // Footer
        sb.appendLine(centerText("Terima Kasih atas Kunjungan Anda"))
        sb.appendLine(centerText("Barang yang sudah dibeli"))
        sb.appendLine(centerText("tidak dapat ditukar/dikembalikan"))
        sb.appendLine("\n\n")

        return sb.toString()
    }

    /**
     * Connects to a Bluetooth ESC/POS printer device and streams byte commands
     */
    @SuppressLint("MissingPermission")
    fun printViaBluetooth(
        deviceAddress: String,
        transaction: TransactionEntity,
        items: List<TransactionItemEntity>,
        storeName: String = "WARUNGKU POS",
        storeAddress: String = "Jl. Merdeka No. 123",
        paperWidthMm: Int = 58,
        formatMoney: (Double) -> String
    ): Result<Boolean> {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            ?: return Result.failure(Exception("Bluetooth tidak didukung pada perangkat ini"))

        if (!bluetoothAdapter.isEnabled) {
            return Result.failure(Exception("Bluetooth belum diaktifkan"))
        }

        var socket: BluetoothSocket? = null
        var outputStream: OutputStream? = null

        return try {
            val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)
            bluetoothAdapter.cancelDiscovery()
            
            socket = try {
                device.createRfcommSocketToServiceRecord(SPP_UUID)
            } catch (e: Exception) {
                try {
                    device.createInsecureRfcommSocketToServiceRecord(SPP_UUID)
                } catch (e2: Exception) {
                    val m = device.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
                    m.invoke(device, 1) as BluetoothSocket
                }
            }

            try {
                socket.connect()
            } catch (e: Exception) {
                // Fallback attempt with reflection port 1 if standard SPP fails
                try {
                    socket.close()
                } catch (_: Exception) {}
                val m = device.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
                socket = m.invoke(device, 1) as BluetoothSocket
                socket.connect()
            }

            outputStream = socket.outputStream

            val maxCols = if (paperWidthMm == 80) 48 else 32
            val lineSeparator = "-".repeat(maxCols) + "\n"
            val doubleLine = "=".repeat(maxCols) + "\n"

            fun writeBytes(bytes: ByteArray) {
                outputStream.write(bytes)
            }

            fun writeText(text: String) {
                outputStream.write(text.toByteArray(charset("GBK")))
            }

            fun writeJustified(left: String, right: String) {
                val spaceNeeded = maxCols - left.length - right.length
                val line = if (spaceNeeded > 0) {
                    left + " ".repeat(spaceNeeded) + right + "\n"
                } else {
                    left.take(maxCols - right.length - 1) + " " + right + "\n"
                }
                writeText(line)
            }

            // 1. Initialize
            writeBytes(ESC_INIT)

            // 2. Open Cash Drawer
            writeBytes(ESC_KICK_DRAWER)

            // 3. Store Header (Centered & Bold)
            writeBytes(ESC_ALIGN_CENTER)
            writeBytes(ESC_BOLD_ON)
            writeBytes(ESC_DOUBLE_HEIGHT_ON)
            writeText("$storeName\n")
            writeBytes(ESC_DOUBLE_HEIGHT_OFF)
            writeBytes(ESC_BOLD_OFF)

            if (storeAddress.isNotBlank()) {
                writeText("$storeAddress\n")
            }
            writeBytes(ESC_ALIGN_LEFT)
            writeText(lineSeparator)

            // 4. Metadata
            val df = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            writeJustified("Faktur:", transaction.invoiceNumber)
            writeJustified("Tanggal:", df.format(Date(transaction.timestamp)))
            writeJustified("Kasir:", transaction.cashierName)
            writeJustified("Pelanggan:", transaction.customerName)
            writeText(lineSeparator)

            // 5. Items List
            items.forEach { item ->
                writeText("${item.productName}\n")
                val qtyPriceStr = "  ${item.quantity} x ${formatMoney(item.price)}"
                val subtotalStr = formatMoney(item.subtotal)
                writeJustified(qtyPriceStr, subtotalStr)
            }
            writeText(lineSeparator)

            // 6. Totals
            writeJustified("Subtotal:", formatMoney(transaction.subTotalAmount))
            if (transaction.discountAmount > 0) {
                writeJustified("Diskon:", "-${formatMoney(transaction.discountAmount)}")
            }
            writeText(doubleLine)

            writeBytes(ESC_BOLD_ON)
            writeJustified("TOTAL:", formatMoney(transaction.totalAmount))
            writeBytes(ESC_BOLD_OFF)

            writeJustified("Bayar (${transaction.paymentMethod}):", formatMoney(transaction.paidAmount))
            writeJustified("Kembali:", formatMoney(transaction.changeAmount))
            writeText(lineSeparator)

            // 7. Footer
            writeBytes(ESC_ALIGN_CENTER)
            writeText("Terima Kasih atas Kunjungan Anda!\n")
            writeText("Barang yang sudah dibeli\n")
            writeText("tidak dapat ditukar/dikembalikan.\n")

            // 8. Auto-Cutter (Feed paper & cut)
            writeAutoCutter(outputStream)

            outputStream.flush()
            // Pause 800ms for Bluetooth hardware buffer to finish transmitting all bytes & cutter commands
            try { Thread.sleep(800) } catch (_: Exception) {}
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            try {
                outputStream?.close()
                socket?.close()
            } catch (_: Exception) {}
        }
    }

    /**
     * Cetak struk via koneksi LAN/Network menggunakan Socket TCP port 9100
     */
    fun printViaNetwork(
        ip: String,
        port: Int = 9100,
        transaction: TransactionEntity,
        items: List<TransactionItemEntity>,
        storeName: String = "WARUNGKU POS",
        storeAddress: String = "Jl. Merdeka No. 123",
        paperWidthMm: Int = 58,
        formatMoney: (Double) -> String
    ): Result<Boolean> {
        return try {
            val socket = Socket(ip, port)
            socket.soTimeout = 5000
            val outputStream = socket.getOutputStream()

            val maxCols = if (paperWidthMm == 80) 48 else 32
            val lineSeparator = "-".repeat(maxCols) + "\n"
            val doubleLine = "=".repeat(maxCols) + "\n"

            fun writeBytes(bytes: ByteArray) = outputStream.write(bytes)
            fun writeText(text: String) = outputStream.write(text.toByteArray(Charsets.UTF_8))
            fun writeJustified(left: String, right: String) {
                val spaceNeeded = maxCols - left.length - right.length
                val line = if (spaceNeeded > 0) {
                    left + " ".repeat(spaceNeeded) + right + "\n"
                } else {
                    left.take(maxCols - right.length - 1) + " " + right + "\n"
                }
                writeText(line)
            }

            writeBytes(ESC_INIT)
            writeBytes(ESC_KICK_DRAWER)
            writeBytes(ESC_ALIGN_CENTER)
            writeBytes(ESC_BOLD_ON)
            writeBytes(ESC_DOUBLE_HEIGHT_ON)
            writeText("$storeName\n")
            writeBytes(ESC_DOUBLE_HEIGHT_OFF)
            writeBytes(ESC_BOLD_OFF)
            if (storeAddress.isNotBlank()) writeText("$storeAddress\n")
            writeBytes(ESC_ALIGN_LEFT)
            writeText(lineSeparator)

            val df = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            writeJustified("Faktur:", transaction.invoiceNumber)
            writeJustified("Tanggal:", df.format(Date(transaction.timestamp)))
            writeJustified("Kasir:", transaction.cashierName)
            writeJustified("Pelanggan:", transaction.customerName)
            writeText(lineSeparator)

            items.forEach { item ->
                writeText("${item.productName}\n")
                writeJustified("  ${item.quantity} x ${formatMoney(item.price)}", formatMoney(item.subtotal))
            }
            writeText(lineSeparator)

            writeJustified("Subtotal:", formatMoney(transaction.subTotalAmount))
            if (transaction.discountAmount > 0) {
                writeJustified("Diskon:", "-${formatMoney(transaction.discountAmount)}")
            }
            writeText(doubleLine)
            writeBytes(ESC_BOLD_ON)
            writeJustified("TOTAL:", formatMoney(transaction.totalAmount))
            writeBytes(ESC_BOLD_OFF)
            writeJustified("Bayar (${transaction.paymentMethod}):", formatMoney(transaction.paidAmount))
            writeJustified("Kembali:", formatMoney(transaction.changeAmount))
            writeText(lineSeparator)
            writeBytes(ESC_ALIGN_CENTER)
            writeText("Terima Kasih atas Kunjungan Anda!\n")
            writeAutoCutter(outputStream)

            outputStream.flush()
            try { Thread.sleep(800) } catch (_: Exception) {}
            outputStream.close()
            socket.close()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Kirim halaman test print ke Bluetooth printer
     */
    @SuppressLint("MissingPermission")
    fun testPrintBluetooth(deviceAddress: String, storeName: String = "WARUNGKU POS"): Result<Boolean> {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            ?: return Result.failure(Exception("Bluetooth tidak didukung"))
        if (!bluetoothAdapter.isEnabled)
            return Result.failure(Exception("Bluetooth belum aktif"))

        var socket: BluetoothSocket? = null
        return try {
            val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
            bluetoothAdapter.cancelDiscovery()

            socket = try {
                device.createRfcommSocketToServiceRecord(SPP_UUID)
            } catch (e: Exception) {
                try {
                    device.createInsecureRfcommSocketToServiceRecord(SPP_UUID)
                } catch (e2: Exception) {
                    val m = device.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
                    m.invoke(device, 1) as BluetoothSocket
                }
            }

            try {
                socket.connect()
            } catch (e: Exception) {
                try { socket.close() } catch (_: Exception) {}
                val m = device.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
                socket = m.invoke(device, 1) as BluetoothSocket
                socket.connect()
            }
            val out = socket.outputStream

            val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            out.write(ESC_INIT)
            out.write(ESC_ALIGN_CENTER)
            out.write(ESC_BOLD_ON)
            out.write(ESC_DOUBLE_HEIGHT_ON)
            out.write("$storeName\n".toByteArray())
            out.write(ESC_DOUBLE_HEIGHT_OFF)
            out.write(ESC_BOLD_OFF)
            out.write("================================\n".toByteArray())
            out.write("    ** TEST PRINT SUKSES **\n".toByteArray())
            out.write("================================\n".toByteArray())
            out.write(ESC_ALIGN_LEFT)
            out.write("Waktu : ${df.format(Date())}\n".toByteArray())
            out.write("Status: Printer Siap Pakai (80mm Auto-Cutter)\n".toByteArray())
            out.write("Koneksi: Bluetooth\n".toByteArray())
            out.write("--------------------------------\n".toByteArray())
            out.write(ESC_ALIGN_CENTER)
            out.write("WarungKu POS - Siap Cetak Struk\n".toByteArray())
            writeAutoCutter(out)
            out.flush()
            try { Thread.sleep(800) } catch (_: Exception) {}
            out.close()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            try { socket?.close() } catch (_: Exception) {}
        }
    }

    /**
     * Kirim halaman test print ke printer LAN/Network
     */
    fun testPrintNetwork(ip: String, port: Int = 9100, storeName: String = "WARUNGKU POS"): Result<Boolean> {
        return try {
            val socket = Socket(ip, port)
            socket.soTimeout = 5000
            val out = socket.getOutputStream()
            val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

            out.write(ESC_INIT)
            out.write(ESC_ALIGN_CENTER)
            out.write(ESC_BOLD_ON)
            out.write(ESC_DOUBLE_HEIGHT_ON)
            out.write("$storeName\n".toByteArray())
            out.write(ESC_DOUBLE_HEIGHT_OFF)
            out.write(ESC_BOLD_OFF)
            out.write("================================\n".toByteArray())
            out.write("    ** TEST PRINT SUKSES **\n".toByteArray())
            out.write("================================\n".toByteArray())
            out.write(ESC_ALIGN_LEFT)
            out.write("Waktu : ${df.format(Date())}\n".toByteArray())
            out.write("Status: Printer Siap Pakai\n".toByteArray())
            out.write("Koneksi: LAN/Network ($ip:$port)\n".toByteArray())
            out.write("--------------------------------\n".toByteArray())
            out.write(ESC_ALIGN_CENTER)
            out.write("WarungKu POS - Siap Cetak Struk\n".toByteArray())
            writeAutoCutter(out)
            out.flush()
            out.close()
            socket.close()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Test buka Cash Drawer via Bluetooth
     */
    @SuppressLint("MissingPermission")
    fun testCashDrawerBluetooth(deviceAddress: String): Result<Boolean> {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            ?: return Result.failure(Exception("Bluetooth tidak didukung"))
        if (!bluetoothAdapter.isEnabled)
            return Result.failure(Exception("Bluetooth belum aktif"))

        var socket: BluetoothSocket? = null
        return try {
            val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
            socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
            bluetoothAdapter.cancelDiscovery()
            socket.connect()
            val out = socket.outputStream
            out.write(ESC_INIT)
            out.write(ESC_KICK_DRAWER)
            // Kirim sekali lagi untuk laci yang lebih keras
            out.write(byteArrayOf(0x1B, 0x70, 0x01, 0x19, 0xFA.toByte()))
            out.flush()
            out.close()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            try { socket?.close() } catch (_: Exception) {}
        }
    }

    /**
     * Test buka Cash Drawer via LAN/Network
     */
    fun testCashDrawerNetwork(ip: String, port: Int = 9100): Result<Boolean> {
        return try {
            val socket = Socket(ip, port)
            socket.soTimeout = 5000
            val out = socket.getOutputStream()
            out.write(ESC_INIT)
            out.write(ESC_KICK_DRAWER)
            out.write(byteArrayOf(0x1B, 0x70, 0x01, 0x19, 0xFA.toByte()))
            out.flush()
            out.close()
            socket.close()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
