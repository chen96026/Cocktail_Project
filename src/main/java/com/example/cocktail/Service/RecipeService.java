package com.example.cocktail.Service;

import com.example.cocktail.DTO.MaterialDTO;
import com.example.cocktail.DTO.RecipeDTO;
import com.example.cocktail.DTO.RecipeRequest;
import com.example.cocktail.JwtUtil;
import com.example.cocktail.Model.BaseWine;
import com.example.cocktail.Model.Material;
import com.example.cocktail.Model.Member;
import com.example.cocktail.Model.Recipe;
import com.example.cocktail.Repository.BaseWineRepository;
import com.example.cocktail.Repository.MemberRepository;
import com.example.cocktail.Repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final MemberRepository memberRepository;
    private final BaseWineRepository baseWineRepository;
    private final JwtUtil jwtUtil;
    private final CloudinaryService cloudinaryService;
    private final MemberService memberService;

    public RecipeService(RecipeRepository recipeRepository,
                         MemberRepository memberRepository,
                         BaseWineRepository baseWineRepository,
                         JwtUtil jwtUtil,
                         CloudinaryService cloudinaryService,
                         MemberService memberService) {
        this.recipeRepository = recipeRepository;
        this.memberRepository = memberRepository;
        this.baseWineRepository = baseWineRepository;
        this.jwtUtil = jwtUtil;
        this.cloudinaryService = cloudinaryService;
        this.memberService = memberService;
    }

    /**
     * @return 所有酒譜（含基酒）
     */
    public List<RecipeDTO> getAllRecipes() {
        return recipeRepository.findAllWithBaseWines().stream()
                .map(RecipeDTO::from)
                .toList();
    }

    /**
     * @param baseWineList 基酒名稱清單，含 All 時回傳全部
     * @return 符合基酒條件的酒譜
     */
    public List<RecipeDTO> getRecipesByBaseWine(List<String> baseWineList) {
        List<Recipe> recipes = baseWineList.contains("All")
                ? recipeRepository.findAll()
                : recipeRepository.findByMatchingBaseWines(baseWineList, baseWineList.size());
        return recipes.stream().map(RecipeDTO::from).toList();
    }

    /**
     * @param request 酒譜內容
     * @param image   酒譜圖片
     * @param token   JWT，用於取得建立者
     */
    public void addRecipe(RecipeRequest request, MultipartFile image, String token) {
        try {
            // 從Token中獲取會員帳號
            String account = jwtUtil.validateToken(token).getSubject();
            Member member = memberRepository.findByAccount(account);
            if (member == null) {
                throw new RuntimeException("會員不存在！");
            }

            Recipe recipe = new Recipe();
            recipe.setEn_title(request.enTitle());
            recipe.setZh_title(request.zhTitle());
            recipe.setMethod(request.method());
            // 上傳圖片，取得圖片 URL
            recipe.setImage(cloudinaryService.uploadImage(image));
            recipe.setMember(member);
            recipe.setBaseWines(resolveBaseWines(request.baseWines()));
            recipe.setMaterials(toMaterials(request.materials(), recipe));

            recipeRepository.save(recipe);
        } catch (Exception e) {
            throw new RuntimeException("添加酒譜失敗", e);
        }
    }

    /**
     * @param recipeId 酒譜 ID
     * @param request  酒譜內容
     * @param image    新圖片，未帶則保留原圖
     * @param token    JWT，用於權限檢查
     */
    public void updateRecipe(Integer recipeId, RecipeRequest request, MultipartFile image, String token) {
        try {
            Recipe existingRecipe = recipeRepository.findByRecipeId(recipeId);
            if (existingRecipe == null) {
                throw new RuntimeException("找不到對應的酒譜，ID: " + recipeId);
            }
            // 檢查權限：ADMIN 通過，USER 僅能改自己的酒譜
            memberService.checkEditPermission(token, existingRecipe.getMember().getAccount());

            existingRecipe.setEn_title(request.enTitle());
            existingRecipe.setZh_title(request.zhTitle());
            existingRecipe.setMethod(request.method());
            // 有帶新圖片才更新，否則保留原圖
            if (image != null && !image.isEmpty()) {
                existingRecipe.setImage(cloudinaryService.uploadImage(image));
            }
            existingRecipe.setBaseWines(resolveBaseWines(request.baseWines()));

            // 清空舊的材料並設置新的材料
            existingRecipe.getMaterials().clear();
            existingRecipe.getMaterials().addAll(toMaterials(request.materials(), existingRecipe));

            recipeRepository.save(existingRecipe);
        } catch (Exception e) {
            throw new RuntimeException("更新酒譜失敗！", e);
        }
    }

    /**
     * @param recipe_id 酒譜 ID
     * @param token     JWT，用於權限檢查
     */
    public void deleteRecipe(Integer recipe_id, String token) {
        // 從 Token 中提取用戶帳號
        String account = jwtUtil.validateToken(token).getSubject();
        Recipe recipe = recipeRepository.findById(recipe_id)
                .orElseThrow(() -> new RuntimeException("找不到該酒譜！"));
        // 如果不是管理員，檢查是否為該酒譜的擁有者
        if (!memberService.isAdmin(token) && !recipe.getMember().getAccount().equals(account)) {
            throw new RuntimeException("無權限刪除此酒譜！");
        }
        recipeRepository.deleteById(recipe_id);
    }

    /**
     * @param recipe_id 酒譜 ID
     * @return 對應的酒譜 Entity，找不到回傳 null
     */
    public Recipe getRecipe(Integer recipe_id) {
        return recipeRepository.findByRecipeId(recipe_id);
    }

    /**
     * @param keyword 中英文名稱關鍵字
     * @return 符合關鍵字的酒譜
     */
    public List<Recipe> searchRecipes(String keyword) {
        return recipeRepository.searchByKeyword(keyword);
    }

    /**
     * 依名稱取得基酒，資料庫沒有的自動新建
     *
     * @param names 基酒名稱清單
     * @return 對應的基酒 Entity 清單
     */
    private List<BaseWine> resolveBaseWines(List<String> names) {
        return names.stream()
                .map(name -> baseWineRepository.findByName(name)
                        .orElseGet(() -> {
                            BaseWine newWine = new BaseWine();
                            newWine.setName(name);
                            return baseWineRepository.save(newWine);
                        }))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * @param materials 材料清單
     * @param recipe    所屬酒譜
     * @return 對應的材料 Entity 清單
     */
    private List<Material> toMaterials(List<MaterialDTO> materials, Recipe recipe) {
        return materials.stream()
                .map(dto -> {
                    Material material = new Material();
                    material.setMaterial_name(dto.material_name());
                    material.setMaterial_quantity(dto.material_quantity());
                    material.setRecipe(recipe);
                    return material;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
