package com.example.cocktail.exception;

/**
 * 業務規則不符（例如重複新增），對應 HTTP 400
 * message 是回給前端的中文訊息，logMessage 是寫進 log 的英文說明
 */
public class BusinessException extends RuntimeException {

    private final String logMessage;

    /**
     * @param message    回給前端的訊息
     * @param logMessage 寫進 log 的英文說明，帶上排查需要的 ID 等參數
     */
    public BusinessException(String message, String logMessage) {
        super(message);
        this.logMessage = logMessage;
    }

    /**
     * @return 寫進 log 的英文說明
     */
    public String getLogMessage() {
        return logMessage;
    }
}
