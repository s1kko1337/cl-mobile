package com.example.ecommerceapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ecommerceapp.data.model.CartItem

/**
 * Основная база данных Room приложения.
 *
 * Содержит таблицу корзины (cart_items) и предоставляет доступ к DAO.
 * Текущая версия базы данных: 2.
 *
 * @property cartDao Возвращает DAO для работы с корзиной
 */
@Database(entities = [CartItem::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Предоставляет доступ к DAO для операций с корзиной.
     *
     * @return Экземпляр CartDao
     */
    abstract fun cartDao(): CartDao

    companion object {
        /**
         * Миграция базы данных с версии 1 на версию 2.
         *
         * Добавляет колонку imageId в таблицу cart_items для хранения ID изображения товара.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cart_items ADD COLUMN imageId INTEGER")
            }
        }
    }
}