package com.example.cocktail.controller;

import com.example.cocktail.model.Combination;
import com.example.cocktail.model.Recipe;
import com.example.cocktail.repository.CombinationRepository;
import com.example.cocktail.repository.RecipeRepository;
import com.example.cocktail.service.CocktailSelectorService;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 後台組合相關端點的請求／回應契約：分配組合、新增組合、列出組合，以及依組合查詢的篩選器與詳細頁
 * 會寫資料，整個測試包在交易裡結束後回滾，不影響其他測試
 *
 * @author Harry
 * @since 2026-10-02
 * 異動歷史：2026-10-02 Harry 新建
 * 　　　　　2026-10-02 Harry 補沒有分配組合的酒譜在列表與詳細頁的行為
 * 　　　　　2026-10-02 Harry 組合 Entity 更名為 Combination
 * 　　　　　2026-10-02 Harry API 路徑改為 REST 風格，新增組合改回傳 201，補篩選器與詳細頁的端點測試
 * 　　　　　2026-10-02 Harry 補新增組合欄位空白時回 400 的測試
 * 　　　　　2026-10-02 Harry 補組合列表與後台對照表依 ID 排序的測試
 * 　　　　　2026-10-02 Harry 一組組合只能分配給一杯酒，既有分配測試改用沒人用的組合
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminApiTest {

    private static final String RECIPE_COMBINATIONS = "/lastwine/recipe-combinations";
    private static final String COMBINATIONS = "/lastwine/combinations";

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
        int first = freeCombination("A").getCombinationId();
        int second = freeCombination("B").getCombinationId();

        assign(assignment(recipeId, first)).andExpect(status().isOk());
        assertEquals(first, cocktailSelectorService.getCocktailDetail(recipeId).combinationId());

        // 改配另一組要取代原本的，不能留下兩筆
        assign(assignment(recipeId, second)).andExpect(status().isOk());
        assertEquals(second, cocktailSelectorService.getCocktailDetail(recipeId).combinationId());
    }

    @Test
    void assignCombinationsWithMissingIdReturns400AndChangesNothing() throws Exception {
        Integer recipeId = recipeRepository.findAll().get(0).getRecipeId();
        int original = freeCombination("A").getCombinationId();
        int other = freeCombination("B").getCombinationId();
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
            mockMvc.perform(patch(RECIPE_COMBINATIONS).contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").isNotEmpty());
        }

        // 整批被拒，連同一批裡合法的那筆也不能生效
        assertEquals(original, cocktailSelectorService.getCocktailDetail(recipeId).combinationId());
    }

    @Test
    void assigningCombinationHeldByAnotherRecipeIsRejected() throws Exception {
        List<Recipe> recipes = recipeRepository.findAll();
        Integer first = recipes.get(0).getRecipeId();
        Integer second = recipes.get(1).getRecipeId();
        int firstCombination = currentCombination(first);
        int secondCombination = currentCombination(second);

        assign(assignment(first, secondCombination))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(startsWith("同一組組合只能分配給一杯酒")));

        assertEquals(firstCombination, currentCombination(first));
        assertEquals(secondCombination, currentCombination(second));
    }

    @Test
    void swappingCombinationsInOneBatchIsAllowed() throws Exception {
        List<Recipe> recipes = recipeRepository.findAll();
        Integer first = recipes.get(0).getRecipeId();
        Integer second = recipes.get(1).getRecipeId();
        int firstCombination = currentCombination(first);
        int secondCombination = currentCombination(second);

        // 以套用後的結果判斷，同一批互換不算重複
        assign(assignment(first, secondCombination) + ", " + assignment(second, firstCombination))
                .andExpect(status().isOk());

        assertEquals(secondCombination, currentCombination(first));
        assertEquals(firstCombination, currentCombination(second));
    }

    @Test
    void sameCombinationTwiceInOneBatchIsRejected() throws Exception {
        List<Recipe> recipes = recipeRepository.findAll();
        Integer first = recipes.get(0).getRecipeId();
        Integer second = recipes.get(1).getRecipeId();
        int firstCombination = currentCombination(first);
        int free = freeCombination("A").getCombinationId();

        assign(assignment(first, free) + ", " + assignment(second, free))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(startsWith("同一組組合只能分配給一杯酒")));

        // 整批被拒，第一筆也不能生效
        assertEquals(firstCombination, currentCombination(first));
    }

    @Test
    void recipeWithoutCombinationIsListedButHasNoDetailUntilAssigned() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setEnTitle("No Combination Cocktail");
        recipe.setZhTitle("尚未分配組合的測試調酒");
        recipe.setMethod("尚未分配組合");
        Integer recipeId = recipeRepository.saveAndFlush(recipe).getRecipeId();
        int combinationId = freeCombination("A").getCombinationId();

        // 後台列表是 LEFT JOIN，沒分配組合的酒譜也要列出，組合欄位為 null
        mockMvc.perform(get(RECIPE_COMBINATIONS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value((int) recipeRepository.count()))
                .andExpect(jsonPath("$[?(@.recipeId == %d && @.combinationId == null)]", recipeId).isNotEmpty());
        // 篩選器的詳細資料只看有組合的酒譜
        mockMvc.perform(get("/lastwine/recipes/" + recipeId + "/detail"))
                .andExpect(status().isNotFound());

        assign(assignment(recipeId, combinationId)).andExpect(status().isOk());
        assertEquals(combinationId, cocktailSelectorService.getCocktailDetail(recipeId).combinationId());
        mockMvc.perform(get("/lastwine/recipes/" + recipeId + "/detail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipeId").value(recipeId))
                .andExpect(jsonPath("$.combinationId").value(combinationId))
                .andExpect(jsonPath("$.materials").isArray());
    }

    @Test
    void selectorReturnsRecipesMatchingAllFourDimensions() throws Exception {
        Integer recipeId = recipeRepository.findAll().get(0).getRecipeId();
        Combination combination = freeCombination("A");
        assign(assignment(recipeId, combination.getCombinationId())).andExpect(status().isOk());

        mockMvc.perform(get("/lastwine/selector")
                        .param("mood", combination.getMood())
                        .param("taste", combination.getTaste())
                        .param("tone", combination.getTone())
                        .param("drunk", combination.getDrunk()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.recipeId == %d)]", recipeId).isNotEmpty());
        // 少帶任一個條件就不會有組合符合，跟改版前 body 缺欄位的行為一致
        mockMvc.perform(get("/lastwine/selector")
                        .param("mood", combination.getMood())
                        .param("taste", combination.getTaste())
                        .param("tone", combination.getTone()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void addCombinationThenListReturnsDtoFields() throws Exception {
        long before = combinationRepository.count();
        String body = "{\"mood\": \"測試心情\", \"taste\": \"測試口味\", \"tone\": \"測試冷暖\", \"drunk\": \"測試醉度\"}";

        mockMvc.perform(post(COMBINATIONS).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mockMvc.perform(post(COMBINATIONS).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(COMBINATIONS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value((int) (before + 1)))
                .andExpect(jsonPath("$[0].combinationId").isNumber())
                .andExpect(jsonPath("$[0].mood").isNotEmpty())
                .andExpect(jsonPath("$[0].taste").isNotEmpty())
                .andExpect(jsonPath("$[0].tone").isNotEmpty())
                .andExpect(jsonPath("$[0].drunk").isNotEmpty());
    }

    @Test
    void addCombinationWithBlankFieldsIsRejected() throws Exception {
        long before = combinationRepository.count();

        for (String body : List.of("{}",
                "{\"mood\": \"測試心情\", \"taste\": \"測試口味\", \"tone\": \"測試冷暖\"}",
                "{\"mood\": \"測試心情\", \"taste\": \"  \", \"tone\": \"測試冷暖\", \"drunk\": \"測試醉度\"}")) {
            mockMvc.perform(post(COMBINATIONS).contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("組合的四個欄位都必須填寫"));
        }

        assertEquals(before, combinationRepository.count());
    }

    @Test
    void combinationListAndAdminTableAreSortedById() throws Exception {
        assertSortedById(get(COMBINATIONS), "combinationId");
        assertSortedById(get(RECIPE_COMBINATIONS), "recipeId");
    }

    /**
     * @param request 回傳 JSON 陣列的請求
     * @param idField 要檢查排序的 ID 欄位
     */
    private void assertSortedById(MockHttpServletRequestBuilder request, String idField) throws Exception {
        String json = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<Integer> ids = JsonPath.read(json, "$[*]." + idField);
        assertFalse(ids.isEmpty());
        assertEquals(ids.stream().sorted().toList(), ids);
    }

    /**
     * seed 的 48 組組合都已分配出去，測試要用的組合另外建，保證沒有被任何酒譜占用
     *
     * @param tag 讓四個欄位跟既有組合不重複的後綴
     * @return 已存檔、沒有被任何酒譜使用的組合
     */
    private Combination freeCombination(String tag) {
        Combination combination = new Combination();
        combination.setMood("測試心情" + tag);
        combination.setTaste("測試口味" + tag);
        combination.setTone("測試冷暖" + tag);
        combination.setDrunk("測試醉度" + tag);
        return combinationRepository.saveAndFlush(combination);
    }

    /**
     * @param recipeId 酒譜 ID
     * @return 該酒譜目前的組合 ID
     */
    private int currentCombination(Integer recipeId) {
        return cocktailSelectorService.getCocktailDetail(recipeId).combinationId();
    }

    /**
     * @param assignments 一或多筆以逗號串接的分配 JSON
     * @return 送出整批分配的結果
     */
    private ResultActions assign(String assignments) throws Exception {
        return mockMvc.perform(patch(RECIPE_COMBINATIONS)
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
