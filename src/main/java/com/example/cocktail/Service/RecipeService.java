package com.example.cocktail.Service;

import com.example.cocktail.DTO.CockTailDetailDTO;
import com.example.cocktail.DTO.RecipeDTO;
import com.example.cocktail.Model.Material;
import com.example.cocktail.Model.Recipe;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RecipeService {

    // 取得所有有基酒的調酒(全部調酒)
    public List<RecipeDTO> getAllRecipes();

    public List<RecipeDTO> getRecipesByBaseWine(List<String> baseWineList);

    public void addRecipe(String enTitle, String zhTitle, List<String> baseWineList, List<String> materials, String method, MultipartFile image, String token);

    public void updateRecipe(Integer recipeId, String enTitle, String zhTitle, String method, MultipartFile image, List<String> baseWineList, List<String> materials, String token);

    public void deleteRecipe(Integer recipe_id, String token);

    public Recipe getRecipe(Integer recipe_id);

    public List<Recipe> searchRecipes(String keyword);

}

