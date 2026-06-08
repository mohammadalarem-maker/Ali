package com.mohali.store.data.local

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import androidx.room.*
import com.mohali.store.data.models.*
import kotlinx.coroutines.flow.Flow

// ============================
// TYPE CONVERTERS
// ============================
class Converters {
    @TypeConverter
    fun fromSaleItemsList(items: List<SaleItem>): String {
        return com.google.gson.Gson().toJson(items)
    }

    @TypeConverter
    fun toSaleItemsList(json: String): List<SaleItem> {
        return com.google.gson.Gson().fromJson(json, Array<SaleItem>::class.java).toList()
    }

    @TypeConverter
    fun fromPurchaseItemsList(items: List<PurchaseItem>): String {
        return com.google.gson.Gson().toJson(items)
    }

    @TypeConverter
    fun toPurchaseItemsList(json: String): List<PurchaseItem> {
        return com.google.gson.Gson().fromJson(json, Array<PurchaseItem>::class.java).toList()
    }

    @TypeConverter
    fun fromProductCategory(category: ProductCategory): String = category.name

    @TypeConverter
    fun toProductCategory(name: String): ProductCategory = ProductCategory.valueOf(name)

    @TypeConverter
    fun fromSaleStatus(status: SaleStatus): String = status.name

    @TypeConverter
    fun toSaleStatus(name: String): SaleStatus = SaleStatus.valueOf(name)

    @TypeConverter
    fun fromPaymentMethod(method: PaymentMethod): String = method.name

    @TypeConverter
    fun toPaymentMethod(name: String): PaymentMethod = PaymentMethod.valueOf(name)

    @TypeConverter
    fun fromDiscountType(type: DiscountType): String = type.name

    @TypeConverter
    fun toDiscountType(name: String): DiscountType = DiscountType.valueOf(name)

    @TypeConverter
    fun fromPurchaseStatus(status: PurchaseStatus): String = status.name

    @TypeConverter
    fun toPurchaseStatus(name: String): PurchaseStatus = PurchaseStatus.valueOf(name)

    @TypeConverter
    fun fromExpenseCategory(cat: ExpenseCategory): String = cat.name

    @TypeConverter
    fun toExpenseCategory(name: String): ExpenseCategory = ExpenseCategory.valueOf(name)
}

// ============================
// DAOS
// ============================
@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: String): Product?

    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    suspend fun getProductByBarcode(barcode: String): Product?

    @Query("SELECT * FROM products WHERE quantity <= minQuantity AND isActive = 1")
    fun getLowStockProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE quantity = 0 AND isActive = 1")
    fun getOutOfStockProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE category = :category AND isActive = 1")
    fun getProductsByCategory(category: ProductCategory): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%'")
    fun searchProducts(query: String): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Update
    suspend fun updateProduct(product: Product)

    @Query("UPDATE products SET quantity = quantity - :quantity WHERE id = :productId")
    suspend fun decreaseStock(productId: String, quantity: Int)

    @Query("UPDATE products SET quantity = quantity + :quantity WHERE id = :productId")
    suspend fun increaseStock(productId: String, quantity: Int)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProduct(id: String)

    @Query("SELECT COUNT(*) FROM products WHERE isActive = 1")
    suspend fun getTotalProductsCount(): Int
}

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY createdAt DESC")
    fun getAllSales(): Flow<List<Sale>>

    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getSaleById(id: String): Sale?

    @Query("SELECT * FROM sales WHERE createdAt >= :startDate AND createdAt <= :endDate ORDER BY createdAt DESC")
    fun getSalesByDateRange(startDate: Long, endDate: Long): Flow<List<Sale>>

    @Query("SELECT SUM(total) FROM sales WHERE createdAt >= :startDate AND createdAt <= :endDate AND status = 'COMPLETED'")
    suspend fun getTotalSalesAmount(startDate: Long, endDate: Long): Double?

    @Query("SELECT COUNT(*) FROM sales WHERE createdAt >= :startDate AND createdAt <= :endDate AND status = 'COMPLETED'")
    suspend fun getSalesCount(startDate: Long, endDate: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: Sale)

    @Update
    suspend fun updateSale(sale: Sale)

    @Query("SELECT * FROM sales WHERE isSynced = 0")
    suspend fun getUnsyncedSales(): List<Sale>

    @Query("UPDATE sales SET isSynced = 1 WHERE id = :id")
    suspend fun markSaleAsSynced(id: String)
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerById(id: String): Customer?

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%'")
    fun searchCustomers(query: String): Flow<List<Customer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer)

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Query("DELETE FROM customers WHERE id = :id")
    suspend fun deleteCustomer(id: String)
}

@Dao
interface PurchaseDao {
    @Query("SELECT * FROM purchases ORDER BY createdAt DESC")
    fun getAllPurchases(): Flow<List<Purchase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: Purchase)

    @Update
    suspend fun updatePurchase(purchase: Purchase)
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses WHERE createdAt >= :startDate AND createdAt <= :endDate")
    suspend fun getTotalExpenses(startDate: Long, endDate: Long): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpense(id: String)
}

// ============================
// DATABASE
// ============================
@Database(
    entities = [Product::class, Sale::class, Customer::class, Purchase::class, Expense::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MohaliDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao
    abstract fun customerDao(): CustomerDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        const val DATABASE_NAME = "mohali_store_db"
    }
}
