import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/DashboardScreen.kt", "r") as f:
    content = f.read()

# Replace LocalDate logic with Calendar logic
old_logic = """    // Simple date filtering (assuming transaction.date is timestamp in ms)
    val filteredTransactions = transactions.filter { tx ->
        if (selectedFilter == "Semua") return@filter true
        
        val txDate = LocalDate.ofEpochDay(tx.timestamp / (24 * 60 * 60 * 1000))
        val today = LocalDate.now()
        
        when (selectedFilter) {
            "Hari Ini" -> txDate.isEqual(today)
            "Minggu Ini" -> txDate.isAfter(today.minusDays(7)) || txDate.isEqual(today)
            "Bulan Ini" -> txDate.isAfter(today.minusDays(30)) || txDate.isEqual(today)
            else -> true
        }
    }"""

new_logic = """    // Simple date filtering
    val filteredTransactions = transactions.filter { tx ->
        if (selectedFilter == "Semua") return@filter true
        
        val txCal = java.util.Calendar.getInstance().apply { timeInMillis = tx.timestamp }
        val todayCal = java.util.Calendar.getInstance()
        
        when (selectedFilter) {
            "Hari Ini" -> {
                txCal.get(java.util.Calendar.YEAR) == todayCal.get(java.util.Calendar.YEAR) &&
                txCal.get(java.util.Calendar.DAY_OF_YEAR) == todayCal.get(java.util.Calendar.DAY_OF_YEAR)
            }
            "Minggu Ini" -> {
                val diffDays = (todayCal.timeInMillis - txCal.timeInMillis) / (24 * 60 * 60 * 1000)
                diffDays in 0..7
            }
            "Bulan Ini" -> {
                val diffDays = (todayCal.timeInMillis - txCal.timeInMillis) / (24 * 60 * 60 * 1000)
                diffDays in 0..30
            }
            else -> true
        }
    }"""

content = content.replace(old_logic, new_logic)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/DashboardScreen.kt", "w") as f:
    f.write(content)

