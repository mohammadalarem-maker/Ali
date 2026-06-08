package com.mohali.store.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.mohali.store.data.local.MohaliDatabase
import com.mohali.store.data.local.ProductDao
import com.mohali.store.data.local.SaleDao
import com.mohali.store.data.local.CustomerDao
import com.mohali.store.data.local.PurchaseDao
import com.mohali.store.data.local.ExpenseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MohaliDatabase =
        Room.databaseBuilder(context, MohaliDatabase::class.java, MohaliDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideProductDao(db: MohaliDatabase): ProductDao = db.productDao()

    @Provides
    fun provideSaleDao(db: MohaliDatabase): SaleDao = db.saleDao()

    @Provides
    fun provideCustomerDao(db: MohaliDatabase): CustomerDao = db.customerDao()

    @Provides
    fun providePurchaseDao(db: MohaliDatabase): PurchaseDao = db.purchaseDao()

    @Provides
    fun provideExpenseDao(db: MohaliDatabase): ExpenseDao = db.expenseDao()
}
