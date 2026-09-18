package com.example.cocktail.Exception;

/**
 * 業務規則不符（例如重複新增），對應 HTTP 400
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
