package com.example.cocktail.Repository;

import com.example.cocktail.DTO.CockTailDetailDTO;
import com.example.cocktail.DTO.CocktailBasicDTO;
import com.example.cocktail.Model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {

    // 找到符合基酒的酒譜
    // size表示 baseWines 列表的大小，只有當某個 Recipe 包含的不同基酒名稱數量與提供的列表大小一致時，該 Recipe 才符合條件
    @Query("SELECT r FROM Recipe r JOIN r.baseWines b WHERE b.name IN :baseWines GROUP BY r HAVING COUNT(DISTINCT b.name) = :size")
    public List<Recipe> findByMatchingBaseWines(@Param("baseWines") List<String> baseWines, @Param("size") int size);

    // 找到該Id的酒譜
    public Recipe findByRecipeId(Integer recipe_id);

    // 新增前檢查重名：英文或中文名稱任一已存在即算重名
    public boolean existsByEnTitleOrZhTitle(String enTitle, String zhTitle);

    // 編輯前檢查重名：排除自己，只看英文或中文名稱是否跟別杯相同
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Recipe r " +
            "WHERE (r.enTitle = :enTitle OR r.zhTitle = :zhTitle) AND r.recipeId <> :recipeId")
    public boolean existsTitleInOtherRecipe(@Param("enTitle") String enTitle,
                                            @Param("zhTitle") String zhTitle,
                                            @Param("recipeId") Integer recipeId);

    // 篩選器：找出符合四維度組合的調酒
    @Query("SELECT new com.example.cocktail.DTO.CocktailBasicDTO(r.recipeId, r.zhTitle, r.enTitle, r.image) " +
            "FROM Recipe r " +
            "JOIN CombinationOption co ON r.recipeId = co.fkRecipeId.recipeId " +
            "JOIN Combinations c ON co.fkCombinationId.combinationId = c.combinationId " +
            "WHERE c.mood = :mood AND c.taste = :taste AND c.tone = :tone AND c.drunk = :drunk")
    public List<CocktailBasicDTO> findRecipesByCombination(
            @Param("mood") String mood,
            @Param("taste") String taste,
            @Param("tone") String tone,
            @Param("drunk") String drunk
    );

    // 篩選器 Modal 的詳細資料，材料另外查（最後是比對 Id，不是比對四種篩選）
    @Query("SELECT new com.example.cocktail.DTO.CockTailDetailDTO(" +
            "r.recipeId, r.image,r.enTitle, r.zhTitle, r.method, " +
            "c.combinationId, c.mood, c.taste, c.tone, c.drunk) " +
            "FROM Recipe r " +
            "JOIN CombinationOption co ON r.recipeId = co.fkRecipeId.recipeId " +
            "JOIN Combinations c ON co.fkCombinationId.combinationId = c.combinationId " +
            "WHERE r.recipeId = :recipeId")
    public CockTailDetailDTO findDetailByRecipeId(@Param("recipeId") Integer recipeId);

    // 搜尋功能，%適用為模糊的字串，like為模糊查詢，lower將文字都轉成小寫(以不區分大小寫)
    @Query("SELECT r FROM Recipe r WHERE LOWER(r.enTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.zhTitle) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    public List<Recipe> searchByKeyword(@Param("keyword") String keyword);

}
