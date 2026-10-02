package com.example.cocktail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 確認 springdoc 能在目前的 Spring Boot 版本產出 OpenAPI 文件
 * springdoc 版本跟 Spring Framework 不相容時 /v3/api-docs 會回 500
 *
 * @author Harry
 * @since 2026-10-02
 * 異動歷史：2026-10-02 Harry 新建
 */
@SpringBootTest
@AutoConfigureMockMvc
class SwaggerApiDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void apiDocsReturnsOk() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists());
    }
}
