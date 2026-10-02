package com.example.cocktail.exception;

/**
 * 查無資料，對應 HTTP 404
 * message 是回給前端的中文訊息，logMessage 是寫進 log 的英文說明
 */
public class NotFoundException extends RuntimeException {

    private final String logMessage;

    /**
     * @param message    回給前端的訊息
     * @param logMessage 寫進 log 的英文說明，帶上排查需要的 ID 等參數
     */
    public NotFoundException(String message, String logMessage) {
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
