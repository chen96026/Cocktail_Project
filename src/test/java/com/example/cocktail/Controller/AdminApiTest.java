package com.example.cocktail.Controller;

import com.example.cocktail.Model.Combinations;
import com.example.cocktail.Model.Recipe;
import com.example.cocktail.Repository.CombinationRepository;
import com.example.cocktail.Repository.RecipeRepository;
import com.example.cocktail.Service.CocktailSelectorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 後台組合相關端點的請求／回應契約：分配組合、新增組合、列出組合
 * 會寫資料，整個測試包在交易裡結束後回滾，不影響其他測試
 *
 * @author Harry
 * @since 2026-10-02
 * 異動歷史：2026-10-02 Harry 新建
 * 　　　　　2026-10-02 Harry 補沒有分配組合的酒譜在列表與詳細頁的行為
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminApiTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private CombinationRepository combinationRepository;
    @Autowired
    private CocktailSelectorService cocktailSelectorService;

    @Test
    void assignCombinationsReplacesRecipeCombination() throws Exception {
        Integer recipeId = recipeRepository.findAll().get(0).getRecipeId();
        List<Combinations> combinations = combinationRepository.findAll();
        int first = combinations.get(0).getCombinationId();
        int second = combinations.get(1).getCombinationId();

        assign(assignment(recipeId, first)).andExpect(status().isOk());
        assertEquals(first, cocktailSelectorService.getCocktailDetail(recipeId).combinationId());

        // 改配另一組要取代原本的，不能留下兩筆
        assign(assignment(recipeId, second)).andExpect(status().isOk());
        assertEquals(second, cocktailSelectorService.getCocktailDetail(recipeId).combinationId());
    }

    @Test
    void assignCombinationsWithMissingIdReturns400AndChangesNothing() throws Exception {
        Integer recipeId = recipeRepository.findAll().get(0).getRecipeId();
        List<Combinations> combinations = combinationRepository.findAll();
        int original = combinations.get(0).getCombinationId();
        int other = combinations.get(1).getCombinationId();
        assign(assignment(recipeId, original)).andExpect(status().isOk());

        List<String> invalidBodies = List.of(
                "[" + assignment(recipeId, other) + ", {\"recipeId\": null, \"combinationId\": " + other + "}]",
                "[{\"recipeId\": " + recipeId + ", \"combinationId\": null}]",
                "[{\"recipeId\": " + recipeId + "}]",
                "[null]",
                // 改版前前端送的 Entity 形狀，新的 record 讀不到 ID
                "[{\"fkRecipeId\": {\"recipeId\": " + recipeId + "}, \"fkCombinationId\": {\"combinationId\": " + other + "}}]"
        );
        for (String body : invalidBodies) {
            mockMvc.perform(post("/lastwine/assignCombinations").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").isNotEmpty());
        }

        // 整批被拒，連同一批裡合法的那筆也不能生效
        assertEquals(original, cocktailSelectorService.getCocktailDetail(recipeId).combinationId());
    }

    @Test
    void recipeWithoutCombinationIsListedButHasNoDetailUntilAssigned() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setEnTitle("No Combination Cocktail");
        recipe.setZhTitle("尚未分配組合的測試調酒");
        recipe.setMethod("尚未分配組合");
        Integer recipeId = recipeRepository.saveAndFlush(recipe).getRecipeId();
        int combinationId = combinationRepository.findAll().get(0).getCombinationId();

        // 後台列表是 LEFT JOIN，沒分配組合的酒譜也要列出，組合欄位為 null
        mockMvc.perform(get("/lastwine/allCombinations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value((int) recipeRepository.count()))
                .andExpect(jsonPath("$[?(@.recipeId == %d && @.combinationId == null)]", recipeId).isNotEmpty());
        // 篩選器的詳細資料只看有組合的酒譜
        mockMvc.perform(get("/lastwine/getCocktailDetail/" + recipeId))
                .andExpect(status().isNotFound());

        assign(assignment(recipeId, combinationId)).andExpect(status().isOk());
        assertEquals(combinationId, cocktailSelectorService.getCocktailDetail(recipeId).combinationId());
    }

    @Test
    void addCombinationThenListReturnsDtoFields() throws Exception {
        long before = combinationRepository.count();
        String body = "{\"mood\": \"測試心情\", \"taste\": \"測試口味\", \"tone\": \"測試冷暖\", \"drunk\": \"測試醉度\"}";

        mockMvc.perform(post("/lastwine/addFourCombination").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
        mockMvc.perform(post("/lastwine/addFourCombination").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/lastwine/getAllCombinations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value((int) (before + 1)))
                .andExpect(jsonPath("$[0].combinationId").isNumber())
                .andExpect(jsonPath("$[0].mood").isNotEmpty())
                .andExpect(jsonPath("$[0].taste").isNotEmpty())
                .andExpect(jsonPath("$[0].tone").isNotEmpty())
                .andExpect(jsonPath("$[0].drunk").isNotEmpty());
    }

    /**
     * @param assignments 一或多筆以逗號串接的分配 JSON
     * @return 送出 assignCombinations 的結果
     */
    private ResultActions assign(String assignments) throws Exception {
        return mockMvc.perform(post("/lastwine/assignCombinations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[" + assignments + "]"));
    }

    /**
     * @param recipeId      酒譜 ID
     * @param combinationId 組合 ID
     * @return 單筆分配的 JSON
     */
    private static String assignment(Integer recipeId, int combinationId) {
        return "{\"recipeId\": " + recipeId + ", \"combinationId\": " + combinationId + "}";
    }
}
