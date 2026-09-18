package com.example.cocktail.Exception;

/**
 * 查無資料，對應 HTTP 404
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
