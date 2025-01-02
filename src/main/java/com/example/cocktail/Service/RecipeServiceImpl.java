package com.example.cocktail.Service;

import com.example.cocktail.DTO.CockTailDetailDTO;
import com.example.cocktail.DTO.RecipeDTO;
import com.example.cocktail.JwtUtil;
import com.example.cocktail.Model.BaseWine;
import com.example.cocktail.Model.Material;
import com.example.cocktail.Model.Member;
import com.example.cocktail.Model.Recipe;
import com.example.cocktail.Repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.cloudinary.AccessControlRule.AccessType.token;

@Service
public class RecipeServiceImpl implements RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BaseWineRepository baseWineRepository;
    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private CombinationRepository combinationRepository;

    @Override
    public List<RecipeDTO> getAllRecipes() {
        List<Recipe> recipes = recipeRepository.findAllWithBaseWines();
        return recipes.stream()
                .map(RecipeDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecipeDTO> getRecipesByBaseWine(List<String> baseWineList) {
        if (baseWineList.contains("All")) {
            return recipeRepository.findAll()
                    .stream()
                    .map(RecipeDTO::new)
                    .collect(Collectors.toList());
        }
        List<Recipe> recipes = recipeRepository.findByMatchingBaseWines(baseWineList, baseWineList.size());
        // 將 Recipe 轉換為 RecipeDTO
        return recipes.stream()
                .map(RecipeDTO::new) // 使用 RecipeDTO 的建構函數
                .collect(Collectors.toList());
    }

    @Override
    public void addRecipe(String enTitle, String zhTitle, List<String> baseWineList, List<String> materials, String method, MultipartFile image, String token) {

        try {
            // 從Token中獲取會員帳號
            String account = jwtUtil.validateToken(token).getSubject();
            Member member = memberRepository.findByAccount(account);
            if (member == null) {
                throw new RuntimeException("會員不存在！");
            }

            // 上傳圖片，獲取圖片 URL
            String imageUrl = cloudinaryService.uploadImage(image);

            // 創建Recipe 對象
            Recipe recipe = new Recipe();
            recipe.setEn_title(enTitle);
            recipe.setZh_title(zhTitle);
            recipe.setMethod(method);
            recipe.setImage(imageUrl);
            recipe.setMember(member);

            List<BaseWine> baseWines = baseWineList.stream()
                    .map(name -> baseWineRepository.findByName(name)
                            .orElseGet(() -> {
                                BaseWine newWine = new BaseWine();
                                newWine.setName(name);
                                return baseWineRepository.save(newWine);
                            }))
                    .collect(Collectors.toList());
            recipe.setBaseWines(baseWines);

            // 處理材料
            List<Material> materialList = materials.stream()
                    .map(material -> {
                        String[] parts = material.split(":");
                        if (parts.length == 2) {
                            Material m = new Material();
                            m.setMaterial_name(parts[0]);
                            m.setMaterial_quantity(parts[1]);
                            m.setRecipe(recipe);
                            return m;
                        } else {
                            throw new IllegalArgumentException("材料格式錯誤，應該為 '材料名稱:數量'");
                        }
                    })
                    .collect(Collectors.toList());
            recipe.setMaterials(materialList);

            recipeRepository.save(recipe);

        } catch (Exception e) {
            throw new RuntimeException("添加酒譜失敗", e);
        }
    }

    // 重新檢查
    @Override
    public void updateRecipe(Integer recipeId, String enTitle, String zhTitle, String method, MultipartFile image, List<String> baseWineList, List<String> materials, String token) {
        try {
            if (!memberService.isAdmin(token)) {
                String account = jwtUtil.validateToken(token).getSubject();
                Recipe existingRecipe = recipeRepository.findByRecipeId(recipeId);
                if (existingRecipe == null) {
                    throw new RuntimeException("找不到對應的酒譜，ID: " + recipeId);
                }
                if (!"ADMIN".equals(jwtUtil.validateToken(token).get("role", String.class)) &&
                        !existingRecipe.getMember().getAccount().equals(account)) {
                    throw new RuntimeException("無權限更新該酒譜");
                }
            }
            // 確認是否有對應的 Recipe
            Recipe existingRecipe = recipeRepository.findByRecipeId(recipeId);
            if (existingRecipe == null) {
                throw new RuntimeException("找不到對應的酒譜，ID: " + recipeId);
            }
            // 檢查權限
            String ownerAccount = existingRecipe.getMember().getAccount();
            memberService.checkEditPermission(token, ownerAccount);
            existingRecipe.setEn_title(enTitle);
            existingRecipe.setZh_title(zhTitle);
            existingRecipe.setMethod(method);
            // 處理圖片上傳
            if (image != null && !image.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(image);
                existingRecipe.setImage(imageUrl);
            } else {
                existingRecipe.setImage(existingRecipe.getImage());
            }
            // 更新基酒關聯
            List<BaseWine> updatedBaseWines = baseWineList.stream()
                    .map(name -> baseWineRepository.findByName(name)
                            .orElseGet(() -> {
                                BaseWine newWine = new BaseWine();
                                newWine.setName(name);
                                return baseWineRepository.save(newWine);
                            }))
                    .collect(Collectors.toList());
            existingRecipe.setBaseWines(updatedBaseWines);

            List<Material> updatedMaterials=materials.stream().map(
                    material->{
                        String[] parts = material.split(":");
                        if(parts.length==2){
                            Material newmaterial = new Material();
                            newmaterial.setMaterial_name(parts[0]);
                            newmaterial.setMaterial_quantity(parts[1]);
                            newmaterial.setRecipe(existingRecipe);
                            return newmaterial;
                        }else{
                            throw new IllegalArgumentException("材料格式錯誤，應該為 '材料名稱:數量'");
                        }
                    })
                    .collect(Collectors.toList());

            //清空舊的材料並設置新的材料
            existingRecipe.getMaterials().clear();
            existingRecipe.getMaterials().addAll(updatedMaterials);

            recipeRepository.save(existingRecipe);
        } catch (Exception e) {
            throw new RuntimeException("更新酒譜失敗！", e);
        }
    }

    @Override
    public void deleteRecipe(Integer recipe_id, String token) {
        // 從 Token 中提取用戶帳號
        String account = jwtUtil.validateToken(token).getSubject();
        // 確認食譜是否存在
        Recipe recipe = recipeRepository.findById(recipe_id).orElseThrow(() -> new RuntimeException("找不到該酒譜！"));
        // 如果不是管理員，檢查是否為該食譜的擁有者
        if (!memberService.isAdmin(token) && !recipe.getMember().getAccount().equals(account)) {
            throw new RuntimeException("無權限刪除此酒譜！");
        }
        recipeRepository.deleteById(recipe_id);
    }

    @Override
    public Recipe getRecipe(Integer recipe_id) {
        return recipeRepository.findByRecipeId(recipe_id);
    }

    @Override
    public List<Recipe> searchRecipes(String keyword) {
        return recipeRepository.searchByKeyword(keyword);
    }

}
