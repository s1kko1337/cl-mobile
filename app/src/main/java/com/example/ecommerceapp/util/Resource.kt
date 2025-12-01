package com.example.ecommerceapp.util

/**
 * Sealed класс для представления состояния операции с данными.
 *
 * Используется для обёртывания результатов сетевых запросов и других операций,
 * которые могут завершиться успехом, ошибкой или находиться в процессе выполнения.
 *
 * @param T Тип данных, которые возвращает операция
 * @property data Данные, полученные в результате операции (опционально)
 * @property message Сообщение об ошибке или статусе (опционально)
 */
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    /**
     * Успешное выполнение операции с данными.
     *
     * @param data Данные, полученные в результате операции
     */
    class Success<T>(data: T) : Resource<T>(data)

    /**
     * Ошибка при выполнении операции.
     *
     * @param message Сообщение об ошибке
     * @param data Частичные данные, если доступны (опционально)
     */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    /**
     * Операция находится в процессе выполнения.
     *
     * @param data Предварительные данные, если доступны (опционально)
     */
    class Loading<T>(data: T? = null) : Resource<T>(data)
}