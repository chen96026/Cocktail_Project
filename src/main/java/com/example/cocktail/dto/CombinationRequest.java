package com.example.cocktail.dto;

/**
 * 後台新增四維度組合的請求內容
 *
 * @author Harry
 * @since 2026-10-02
 * 異動歷史：2026-10-02 Harry 新建
 *
 * @param mood  心情
 * @param taste 口味
 * @param tone  冷暖
 * @param drunk 醉度
 */
public record CombinationRequest(String mood, String taste, String tone, String drunk) {
}
