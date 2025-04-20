package com.diplom.demo.Enums;

public enum PaymentStatus {
    PENDING,      // Ожидает оплаты
    COMPLETED,    // Оплата прошла успешно
    FAILED,       // Ошибка оплаты
    CANCELED,     // Оплата отменена пользователем или системой
    REFUNDED      // Деньги возвращены
}

