package com.example.cocktail.controller;

import com.example.cocktail.repository.RecipeRepository;
import com.example.cocktail.service.CloudinaryService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 酒譜查詢端點的 JSON 契約：單筆、關鍵字搜尋跟列表回同一個形狀
 * 列表的基酒、關鍵字篩選組合，新增／更新／刪除的狀態碼，以及改版前的舊路徑一律回 404
 * 類別刻意不加 @Transactional，open-in-view 關閉後若有交易外 lazy load 會直接失敗；會寫資料的測試個別加上
 * Cloudinary 一律 mock，不打真的雲端
 *
 * @author Harry
 * @since 2026-10-02
 * 異動歷史：2026-10-02 Harry 新建
 * 　　　　　2026-10-02 Harry API 路徑改為 REST 風格，補列表篩選組合、批次載入與舊路徑 404 測試
 */
@SpringBootTest
@AutoConfigureMockMvc
class RecipeApiTest {

    private static final String RECIPES = "/lastwine/recipes";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Value("${spring.jpa.properties.hibernate.default_batch_fetch_size}")
    private int batchFetchSize;

    @MockitoBean
    private CloudinaryService cloudinaryService;

    @Test
    void getRecipeAndKeywordSearchReturnSameShapeAsList() throws Exception {
        JsonNode expected = list(get(RECIPES)).stream()
                .filter(recipe -> !recipe.get("baseWines").isEmpty())
                .findFirst()
                .orElseThrow();
        // baseWines 是基酒名稱字串陣列，不是 {baseWineId, name} 物件
        assertTrue(expected.get("baseWines").get(0).isTextual());
        assertFalse(expected.get("materials").get(0).has("materialId"));

        assertEquals(expected, getJson(get(RECIPES + "/" + expected.get("recipeId").asInt())));

        List<JsonNode> searched = list(get(RECIPES).param("keyword", expected.get("enTitle").asText()));
        assertTrue(searched.contains(expected));
    }

    @Test
    void getRecipeNotFoundReturns404() throws Exception {
        mockMvc.perform(get(RECIPES + "/-1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void listWithoutFilterOrWithAllReturnsEveryRecipe() throws Exception {
        Set<Integer> all = ids(list(get(RECIPES)));
        assertEquals(recipeRepository.count(), all.size());

        assertEquals(all, ids(list(get(RECIPES).param("baseWine", "All"))));
        assertEquals(all, ids(list(get(RECIPES).param("baseWine", "All", "Vodka"))));
        // 空白的參數視同沒帶
        assertEquals(all, ids(list(get(RECIPES).param("baseWine", "").param("keyword", "  "))));
        // All 只代表不篩基酒，關鍵字照樣生效
        assertEquals(ids(list(get(RECIPES).param("keyword", "blue"))),
                ids(list(get(RECIPES).param("baseWine", "All").param("keyword", "blue"))));
    }

    @Test
    void listByBaseWineRequiresEverySelectedBaseWine() throws Exception {
        List<String> selected = List.of("Vodka", "Others");
        List<JsonNode> all = list(get(RECIPES));
        Set<Integer> expected = ids(all, recipe -> baseWines(recipe).containsAll(selected));
        // 資料要能分出「全部符合」與「任一符合」，否則這個測試驗不出語意
        assertFalse(expected.isEmpty());
        assertTrue(expected.size() < ids(all, recipe -> baseWines(recipe).stream().anyMatch(selected::contains)).size());

        assertEquals(expected, ids(list(get(RECIPES).param("baseWine", "Vodka,Others"))));
        assertEquals(expected, ids(list(get(RECIPES).param("baseWine", "Vodka", "Others"))));
        // 重複的基酒不能讓 HAVING 的數量對不上
        assertEquals(expected, ids(list(get(RECIPES).param("baseWine", "Vodka,Others", "Vodka"))));
    }

    @Test
    void listByKeywordMatchesEitherTitleIgnoringCase() throws Exception {
        Set<Integer> expected = ids(list(get(RECIPES)), recipe ->
                recipe.get("enTitle").asText().toLowerCase().contains("blue")
                        || recipe.get("zhTitle").asText().contains("blue"));
        assertFalse(expected.isEmpty());

        assertEquals(expected, ids(list(get(RECIPES).param("keyword", "BLUE"))));
        assertEquals(expected, ids(list(get(RECIPES).param("keyword", " blue "))));
    }

    @Test
    void listByBaseWineAndKeywordReturnsIntersection() throws Exception {
        Set<Integer> byBaseWine = ids(list(get(RECIPES).param("baseWine", "Rum")));
        Set<Integer> byKeyword = ids(list(get(RECIPES).param("keyword", "blue")));
        Set<Integer> expected = new HashSet<>(byBaseWine);
        expected.retainAll(byKeyword);
        // 交集要比兩邊都小，才證明兩個條件都有生效
        assertFalse(expected.isEmpty());
        assertTrue(expected.size() < byBaseWine.size() && expected.size() < byKeyword.size());

        assertEquals(expected, ids(list(get(RECIPES).param("baseWine", "Rum").param("keyword", "BLUE"))));
    }

    @Test
    void listLoadsBaseWinesAndMaterialsInBatches() throws Exception {
        List<MockHttpServletRequestBuilder> requests = List.of(
                get(RECIPES),
                get(RECIPES).param("baseWine", "Vodka"),
                get(RECIPES).param("keyword", "a"),
                get(RECIPES).param("baseWine", "Vodka").param("keyword", "a"));
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        try {
            for (MockHttpServletRequestBuilder request : requests) {
                statistics.clear();
                int size = list(request).size();
                // 主查詢 1 次，基酒、材料各依 batch size 分批，不會隨筆數一筆一筆查
                long maxStatements = 1 + 2L * Math.ceilDiv(size, batchFetchSize);
                long statements = statistics.getPrepareStatementCount();
                // 至少要有主查詢，確認統計真的有在計數
                assertTrue(statements >= 1 && statements <= maxStatements,
                        "statements=" + statements + ", recipes=" + size);
            }
        } finally {
            statistics.setStatisticsEnabled(false);
        }
    }

    @Test
    @Transactional
    void addUpdateDeleteRecipeUseRestStatusCodes() throws Exception {
        when(cloudinaryService.uploadImage(any())).thenReturn("https://example.com/new.jpg");

        String location = mockMvc.perform(multipart(RECIPES)
                        .file(recipePart("{\"enTitle\": \"Rest Api Cocktail\", \"zhTitle\": \"REST 測試調酒\", "
                                + "\"method\": \"測試作法\", \"baseWines\": [\"Gin\"], "
                                + "\"materials\": [{\"materialName\": \"琴酒\", \"materialQuantity\": \"30ml\"}]}"))
                        .file(new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[]{1, 2, 3})))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, matchesPattern(".*/lastwine/recipes/\\d+$")))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andReturn().getResponse().getHeader(HttpHeaders.LOCATION);
        // Location 要指到真的查得到的酒譜
        String recipeUrl = location.substring(location.indexOf(RECIPES));
        assertEquals("Rest Api Cocktail", getJson(get(recipeUrl)).get("enTitle").asText());

        // 更新不帶圖片時保留原圖
        mockMvc.perform(multipart(HttpMethod.PUT, recipeUrl)
                        .file(recipePart("{\"enTitle\": \"Rest Api Cocktail\", \"zhTitle\": \"REST 測試調酒\", "
                                + "\"method\": \"改過的作法\", \"baseWines\": [\"Gin\"], \"materials\": []}")))
                .andExpect(status().isOk());
        JsonNode updated = getJson(get(recipeUrl));
        assertEquals("改過的作法", updated.get("method").asText());
        assertEquals("https://example.com/new.jpg", updated.get("image").asText());

        mockMvc.perform(delete(recipeUrl)).andExpect(status().isOk());
        mockMvc.perform(get(recipeUrl)).andExpect(status().isNotFound());
    }

    @Test
    void legacyPathsReturn404() throws Exception {
        List<MockHttpServletRequestBuilder> legacyRequests = List.of(
                get("/lastwine/getAllRecipe"),
                get("/lastwine/getRecipesByBaseWine").param("baseWine", "Vodka"),
                get("/lastwine/search").param("keyword", "blue"),
                get("/lastwine/findRecipeId/1"),
                post("/lastwine/addRecipe"),
                put("/lastwine/updateRecipe/-1"),
                delete("/lastwine/deleteRecipe/-1"),
                get("/lastwine/getAllCombinations"),
                post("/lastwine/addFourCombination"),
                get("/lastwine/allCombinations"),
                post("/lastwine/assignCombinations"),
                post("/lastwine/getCocktailSelector"),
                get("/lastwine/getCocktailDetail/1"));
        for (MockHttpServletRequestBuilder request : legacyRequests) {
            mockMvc.perform(request)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").isNotEmpty());
        }
    }

    @Test
    void unsupportedMethodReturns405WithAllowHeader() throws Exception {
        mockMvc.perform(patch(RECIPES))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().exists(HttpHeaders.ALLOW))
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

    /**
     * @param request 回傳酒譜陣列的請求
     * @return 陣列裡的每一筆酒譜
     */
    private List<JsonNode> list(RequestBuilder request) throws Exception {
        return StreamSupport.stream(getJson(request).spliterator(), false).toList();
    }

    /**
     * @param json 酒譜內容的 JSON
     * @return multipart 的 recipe part
     */
    private static MockMultipartFile recipePart(String json) {
        return new MockMultipartFile("recipe", "", MediaType.APPLICATION_JSON_VALUE, json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * @param recipes 酒譜列表
     * @return 酒譜 ID 集合
     */
    private static Set<Integer> ids(List<JsonNode> recipes) {
        return ids(recipes, recipe -> true);
    }

    /**
     * @param recipes 酒譜列表
     * @param filter  要留下的酒譜
     * @return 符合條件的酒譜 ID 集合
     */
    private static Set<Integer> ids(List<JsonNode> recipes, Predicate<JsonNode> filter) {
        return recipes.stream()
                .filter(filter)
                .map(recipe -> recipe.get("recipeId").asInt())
                .collect(Collectors.toSet());
    }

    /**
     * @param recipe 單筆酒譜
     * @return 該酒譜的基酒名稱
     */
    private static List<String> baseWines(JsonNode recipe) {
        return StreamSupport.stream(recipe.get("baseWines").spliterator(), false)
                .map(JsonNode::asText)
                .toList();
    }
}
