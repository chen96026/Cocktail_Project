package com.example.cocktail.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.nio.charset.StandardCharsets;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 酒譜查詢端點的 JSON 契約：findRecipeId、search 跟 getAllRecipe 回同一個形狀
 * 刻意不加 @Transactional，open-in-view 關閉後若有交易外 lazy load 會直接失敗
 *
 * @author Harry
 * @since 2026-10-02
 * 異動歷史：2026-10-02 Harry 新建
 */
@SpringBootTest
@AutoConfigureMockMvc
class RecipeApiTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findRecipeIdAndSearchReturnSameShapeAsGetAllRecipe() throws Exception {
        JsonNode expected = StreamSupport.stream(getJson(get("/lastwine/getAllRecipe")).spliterator(), false)
                .filter(recipe -> !recipe.get("baseWines").isEmpty())
                .findFirst()
                .orElseThrow();
        // baseWines 是基酒名稱字串陣列，不是 {baseWineId, name} 物件
        assertTrue(expected.get("baseWines").get(0).isTextual());
        assertFalse(expected.get("materials").get(0).has("materialId"));

        assertEquals(expected, getJson(get("/lastwine/findRecipeId/" + expected.get("recipeId").asInt())));

        JsonNode searched = getJson(get("/lastwine/search").param("keyword", expected.get("enTitle").asText()));
        assertTrue(StreamSupport.stream(searched.spliterator(), false).anyMatch(expected::equals));
    }

    @Test
    void findRecipeIdNotFoundReturns404() throws Exception {
        mockMvc.perform(get("/lastwine/findRecipeId/-1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    /**
     * @param request 要送出的請求
     * @return 200 回應解析後的 JSON
     */
    private JsonNode getJson(RequestBuilder request) throws Exception {
        String body = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readTree(body);
    }
}
