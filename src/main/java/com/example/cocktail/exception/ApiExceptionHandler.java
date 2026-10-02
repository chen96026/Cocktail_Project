package com.example.cocktail.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全域例外處理，讓 Controller 不用各自包 try/catch
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /**
     * @param e 查無資料的例外
     * @return 404 與錯誤訊息
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(e.getMessage()));
    }

    /**
     * @param e 業務規則不符的例外
     * @return 400 與錯誤訊息
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException e) {
        log.warn("Business rule violated: {}", e.getMessage());
        return ResponseEntity.badRequest().body(new ApiError(e.getMessage()));
    }

    /**
     * 沒有任何端點對應這個路徑（例如改版前的舊路徑）
     * Spring Boot 3.2 起會丟 NoResourceFoundException，沒攔下來會被歸到下面的 500
     *
     * @param e 查無對應路徑的例外
     * @return 404 與錯誤訊息
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResource(NoResourceFoundException e) {
        log.warn("No endpoint found: {} /{}", e.getHttpMethod(), sanitize(e.getResourcePath()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError("找不到對應的 API 路徑"));
    }

    /**
     * 路徑存在但 HTTP 方法不對，回應帶 Allow 標頭列出支援的方法
     *
     * @param e       不支援該 HTTP 方法的例外
     * @param request 目前的請求，log 用來帶出路徑
     * @return 405 與錯誤訊息
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                             WebRequest request) {
        log.warn("HTTP method not supported: {} {}, supported: {}",
                e.getMethod(), sanitize(request.getDescription(false)), e.getSupportedHttpMethods());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .headers(e.getHeaders())
                .body(new ApiError("此 API 路徑不支援 " + e.getMethod() + " 方法"));
    }

    /**
     * @param e 漏帶必要的 query 參數
     * @return 400 與缺少的參數名稱
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParameter(MissingServletRequestParameterException e) {
        log.warn("Missing request parameter: {}", e.getParameterName());
        return ResponseEntity.badRequest().body(new ApiError("缺少必要參數：" + e.getParameterName()));
    }

    /**
     * @param e multipart 漏帶必要的 part，例如新增酒譜沒附圖片
     * @return 400 與缺少的欄位名稱
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiError> handleMissingPart(MissingServletRequestPartException e) {
        log.warn("Missing request part: {}", e.getRequestPartName());
        return ResponseEntity.badRequest().body(new ApiError("缺少必要欄位：" + e.getRequestPartName()));
    }

    /**
     * 路徑或 query 參數轉型失敗，例如 /recipes/abc
     *
     * @param e 參數型別不符的例外
     * @return 400 與格式錯誤的參數名稱
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("Request parameter type mismatch: {}={}", e.getName(), sanitize(String.valueOf(e.getValue())));
        return ResponseEntity.badRequest().body(new ApiError("參數 " + e.getName() + " 格式錯誤"));
    }

    /**
     * JSON 壞掉或欄位型別不符，parser 的細節只寫 log，不回給前端
     *
     * @param e 請求內容無法解析的例外
     * @return 400 與錯誤訊息
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException e) {
        log.warn("Malformed request body: {}", sanitize(e.getMostSpecificCause().getMessage()));
        return ResponseEntity.badRequest().body(new ApiError("請求內容格式錯誤"));
    }

    /**
     * @param e 其他未預期的例外
     * @return 500，訊息不對外揭露細節
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception e) {
        log.error("Unexpected error occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("系統發生錯誤，請稍後再試"));
    }

    /**
     * 路徑來自使用者輸入，寫 log 前去掉換行，避免偽造 log 內容
     *
     * @param value 要寫進 log 的字串
     * @return 換行改成底線後的字串
     */
    private static String sanitize(String value) {
        return value == null ? null : value.replaceAll("[\r\n]", "_");
    }
}
