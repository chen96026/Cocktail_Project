package com.example.cocktail.exception;

import com.example.cocktail.service.CloudinaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 請求本身格式有問題時要回 400 與可讀的訊息，不能落到 catch-all 變成 500
 *
 * @author Harry
 * @since 2026-10-02
 * 異動歷史：2026-10-02 Harry 新建
 */
@SpringBootTest
@AutoConfigureMockMvc
class ApiExceptionHandlerTest {

    private static final String RECIPE_JSON = """
            {"enTitle":"Bad Request Test","zhTitle":"格式錯誤測試","method":"測試",
             "baseWines":["Gin"],"materials":[{"materialName":"琴酒","materialQuantity":"30ml"}]}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CloudinaryService cloudinaryService;

    @Test
    void pathVariableTypeMismatchReturns400() throws Exception {
        expectBadRequest(get("/lastwine/recipes/abc"), "參數 recipeId 格式錯誤");
        expectBadRequest(get("/lastwine/recipes/abc/detail"), "參數 recipeId 格式錯誤");
    }

    @Test
    void malformedJsonBodyReturns400() throws Exception {
        expectBadRequest(patch("/lastwine/recipe-combinations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("not json"), "請求內容格式錯誤");
        expectBadRequest(patch("/lastwine/recipe-combinations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[{\"recipeId\":\"abc\",\"combinationId\":1}]"), "請求內容格式錯誤");
        expectBadRequest(post("/lastwine/combinations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"mood\":"), "請求內容格式錯誤");
    }

    @Test
    void missingMultipartPartReturns400WithoutUploading() throws Exception {
        MockMultipartFile recipe = new MockMultipartFile("recipe", "", MediaType.APPLICATION_JSON_VALUE,
                RECIPE_JSON.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile image = new MockMultipartFile("image", "a.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[]{1});

        expectBadRequest(multipart("/lastwine/recipes").file(recipe), "缺少必要欄位：image");
        expectBadRequest(multipart("/lastwine/recipes").file(image), "缺少必要欄位：recipe");

        verify(cloudinaryService, never()).uploadImage(any());
    }

    /**
     * @param request         要送出的請求
     * @param expectedMessage 預期的錯誤訊息
     */
    private void expectBadRequest(RequestBuilder request, String expectedMessage) throws Exception {
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }
}
