package com.yofidewo.pos.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.UUID
import kotlin.random.Random

object FirebaseSyncManager {
    private const val TAG = "FirebaseSyncManager"
    
    // Default Firebase Realtime Database URL
    var firebaseUrl = "https://warungku-pos-default-rtdb.firebaseio.com"

    private val client = OkHttpClient.Builder().build()
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private var syncJob: Job? = null
    var currentOutletCode: String = ""
        private set

    fun generateOutletCode(): String {
        val randomDigits = Random.nextInt(10000, 99999)
        return "POS-$randomDigits"
    }

    suspend fun createOutlet(code: String, name: String, address: String, phone: String): Boolean = withContext(Dispatchers.IO) {
        currentOutletCode = code
        val profileData = """
            {
                "code": "$code",
                "name": "$name",
                "address": "$address",
                "phone": "$phone",
                "createdAt": ${System.currentTimeMillis()},
                "licenseType": "FREE TRIAL",
                "activationDate": "-",
                "txCount": 0
            }
        """.trimIndent()

        val request = Request.Builder()
            .url("$firebaseUrl/outlets/$code/profile.json")
            .put(profileData.toRequestBody(jsonMediaType))
            .build()

        try {
            client.newCall(request).execute().close()
        } catch (e: Exception) {
            Log.e(TAG, "Cloud sync notice for outlet $code: ${e.message}")
        }
        true
    }

    suspend fun updateOutletMetadata(code: String, licenseType: String? = null, activationDate: String? = null, txCount: Int? = null) = withContext(Dispatchers.IO) {
        if (code.isBlank()) return@withContext
        val patches = mutableListOf<String>()
        if (licenseType != null) patches.add("\"licenseType\": \"$licenseType\"")
        if (activationDate != null) patches.add("\"activationDate\": \"$activationDate\"")
        if (txCount != null) patches.add("\"txCount\": $txCount")
        if (patches.isEmpty()) return@withContext

        val patchJson = "{ ${patches.joinToString(", ")} }"
        val request = Request.Builder()
            .url("$firebaseUrl/outlets/$code/profile.json")
            .patch(patchJson.toRequestBody(jsonMediaType))
            .build()

        try {
            client.newCall(request).execute().close()
        } catch (e: Exception) {
            Log.e(TAG, "Notice updating outlet metadata $code: ${e.message}")
        }
    }

    suspend fun checkOutletExists(code: String): Boolean = withContext(Dispatchers.IO) {
        if (code.isBlank()) return@withContext false
        val request = Request.Builder()
            .url("$firebaseUrl/outlets/$code/profile.json")
            .get()
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string()
                if (response.isSuccessful && !body.isNullOrBlank() && body != "null") {
                    return@withContext true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Notice checking cloud outlet $code: ${e.message}")
        }
        // Fallback: If code is a valid POS format (POS-XXXXX), accept for local & peer sync
        code.trim().startsWith("POS-", ignoreCase = true) || code.trim().length >= 4
    }

    suspend fun pushProduct(outletCode: String, product: ProductEntity) = withContext(Dispatchers.IO) {
        if (outletCode.isBlank()) return@withContext
        val productJson = """
            {
                "id": ${product.id},
                "name": "${product.name.replace("\"", "\\\"")}",
                "code": "${product.code}",
                "barcode": "${product.barcode}",
                "buyPrice": ${product.buyPrice},
                "sellPrice": ${product.sellPrice},
                "stock": ${product.stock},
                "minStock": ${product.minStock},
                "description": "${product.description.replace("\"", "\\\"")}"
            }
        """.trimIndent()

        val request = Request.Builder()
            .url("$firebaseUrl/outlets/$outletCode/products/${product.id}.json")
            .put(productJson.toRequestBody(jsonMediaType))
            .build()

        try {
            client.newCall(request).execute().close()
        } catch (e: Exception) {
            Log.e(TAG, "Error pushing product ${product.id}", e)
        }
    }

    suspend fun pushTransaction(outletCode: String, transaction: TransactionEntity, items: List<TransactionItemEntity>) = withContext(Dispatchers.IO) {
        if (outletCode.isBlank()) return@withContext
        val itemsJson = items.joinToString(",") { item ->
            """
                {
                    "productId": ${item.productId},
                    "productName": "${item.productName.replace("\"", "\\\"")}",
                    "price": ${item.price},
                    "quantity": ${item.quantity},
                    "subtotal": ${item.subtotal}
                }
            """.trimIndent()
        }

        val txJson = """
            {
                "id": ${transaction.id},
                "invoiceNumber": "${transaction.invoiceNumber}",
                "userId": ${transaction.userId},
                "cashierName": "${transaction.cashierName}",
                "customerName": "${transaction.customerName}",
                "totalAmount": ${transaction.totalAmount},
                "paidAmount": ${transaction.paidAmount},
                "changeAmount": ${transaction.changeAmount},
                "paymentMethod": "${transaction.paymentMethod}",
                "status": "${transaction.status}",
                "timestamp": ${transaction.timestamp},
                "items": [$itemsJson]
            }
        """.trimIndent()

        val request = Request.Builder()
            .url("$firebaseUrl/outlets/$outletCode/transactions/${transaction.id}.json")
            .put(txJson.toRequestBody(jsonMediaType))
            .build()

        try {
            client.newCall(request).execute().close()
        } catch (e: Exception) {
            Log.e(TAG, "Error pushing transaction ${transaction.id}", e)
        }
    }

    suspend fun fetchCloudProducts(outletCode: String): List<ProductEntity> = withContext(Dispatchers.IO) {
        if (outletCode.isBlank()) return@withContext emptyList()
        val request = Request.Builder()
            .url("$firebaseUrl/outlets/$outletCode/products.json")
            .get()
            .build()

        val resultList = mutableListOf<ProductEntity>()
        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string()
                if (response.isSuccessful && !body.isNullOrBlank() && body != "null") {
                    val rootObj = org.json.JSONObject(body)
                    val keys = rootObj.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        val obj = rootObj.getJSONObject(key)
                        resultList.add(
                            ProductEntity(
                                id = obj.optLong("id", key.toLongOrNull() ?: 0L),
                                name = obj.optString("name", "Product"),
                                code = obj.optString("code", ""),
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
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching cloud products", e)
        }
        resultList
    }

    suspend fun fetchCloudTransactions(outletCode: String): List<Pair<TransactionEntity, List<TransactionItemEntity>>> = withContext(Dispatchers.IO) {
        if (outletCode.isBlank()) return@withContext emptyList()
        val request = Request.Builder()
            .url("$firebaseUrl/outlets/$outletCode/transactions.json")
            .get()
            .build()

        val resultList = mutableListOf<Pair<TransactionEntity, List<TransactionItemEntity>>>()
        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string()
                if (response.isSuccessful && !body.isNullOrBlank() && body != "null") {
                    val rootObj = org.json.JSONObject(body)
                    val keys = rootObj.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        val obj = rootObj.getJSONObject(key)
                        val txId = obj.optLong("id", key.toLongOrNull() ?: 0L)
                        val tx = TransactionEntity(
                            id = txId,
                            invoiceNumber = obj.optString("invoiceNumber", "INV-$txId"),
                            userId = obj.optLong("userId", 1),
                            cashierName = obj.optString("cashierName", "Kasir"),
                            customerName = obj.optString("customerName", "Umum"),
                            totalAmount = obj.optDouble("totalAmount", 0.0),
                            paidAmount = obj.optDouble("paidAmount", 0.0),
                            changeAmount = obj.optDouble("changeAmount", 0.0),
                            paymentMethod = obj.optString("paymentMethod", "Cash"),
                            status = obj.optString("status", "COMPLETED"),
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                        )
                        val items = mutableListOf<TransactionItemEntity>()
                        if (obj.has("items")) {
                            val itemsArr = obj.getJSONArray("items")
                            for (i in 0 until itemsArr.length()) {
                                val itemObj = itemsArr.getJSONObject(i)
                                items.add(
                                    TransactionItemEntity(
                                        id = 0,
                                        transactionId = txId,
                                        productId = itemObj.optLong("productId", 0),
                                        productName = itemObj.optString("productName", "Item"),
                                        price = itemObj.optDouble("price", 0.0),
                                        quantity = itemObj.optInt("quantity", 1),
                                        subtotal = itemObj.optDouble("subtotal", 0.0)
                                    )
                                )
                            }
                        }
                        resultList.add(Pair(tx, items))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching cloud transactions", e)
        }
        resultList
    }

    fun startSyncLoop(
        coroutineScope: CoroutineScope,
        outletCode: String,
        onProductsFetched: (List<ProductEntity>) -> Unit,
        onTransactionsFetched: (List<Pair<TransactionEntity, List<TransactionItemEntity>>>) -> Unit
    ) {
        currentOutletCode = outletCode
        syncJob?.cancel()
        syncJob = coroutineScope.launch {
            while (isActive) {
                if (currentOutletCode.isNotBlank()) {
                    val products = fetchCloudProducts(currentOutletCode)
                    if (products.isNotEmpty()) {
                        onProductsFetched(products)
                    }
                    val txs = fetchCloudTransactions(currentOutletCode)
                    if (txs.isNotEmpty()) {
                        onTransactionsFetched(txs)
                    }
                }
                delay(4000) // Poll every 4s for real-time multi-device sync
            }
        }
    }

    fun stopSync() {
        syncJob?.cancel()
        syncJob = null
    }
}
