package com.mohali.store.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import java.util.Date

// ============================
// USER MODEL
// ============================
data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val role: UserRole = UserRole.CASHIER,
    val isActive: Boolean = true,
    val createdAt: Timestamp = Timestamp.now(),
    val lastLogin: Timestamp? = null,
    val profileImage: String = ""
)

enum class UserRole(val displayName: String, val nameAr: String) {
    ADMIN("Admin", "مدير"),
    MANAGER("Manager", "مشرف"),
    CASHIER("Cashier", "كاشير"),
    VIEWER("Viewer", "مشاهد فقط")
}

// ============================
// PRODUCT MODEL
// ============================
@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val nameAr: String = "",
    val barcode: String = "",
    val category: ProductCategory = ProductCategory.PHONES,
    val brand: String = "",
    val model: String = "",
    val buyPrice: Double = 0.0,
    val sellPrice: Double = 0.0,
    val quantity: Int = 0,
    val minQuantity: Int = 5,
    val description: String = "",
    val imageUrl: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val location: String = "",
    val warrantyMonths: Int = 0,
    val color: String = "",
    val storage: String = ""
)

enum class ProductCategory(val displayName: String, val nameAr: String, val icon: String) {
    PHONES("Phones", "هواتف", "phone"),
    ACCESSORIES("Accessories", "اكسسوارات", "headphones"),
    ELECTRONICS("Electronics", "إلكترونيات", "computer"),
    COSMETICS("Cosmetics", "مستحضرات تجميل", "spa"),
    BEAUTY_TOOLS("Beauty Tools", "أدوات تجميل", "brush"),
    CHARGERS("Chargers", "شواحن", "battery_charging_full"),
    CASES("Cases & Covers", "جرابات وأغطية", "phone_android"),
    SCREENS("Screens & Glass", "شاشات وزجاج", "tablet"),
    SMARTWATCHES("Smart Watches", "ساعات ذكية", "watch"),
    OTHER("Other", "أخرى", "category")
}

// ============================
// SALE MODEL
// ============================
@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey val id: String = "",
    val invoiceNumber: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val items: List<SaleItem> = emptyList(),
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val discountType: DiscountType = DiscountType.AMOUNT,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val paidAmount: Double = 0.0,
    val changeAmount: Double = 0.0,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val notes: String = "",
    val cashierId: String = "",
    val cashierName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val status: SaleStatus = SaleStatus.COMPLETED,
    val isSynced: Boolean = false
)

data class SaleItem(
    val productId: String = "",
    val productName: String = "",
    val barcode: String = "",
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0
)

enum class DiscountType { AMOUNT, PERCENTAGE }
enum class SaleStatus { COMPLETED, CANCELLED, RETURNED, PENDING }
enum class PaymentMethod(val nameAr: String) {
    CASH("نقدي"),
    CARD("بطاقة"),
    TRANSFER("تحويل"),
    INSTALLMENT("تقسيط")
}

// ============================
// CUSTOMER MODEL
// ============================
@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val totalPurchases: Double = 0.0,
    val purchaseCount: Int = 0,
    val lastPurchase: Long = 0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val loyaltyPoints: Int = 0
)

// ============================
// PURCHASE / SUPPLY MODEL
// ============================
@Entity(tableName = "purchases")
data class Purchase(
    @PrimaryKey val id: String = "",
    val supplierName: String = "",
    val supplierPhone: String = "",
    val items: List<PurchaseItem> = emptyList(),
    val totalCost: Double = 0.0,
    val paidAmount: Double = 0.0,
    val remainingAmount: Double = 0.0,
    val invoiceNumber: String = "",
    val notes: String = "",
    val createdById: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val status: PurchaseStatus = PurchaseStatus.COMPLETED,
    val isSynced: Boolean = false
)

data class PurchaseItem(
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 1,
    val unitCost: Double = 0.0,
    val total: Double = 0.0
)

enum class PurchaseStatus { COMPLETED, PENDING, PARTIAL }

// ============================
// EXPENSE MODEL
// ============================
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey val id: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val category: ExpenseCategory = ExpenseCategory.OTHER,
    val description: String = "",
    val createdById: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

enum class ExpenseCategory(val nameAr: String) {
    RENT("إيجار"),
    UTILITIES("مرافق"),
    SALARY("رواتب"),
    MAINTENANCE("صيانة"),
    MARKETING("تسويق"),
    OTHER("أخرى")
}

// ============================
// NOTIFICATION MODEL
// ============================
data class AppNotification(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val type: NotificationType = NotificationType.SALE,
    val data: Map<String, String> = emptyMap(),
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class NotificationType {
    SALE, LOW_STOCK, PURCHASE, EXPENSE, SYSTEM
}

// ============================
// DASHBOARD STATS
// ============================
data class DashboardStats(
    val todaySales: Double = 0.0,
    val todayTransactions: Int = 0,
    val weekSales: Double = 0.0,
    val monthSales: Double = 0.0,
    val totalProducts: Int = 0,
    val lowStockProducts: Int = 0,
    val outOfStockProducts: Int = 0,
    val totalCustomers: Int = 0,
    val netProfit: Double = 0.0,
    val topSellingProducts: List<Product> = emptyList()
)
