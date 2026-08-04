package com.yofidewo.pos.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.yofidewo.pos.data.*
import com.yofidewo.pos.ui.PosViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupMetadata(
    val timestamp: Long = System.currentTimeMillis(),
    val productCount: Int = 0,
    val categoryCount: Int = 0,
    val discountCount: Int = 0,
    val transactionCount: Int = 0
)

object DataExporter {

    private fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        val uri: Uri = try {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            Uri.fromFile(file)
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    private fun saveAndShareFile(context: Context, fileName: String, contentBytes: ByteArray, mimeType: String, title: String) {
        val cacheFile = File(context.cacheDir, fileName)
        FileOutputStream(cacheFile).use { it.write(contentBytes) }

        try {
            val downloadsDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)
            if (downloadsDir != null) {
                val publicFile = File(downloadsDir, fileName)
                FileOutputStream(publicFile).use { it.write(contentBytes) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        shareFile(context, cacheFile, mimeType, title)
    }

    fun parseBackupMetadata(jsonContent: String): BackupMetadata? {
        return try {
            val root = JSONObject(jsonContent)
            val ts = root.optLong("timestamp", System.currentTimeMillis())
            val prods = if (root.has("products")) root.getJSONArray("products").length() else 0
            val cats = if (root.has("categories")) root.getJSONArray("categories").length() else 0
            val discs = if (root.has("discounts")) root.getJSONArray("discounts").length() else 0
            val txs = if (root.has("transactions")) root.getJSONArray("transactions").length() else 0
            BackupMetadata(timestamp = ts, productCount = prods, categoryCount = cats, discountCount = discs, transactionCount = txs)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Export Products to CSV (Compatible with Microsoft Excel / Google Sheets)
     */
    suspend fun exportProductsToCsv(context: Context, products: List<ProductEntity>, categories: List<CategoryEntity>, brands: List<BrandEntity>) = withContext(Dispatchers.IO) {
        val categoryMap = categories.associateBy { it.id }
        val brandMap = brands.associateBy { it.id }

        val sb = StringBuilder()
        sb.appendLine("ID,Nama Produk,Kode SKU,Barcode,Kategori,Merek,Harga Beli,Harga Jual,Stok,Stok Minimal,Deskripsi")

        products.forEach { p ->
            val catName = categoryMap[p.categoryId]?.name ?: "-"
            val brandName = brandMap[p.brandId]?.name ?: "-"
            val cleanName = p.name.replace(",", " ")
            val cleanDesc = p.description.replace(",", " ")
            sb.appendLine("${p.id},\"$cleanName\",\"${p.code}\",\"${p.barcode}\",\"$catName\",\"$brandName\",${p.buyPrice},${p.sellPrice},${p.stock},${p.minStock},\"$cleanDesc\"")
        }

        try {
            val fileName = "Daftar_Produk_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
            saveAndShareFile(context, fileName, sb.toString().toByteArray(), "text/csv", "Bagikan / Simpan CSV Produk (Excel)")
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Gagal mengekspor CSV: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Export Products to PDF (Document for Catalog & Inventory)
     */
    suspend fun exportProductsToPdf(context: Context, products: List<ProductEntity>, categories: List<CategoryEntity>, brands: List<BrandEntity>) = withContext(Dispatchers.IO) {
        val categoryMap = categories.associateBy { it.id }
        val pdfDoc = android.graphics.pdf.PdfDocument()

        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size
        val page = pdfDoc.startPage(pageInfo)
        val canvas: android.graphics.Canvas = page.canvas

        val paint = android.graphics.Paint()
        val titlePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 18f
            isFakeBoldText = true
        }
        val headerPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.DKGRAY
            textSize = 10f
            isFakeBoldText = true
        }
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 9f
        }

        var y = 40f
        canvas.drawText("WARUNGKU POS - KATALOG & LAPORAN PRODUK", 30f, y, titlePaint)
        y += 20f

        val df = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        canvas.drawText("Tanggal Cetak: ${df.format(Date())} | Total Produk: ${products.size} Item", 30f, y, textPaint)
        y += 25f

        // Table Header
        canvas.drawRect(30f, y - 12f, 565f, y + 8f, android.graphics.Paint().apply { color = android.graphics.Color.LTGRAY })
        canvas.drawText("SKU", 35f, y, headerPaint)
        canvas.drawText("NAMA PRODUK", 100f, y, headerPaint)
        canvas.drawText("KATEGORI", 280f, y, headerPaint)
        canvas.drawText("STOK", 380f, y, headerPaint)
        canvas.drawText("HARGA MODAL", 430f, y, headerPaint)
        canvas.drawText("HARGA JUAL", 500f, y, headerPaint)
        y += 18f

        products.take(35).forEach { p ->
            val catName = categoryMap[p.categoryId]?.name ?: "-"
            val nameClean = if (p.name.length > 25) p.name.take(23) + ".." else p.name
            canvas.drawText(p.code.take(10), 35f, y, textPaint)
            canvas.drawText(nameClean, 100f, y, textPaint)
            canvas.drawText(catName.take(12), 280f, y, textPaint)
            canvas.drawText("${p.stock} Pcs", 380f, y, textPaint)
            canvas.drawText("Rp ${p.buyPrice.toInt()}", 430f, y, textPaint)
            canvas.drawText("Rp ${p.sellPrice.toInt()}", 500f, y, textPaint)
            y += 16f
        }

        pdfDoc.finishPage(page)

        try {
            val fileName = "Katalog_Produk_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
            val outputStream = java.io.ByteArrayOutputStream()
            pdfDoc.writeTo(outputStream)
            pdfDoc.close()
            saveAndShareFile(context, fileName, outputStream.toByteArray(), "application/pdf", "Bagikan / Simpan PDF Produk")
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Gagal mencetak PDF Produk: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Export Transactions & Profit/Loss Report to CSV (Compatible with Excel)
     */
    suspend fun exportTransactionsToCsv(context: Context, transactions: List<TransactionEntity>, viewModel: PosViewModel) = withContext(Dispatchers.IO) {
        val sb = StringBuilder()
        sb.appendLine("No Faktur,Tanggal,Kasir,Pelanggan,Metode Bayar,Status Bayar,Subtotal,Diskon,Total Omzet,Catatan")

        val df = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        transactions.forEach { tx ->
            val cleanCustomer = tx.customerName.replace(",", " ")
            val cleanNotes = tx.notes.replace(",", " ")
            val dateStr = df.format(Date(tx.timestamp))
            sb.appendLine("\"${tx.invoiceNumber}\",\"$dateStr\",\"${tx.cashierName}\",\"$cleanCustomer\",\"${tx.paymentMethod}\",\"${tx.paymentStatus}\",${tx.subTotalAmount},${tx.discountAmount},${tx.totalAmount},\"$cleanNotes\"")
        }

        try {
            val fileName = "Laporan_Penjualan_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
            saveAndShareFile(context, fileName, sb.toString().toByteArray(), "text/csv", "Bagikan / Simpan CSV Laporan Penjualan (Excel)")
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Gagal mengekspor Laporan: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Export Transactions & Profit/Loss Report to PDF Document
     */
    suspend fun exportTransactionsToPdf(context: Context, transactions: List<TransactionEntity>, viewModel: PosViewModel) = withContext(Dispatchers.IO) {
        val pdfDoc = android.graphics.pdf.PdfDocument()

        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size
        val page = pdfDoc.startPage(pageInfo)
        val canvas: android.graphics.Canvas = page.canvas

        val titlePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 18f
            isFakeBoldText = true
        }
        val headerPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.DKGRAY
            textSize = 10f
            isFakeBoldText = true
        }
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 9f
        }

        var y = 40f
        canvas.drawText("WARUNGKU POS - LAPORAN PENJUALAN & OMZET", 30f, y, titlePaint)
        y += 20f

        val df = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val totalOmset = transactions.sumOf { it.totalAmount }
        canvas.drawText("Tanggal Cetak: ${df.format(Date())} | Total Transaksi: ${transactions.size} | Omzet Total: Rp ${totalOmset.toInt()}", 30f, y, textPaint)
        y += 25f

        // Table Header
        canvas.drawRect(30f, y - 12f, 565f, y + 8f, android.graphics.Paint().apply { color = android.graphics.Color.LTGRAY })
        canvas.drawText("FAKTUR", 35f, y, headerPaint)
        canvas.drawText("TANGGAL", 130f, y, headerPaint)
        canvas.drawText("KASIR", 240f, y, headerPaint)
        canvas.drawText("PELANGGAN", 330f, y, headerPaint)
        canvas.drawText("METODE", 430f, y, headerPaint)
        canvas.drawText("TOTAL OMZET", 490f, y, headerPaint)
        y += 18f

        transactions.take(35).forEach { tx ->
            val dateStr = df.format(Date(tx.timestamp))
            canvas.drawText(tx.invoiceNumber.take(14), 35f, y, textPaint)
            canvas.drawText(dateStr.take(15), 130f, y, textPaint)
            canvas.drawText(tx.cashierName.take(12), 240f, y, textPaint)
            canvas.drawText(tx.customerName.take(12), 330f, y, textPaint)
            canvas.drawText(tx.paymentMethod.take(8), 430f, y, textPaint)
            canvas.drawText("Rp ${tx.totalAmount.toInt()}", 490f, y, textPaint)
            y += 16f
        }

        pdfDoc.finishPage(page)

        try {
            val fileName = "Laporan_Penjualan_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
            val outputStream = java.io.ByteArrayOutputStream()
            pdfDoc.writeTo(outputStream)
            pdfDoc.close()
            saveAndShareFile(context, fileName, outputStream.toByteArray(), "application/pdf", "Bagikan / Simpan PDF Laporan Penjualan")
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Gagal mencetak PDF Laporan: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Export Full Database Backup to JSON file
     */
    suspend fun exportFullDatabaseBackup(context: Context, repository: PosRepository) = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject()
            root.put("version", 1)
            root.put("timestamp", System.currentTimeMillis())

            // Users
            val usersArray = JSONArray()
            repository.getAllUsersSync().forEach { u ->
                val uObj = JSONObject()
                uObj.put("id", u.id)
                uObj.put("name", u.name)
                uObj.put("email", u.email)
                uObj.put("pin", u.pin)
                uObj.put("roleId", u.roleId ?: -1)
                usersArray.put(uObj)
            }
            root.put("users", usersArray)

            // Categories
            val categoriesArray = JSONArray()
            repository.getAllCategoriesSync().forEach { c ->
                val cObj = JSONObject()
                cObj.put("id", c.id)
                cObj.put("name", c.name)
                cObj.put("code", c.code)
                cObj.put("description", c.description)
                categoriesArray.put(cObj)
            }
            root.put("categories", categoriesArray)

            // Products
            val productsArray = JSONArray()
            repository.getAllProductsSync().forEach { p ->
                val pObj = JSONObject()
                pObj.put("id", p.id)
                pObj.put("name", p.name)
                pObj.put("code", p.code)
                pObj.put("barcode", p.barcode)
                pObj.put("buyPrice", p.buyPrice)
                pObj.put("sellPrice", p.sellPrice)
                pObj.put("stock", p.stock)
                pObj.put("minStock", p.minStock)
                pObj.put("description", p.description)
                productsArray.put(pObj)
            }
            root.put("products", productsArray)

            // Discounts
            val discountsArray = JSONArray()
            repository.getAllDiscountsSync().forEach { d ->
                val dObj = JSONObject()
                dObj.put("id", d.id)
                dObj.put("name", d.name)
                dObj.put("type", d.type)
                dObj.put("value", d.value)
                dObj.put("isActive", d.isActive)
                discountsArray.put(dObj)
            }
            root.put("discounts", discountsArray)

            val fileName = "Backup_WarungKu_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.json"
            saveAndShareFile(context, fileName, root.toString(2).toByteArray(), "application/json", "Simpan / Cadangkan File Database")
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Gagal membuat backup: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Restore Full Database from JSON String with Progress Feedback
     */
    suspend fun restoreDatabaseFromJson(
        context: Context,
        jsonContent: String,
        repository: PosRepository,
        onProgress: (current: Int, total: Int, label: String) -> Unit = { _, _, _ -> }
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonContent)
            var currentProcessed = 0
            val totalItems = (if (root.has("categories")) root.getJSONArray("categories").length() else 0) +
                    (if (root.has("products")) root.getJSONArray("products").length() else 0) +
                    (if (root.has("discounts")) root.getJSONArray("discounts").length() else 0)

            if (root.has("categories")) {
                val catArr = root.getJSONArray("categories")
                for (i in 0 until catArr.length()) {
                    val obj = catArr.getJSONObject(i)
                    repository.insertCategory(
                        CategoryEntity(
                            name = obj.optString("name", "Category"),
                            code = obj.optString("code", ""),
                            description = obj.optString("description", "")
                        )
                    )
                    currentProcessed++
                    onProgress(currentProcessed, totalItems.coerceAtLeast(1), "Memulihkan Kategori (${i + 1}/${catArr.length()})")
                }
            }

            if (root.has("products")) {
                val prodArr = root.getJSONArray("products")
                for (i in 0 until prodArr.length()) {
                    val obj = prodArr.getJSONObject(i)
                    repository.insertProduct(
                        ProductEntity(
                            name = obj.optString("name", "Produk"),
                            code = obj.optString("code", "SKU-${System.currentTimeMillis()}-$i"),
                            barcode = obj.optString("barcode", ""),
                            categoryId = null,
                            brandId = null,
                            warehouseId = null,
                            buyPrice = obj.optDouble("buyPrice", 0.0),
                            sellPrice = obj.optDouble("sellPrice", 0.0),
                            stock = obj.optInt("stock", 0),
                            minStock = obj.optInt("minStock", 5),
                            description = obj.optString("description", "")
                        )
                    )
                    currentProcessed++
                    if (i % 5 == 0 || i == prodArr.length() - 1) {
                        onProgress(currentProcessed, totalItems.coerceAtLeast(1), "Memulihkan Produk (${i + 1}/${prodArr.length()})")
                    }
                }
            }

            if (root.has("discounts")) {
                val discArr = root.getJSONArray("discounts")
                for (i in 0 until discArr.length()) {
                    val obj = discArr.getJSONObject(i)
                    repository.insertDiscount(
                        DiscountEntity(
                            name = obj.optString("name", "Diskon"),
                            type = obj.optString("type", "PERCENT"),
                            value = obj.optDouble("value", 0.0),
                            isActive = obj.optBoolean("isActive", true)
                        )
                    )
                    currentProcessed++
                    onProgress(currentProcessed, totalItems.coerceAtLeast(1), "Memulihkan Diskon (${i + 1}/${discArr.length()})")
                }
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
